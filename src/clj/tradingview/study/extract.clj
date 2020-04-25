(ns tradingview.study.extract
  (:require
   [cheshire.core :refer [parse-string generate-string]]
   [clojure.pprint :refer :all]
   [clojure.walk]
   ))

;; JSON PARSER

(defn source [t]
  (let [[a b] (get-in t [:points])]
    {:type (:type t)
     :symbol (get-in t [:state :symbol])
     :id (get-in t [:id])
     :time (get-in t [:state :lastUpdateTime])
     ;:time (get-in t [:state :lastUpdateTime])
     ;:group (get-in t [ :ownerSource])

     ;:left (get-in t [:state :leftEnd])
     ;:right (get-in t [:state :rightEnd])
     :a-t (:time_t a)
     :a-p (:price a)
     :b-t (:time_t b)
     :b-p (:price b)}))

(defn pane [p]
  (->> p
       (:sources)
       (map source)))

(defn extract [content]
  (->>
   (-> content
        ;(:name)
       (:charts)
       (first)
       (:panes)
        ; (count)
       )
   (map pane)
   (flatten)
   (filter #(= (:type %) "LineToolTrendLine"))
   (map #(dissoc % :type))
    ;(print-table)
    ;(frequencies)
   ;(count)
   ))


(defn chart-extract-page [tv id]
  (println "chart extract chart-id: " id)
  (let [chart (.load-chart tv 77 77 (Integer/parseInt id))
        content (clojure.walk/keywordize-keys (:content chart))
        _ (println "content: " content)
        extracted (extract content)
        _ (println "extracted: " extracted)]
    (with-out-str (print-table extracted))))


