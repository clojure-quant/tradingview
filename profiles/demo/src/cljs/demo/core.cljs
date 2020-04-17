(ns demo.core
  (:require
   [reagent.dom :refer [render]]
   [api.my-host :refer [dev?]]
   [tradingview.chart :refer [tradingview-chart]]))

(def config-tradingview-demo {:feed-url "https://demo_feed.tradingview.com"
                              :storage-url "https://saveload.tradingview.com"})

(def config-dev {:feed-url "http://localhost:8000/api/tradingview"
                 :storage-url "http://localhost:8000/api/tradingviewstorage"})

(def config-prod {:feed-url "https://quant.hoertlehner.com/api/tradingview"
                  :storage-url "https://quant.hoertlehner.com/api/tradingviewstorage"})


(def config
  (atom (if (dev?)
          config-dev ; use dev when no hostname is being returned.
          config-prod)))

(defn change-config [c]
  (println "changing config to: " c)
  (reset! config
          (case c
            "dev" config-dev
            "tradingview-demo" config-tradingview-demo
            config-dev))
  (println "new config:" @config))


(defn app []
  (fn []
    [:<>
     [:select {:on-change #(change-config (.. % -target -value))}
      [:option {:value :dev} "our source"]
      [:option {:value :tradingview-demo} "demo"]]
     [:div {:style {:width 600 :height 600}}
      [tradingview-chart @config]
      [:p "config: " @config]]]))

(defn stop []
  (js/console.log "Stopping..."))

(defn start []
  (js/console.log "Starting...")
  (render [app]
          (.getElementById js/document "app")))

(defn ^:export init []
  (start))