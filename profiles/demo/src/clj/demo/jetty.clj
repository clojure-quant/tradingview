
(ns demo.jetty
  "web server for Ring handlers."
  (:import java.net.BindException
           #_org.mortbay.log.Logger)
  (:require
   [ring.adapter.jetty :as jetty] ; add this require
   [ring.middleware.stacktrace :as st]))

(defn demo-handler
  "A very basic demo-handler that renders hello world"
  [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello world"})


(defn run-jetty-blocking
  [handler port]
  (jetty/run-jetty handler {:port port}))


(defonce jetty-server (atom nil))

(defn- server-connector []
  (first (.getConnectors @jetty-server)))

(defn server-port []
  (.getPort (server-connector)))




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
     st/wrap-stacktrace)
    {:port port, :join? false})))


(defn start-jetty [handler & [port]]
  (stop-jetty)
  (reset! jetty-server
          (if port
            (start-server handler port)
            (start-server handler)))
  (println "Started web server on port" (server-port))
  nil)


(comment
  (run-jetty-blocking demo-handler 9999)

  (start-jetty demo-handler 9999)
  (stop-jetty)

  (+ 1 2))