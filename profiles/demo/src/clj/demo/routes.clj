(ns demo.routes
  (:require
   [demo.config :refer [tradingview-config!]]
   [tradingview.routes :refer [create-tradingview-routes!]]))


(let [c (tradingview-config!)
      tv (:tradingview c)
      ]
  (def routes
    (create-tradingview-routes! tv)))
