
(ns demo.config
  (:require
   [tradingview.mongo.db :as db]
   [tradingview.impl.core :refer [tradingview!]]))

(def config
  {:mongo {:mongo-ip "127.0.0.1"
           :mongo-port 27017
           :mongo-db "seasonality"}})

(defn tradingview-config! []
  (let [mongo (db/connect config)
        db (:db mongo)
        tv (tradingview! db)]
    (assoc mongo :tradingview tv)))
