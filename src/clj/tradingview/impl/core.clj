(ns tradingview.impl.core
  (:require
   [tradingview.interface :refer [tradingview-template]]
   [tradingview.mongo.db :as db]

   [tradingview.impl.server-features :as server-features]
   [tradingview.impl.search :as search]
   [tradingview.impl.symbol :as symbol]
   [tradingview.impl.series :as series]
   [tradingview.impl.storage :as storage]
   [tradingview.impl.template :as template]

      ;[tradingview.marks]
    ;[tradingview.quotes]
    ;[tradingview.group]
   ))

(def config
  {:mongo {:mongo-ip "127.0.0.1"
           :mongo-port 27017
           :mongo-db "seasonality"}

   :bloomberg {:path "/home/admin/quant/bbclientbin"
               :daily-cost-limit 40000}

   :web {:port 5005} ; 7000
   })

(defn tradingview! [db]
  (reify tradingview-template
    (config [this] (server-features/server-features))

    (search [this query type  exchange limit]  (search/tradingview-search-request db query type  exchange limit))
    (symbol-info [this symbol] (symbol/tradingview-symbol-info-request db symbol))

    (load-series [this symbol resolution from to] (series/tradingview-history db symbol resolution from to))

    (load-chart [this client user chart-id] (storage/load-chart db client user chart-id))
    (save-chart [this client-id user-id data] (storage/save-chart db client-id user-id data))
    (modify-chart [this client-id user-id chart-id data] (storage/modify-chart db client-id user-id chart-id data))
    (delete-chart [this client-id user-id chart-id]  (storage/delete-chart db client-id user-id chart-id))

    (load-template [this client-id user-id template-id] (template/load-template db client-id user-id template-id))
    (save-template [this client-id user-id data] (template/save-template db client-id user-id data))
    (delete-template [this client-id user-id template-id] (template/delete-template db client-id user-id template-id))))


(defn tradingview-config! []
  (let [mongo (db/connect config)
        db (:db mongo)
        tv (tradingview! db)]
    (assoc mongo :tradingview tv)))

(comment

  (def tv (tradingview-config!))
  (def template (:tradingview-template tv))

  (def template-id "5d87c9db3e4d5711b9cd0cc7")
  (.load-template template 1 1 template-id)
  (.save-template template 7 7 {:name "test" :data "contents"})
  (.demo template 7)
 ;
  )


