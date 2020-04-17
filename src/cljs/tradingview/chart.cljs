(ns tradingview.chart
  (:require
    [reagent.core :as r]
    [comp.loader]
    [api.my-host :refer [dev?]])
)

(def config-demo
  {:data "https://demo_feed.tradingview.com"
   :storage "https://saveload.tradingview.com";
  })

(def config-dev
  {:data "http://localhost:5005/api/tradingview"
   :storage "http://localhost:5005/api/tradingviewstorage";
  })

(def config-dev-rt
  {:data "http://localhost:5005/api/tradingviewrt"
   :storage "http://localhost:5005/api/tradingviewstorage";
  })


(def config-prod
  {:data "https://quant.hoertlehner.com/api/tradingview"
   :storage "https://quant.hoertlehner.com/api/tradingviewstorage";
  })


(def config
  (if (dev?)
      config-dev-rt ; use dev when no hostname is being returned.
      config-prod))



(def chart-props {
    :containerId "tradingview_container"
  })

(defn space-to-underscore [str]
  (if (nil? str) str
      (clojure.string/replace str " " "_")))


(defn init-chart [symbol]
  (println "TradingViewChart.Init: " symbol)
  (js/window.MyTradingView.initChart (:containerId chart-props) (:data config) (:storage config) (space-to-underscore symbol))
   )

(defn set-symbol [symbol]
  (println "TradingViewChart.ChangeSymbol: " symbol)
  (.setSymbol js/window.MyTradingView (space-to-underscore symbol) "D"))

(defn remove-chart []
  (println "TradingViewChart.Init: " symbol)
  (js/window.MyTradingView.removeChart)
   )


(defn chart [symbol]
  (let [state (r/atom {})] ;; you can include state
    (r/create-class {
      :display-name  "tradingviewWrapper"      ;; for more helpful warnings & errors
      :reagent-render ;; let it re-render when the arguments change
        (fn [symbol]
           [:div {:id (:containerId chart-props) }]) ;; let it re-render when the arguments change
      :component-did-mount (fn [this]
         (println "TradingViewChart.ComponentDidMount")
         (init-chart symbol)) ;;component is mounted into the DOM
      ; :component-did-mount (fn [comp]  (.log js/console "ComponentDidMount")

     ;:component-will-receive-props  (fn [this]
    ;   (let [ [_ s] (r/argv this)]
    ;    (.log js/console "CompWillReceiveProps: " s)
    ;    (set-symbol s)
    ;    ))
    :component-did-unmount (fn [this]
      (println "TradingViewChart.ComponentDid-UN-Mount")
      (remove-chart))

     :component-did-update  ;;called just after re-rendering.
      (fn [this]
         (let [[_ s] (r/argv this) ]
           (.log js/console "TradingViewChart.ComponentDidUpdate " s)
           (set-symbol s)
           ))
})))


;(defn chart-with-js []
;  [comp.loader/js-loader {
;    :scripts {
;      #(exists? js/Stripe) "https://js.stripe.com/v2/"
;      #(exists? js/TradingView) "/charting_library.min.js"
;    }
;    :loading [:h1 "Loading Scripts..."]
;    :loaded [:h1 "Loaded Finished!"]
;    ;[chart]
;    }]
;  )
