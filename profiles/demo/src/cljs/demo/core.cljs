(ns demo.core
  (:require
  ; [cljs.pprint]
   [reagent.dom :refer [render]]))

(def app
  [:<>
   [:h1 "Tradingview Demo"]])

(defn stop []
  (js/console.log "Stopping..."))

(defn start []
  (js/console.log "Starting...")
  (render app
            (.getElementById js/document "app")))

(defn ^:export init []
  (start))