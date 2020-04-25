(ns tradingview.impl.core
  (:require
   [tradingview.interface :refer [tradingview-template]]

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

(defn tradingview! [db]
  (reify tradingview-template
    (config [this] server-features/server-features)

    (search [this query type  exchange limit]  (search/search db query type exchange limit))
    (symbol-info [this symbol] (symbol/tradingview-symbol-info-request db symbol))

    (load-series [this symbol resolution from to] (series/tradingview-series db symbol resolution from to))

    (chart-list [this client user] (storage/chart-list db client user))
    (load-chart [this client user chart-id] (storage/load-chart db client user chart-id))
    (save-chart [this client-id user-id data] (storage/save-chart db client-id user-id data))
    (modify-chart [this client-id user-id chart-id data] (storage/modify-chart db client-id user-id chart-id data))
    (delete-chart [this client-id user-id chart-id]  (storage/delete-chart db client-id user-id chart-id))

    (load-template [this client-id user-id template-id] (template/load-template db client-id user-id template-id))
    (save-template [this client-id user-id data] (template/save-template db client-id user-id data))
    (delete-template [this client-id user-id template-id] (template/delete-template db client-id user-id template-id))))





