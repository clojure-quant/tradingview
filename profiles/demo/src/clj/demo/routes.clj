(ns demo.routes
  (:require
   [compojure.core :refer [defroutes GET]]
   [ring.util.response :refer [response charset content-type resource-response]]
   [compojure.core :refer [defroutes GET]]
   [compojure.route :refer [resources files not-found]]
   [demo.config :refer [tradingview-config!]]
   [tradingview.routes :refer [create-tradingview-routes!]]))

(let [c (tradingview-config!)
      tv (:tradingview c)]
  ;(def routes
  ;  (create-tradingview-routes! tv))
  (defroutes routes
    ;(GET "/study" [] (response/response (study-ant)))
    (create-tradingview-routes! tv)))


(defroutes routes-ext
 ;(GET "/" [] (resource-response "index.html" {:root "public"}))
  (resources "/" {:root "app"})
  (resources "/" {:root "public"})

  (GET "/" []
    (println "rendering main")
    ;(content-type 
    (resource-response "index.html" {:root "public"})
  ;  "text/html")
    )

  routes

  ;(files "files/" {:root "."})
  (not-found "Bummer, not found"))
