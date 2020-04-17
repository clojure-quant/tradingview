(ns demo.core
  (:require
   [reagent.core :as r]
   [reagent.dom :refer [render]]
   [api.my-host :refer [dev?]]
   [tradingview.chart :refer [tradingview-chart]]))

(def config-tradingview-demo {:feed-url "https://demo_feed.tradingview.com"
                              :storage-url "https://saveload.tradingview.com"})

(def config-dev {:feed-url "http://localhost:8000/api/tradingview"
                 :storage-url "http://localhost:8000/api/tradingviewstorage"})

(def config-prod {:feed-url "https://quant.hoertlehner.com/api/tradingview"
                  :storage-url "https://quant.hoertlehner.com/api/tradingviewstorage"})

(def config-our (if (dev?)
                  config-dev ; use dev when no hostname is being returned.
                  config-prod))


(def aconfig
  (r/atom config-our))

(defn change-config! [c]
  (println "changing config to: " c)
  (reset! aconfig
          (case c
            "our" config-our
            "tradingview-demo" config-tradingview-demo
            config-dev))
  (println "new config:" @aconfig))


(defn app []
  (fn []
    [:<>
     [:select {:on-change #(change-config! (.. % -target -value))}
      [:option {:value :our} "our source"]
      [:option {:value :tradingview-demo} "demo"]]
     [:div {:style {:width 800 :height 600}}
      [tradingview-chart @aconfig]]
     [:p (str "config: " @aconfig)]]))

(defn stop []
  (js/console.log "Stopping..."))

(defn start []
  (js/console.log "Starting...")
  (render [app]
          (.getElementById js/document "app")))

(defn ^:export init []
  (start))