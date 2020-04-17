(ns web.views.views
  (:require [clojure.string :as str]
            [hiccup.core :as h]
            [hiccup.page :as page]
            [ring.util.anti-forgery :as util]
            [ring.util.codec :as codec]

            [db.instruments :as instruments]

            [seasonal.stats :as stats]
            [seasonal.trading :as trading] ))


(defn app-router-page []
  (page/html5
    [:head
      [:title "clojureQuant"]

     (page/include-js "https://code.highcharts.com/modules/exporting.js")
     (page/include-js "https://code.highcharts.com/highcharts.js")
     [:script "highcharts.core.main();"]

  
     ;(page/include-css "/css/styles.css")
     (page/include-css "/css/financials.css")
     (page/include-js "js/compiled/example.js")
     ]

    [:div#root]

    [:script "bongo.core.start();"]
    ))
