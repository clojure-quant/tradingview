(ns demo.routes
  (:require
   [ring.util.response :refer [response charset content-type resource-response]]
   [compojure.core :refer [defroutes GET]]
   [compojure.route :refer [resources files not-found]]   
   [demo.config :refer [tradingview-config!]]
   [tradingview.routes :refer [create-tradingview-routes!]]))

(def tv (:tradingview (tradingview-config!)))

(defroutes tradingview-routes
  (create-tradingview-routes! tv)
  (not-found "Bummer, not found"))
  
(defroutes routes-ext
  (resources "/" {:root "app"})
  (resources "/" {:root "public"})
  (GET "/" []
    (content-type
     (resource-response "index.html" {:root "public"})
     "text/html"))
  tradingview-routes)
