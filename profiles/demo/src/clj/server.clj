(ns web.server
  "Development web server for Ring handlers."
  (:import java.net.BindException
    #_ org.mortbay.log.Logger )
  (:require
   [ring.adapter.jetty :as jetty] ; add this require
   [ring.middleware.stacktrace :as st]
   [mount.core :as mount :refer [defstate]]
   [web.app]
  ))

(defn run-jetty-blocking
  [handler port]
  (jetty/run-jetty handler {:port port}))


(defonce jetty-server (atom nil))

(defn stop-jetty
  "Stop the server started by the serve macro."
  []
  (when @jetty-server
    (.stop @jetty-server)
    (println "Stopped web server")))

(defn- try-ports [func ports]
  (try (func (first ports))
       (catch BindException ex
         (if-let [ports (next ports)]
           (try-ports func ports)
           (throw ex)))))

(def suitable-ports (range 3000 3011))

(defn- start-server
  ([handler]
   (try-ports #(start-server handler %) suitable-ports))
  ([handler port]
   (jetty/run-jetty
     (->
       handler
       st/wrap-stacktrace
       )
     {:port port, :join? false})))


(defn start-jetty [handler & [port]]
  (stop-jetty)
  (reset! jetty-server
          (if port
            (start-server handler port)
            (start-server handler)))
  (println "Started web server")
  nil)

(defn demo-handler
    "A very basic demo-handler that renders hello world"
    [request]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body "Hello world"})

(defn print-web-dev-help []
  "To Run Dev Web Server:  (web.server/start-jetty web.app/app 5005)")

(defstate webserver
        :start (identity nil)
        :stop #(identity nil))


(comment

    ; TEST WEB SERVER:
    ; lein repl
    ; (mount.core/start)
    ; (load "/web/server")
    ; (load "/web/app")
    ; (start-jetty web.app/app 12345)
(mount.core/start)

    (run-jetty-blocking demo-handler 9999)
    (run-jetty-blocking web.app/app 5005)

    (web.server/start-jetty demo-handler 9999)
    (web.server/start-jetty web.app/app 5005)
    (web.server/stop-jetty)

    (clojure-version)

)
