(ns tradingview.json
  (:require
    [clojure.string :as str]
    [clojure.java.io :as io]
    [json-html.core :refer :all]
    [hiccup.page :refer :all]
    [clojure.pprint :refer :all]
    ))

(defn convert-to-html [file-name]
  (spit "formatted.html"
     (html5
      [:head [:style (-> "json.human.css" clojure.java.io/resource slurp)]]
        (json->html (slurp file-name) )))  )

;; JSON PARSER

(defn source [t]
   (let [ [a b] (get-in t [:points]) ]
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
     :b-p (:price b)


    })
  )

(defn pane [p]
  (->> p
     (:sources)
     (map source)
  ))



(defn parse-text [text]
  (->>
    (-> text
        (cheshire.core/parse-string true)
        ;(:name)
        (:charts)
        (first)
        (:panes)
        ; (count)
        )
    (map pane)
    (flatten)
    (filter #(= (:type %) "LineToolTrendLine" ))
    (map #(dissoc % :type))
    ;(print-table)
    ;(frequencies)
   ;(count)
    ))

(defn parse-file [file-name]
  (-> file-name
      (slurp)
      (parse-text)))

(comment

  (parse-file "resources/ant.json")
  (print-table (parse-file "resources/ant.json"))
  (future (convert-to-html "resources/tradingview.json"))
  )
