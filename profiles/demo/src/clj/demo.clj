(ns demo
  (:require
   [tradingview.impl.core :refer [tradingview-config!]]
   [tradingview.routes :refer [create-tradingview-routes!]]))


(let [tv (tradingview-config!)]
  (def routes
    (create-tradingview-routes! tv)))
