(ns comp.loader
  (:require [reagent.core :as reagent]
            [goog.net.jsloader :as jsl]
            [goog.html.legacyconversions :as conv]
            [cljs.core]
            ))


(defn filter-loaded [scripts]
  (reduce (fn [acc [loaded? src]]
            (if (loaded?) acc (conj acc src)))
          []
          scripts))

(defn js-loader
  "Load a supplied list of Javascript files and render a component
   during loading and another component as soon as every script is
   loaded.

   Arg map: {:scripts {loaded-test-fn src}
             :loading component
             :loaded component}"
  [{:keys [scripts loading loaded]}]
  (let [loaded? (reagent/atom false)]
    (reagent/create-class
     {:component-did-mount
        (fn [_]
         (let [not-loaded (clj->js (filter-loaded scripts))]
              (.then (jsl/safeLoadMany (map conv/trustedResourceUrlFromString not-loaded))
                     #(do (js/console.log "Loaded:" not-loaded)
                     (reset! loaded? true)))))
      :reagent-render
        (fn [{:keys [scripts loading loaded]}]
           (if @loaded? loaded loading))})))

(comment

  (comp.loader/filter-loaded {
    #(exists? js/Stripe) "https://js.stripe.com/v2/"
    ;(exists? js/TradingView) "/charting_library.min.js"
  })


  )
