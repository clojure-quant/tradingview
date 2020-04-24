(ns demo.routes
  (:require
   [compojure.core :refer [defroutes GET]]
   [ring.util.response :as response]
   [demo.config :refer [tradingview-config!]]
   [tradingview.routes :refer [create-tradingview-routes!]]
))


(let [c (tradingview-config!)
      tv (:tradingview c)]
  ;(def routes
  ;  (create-tradingview-routes! tv))
  (defroutes routes
    ;(GET "/study" [] (response/response (study-ant)))
    (create-tradingview-routes! tv)))
