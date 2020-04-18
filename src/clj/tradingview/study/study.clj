(ns tradingview.study.study
  (:require
   [clojure.java.io :as io]
   [cheshire.core :refer [parse-string]]

   [json-html.core :refer [json->html]]
   ;[hiccup.core :refer :all]
   [hiccup.page :as page]
   [clojure.pprint :refer :all]))


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

(defn parse-text [text]
  (->>
   (-> text
       (parse-string true)
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

(defn parse-file [file-name]
  (-> file-name
      (slurp)
      (parse-text)))

(defn study-raw [file-name]
  (page/html5
   [:head
    [:title "tradingview study analysis"]
    (page/include-css "json.human.css")]
   (json->html (slurp file-name))))

(defn study-raw-ant []
  (study-raw "profiles/demo/resources/ant.json"))

(defn study-ant []
  (with-out-str (print-table (parse-file "profiles/demo/resources/ant.json"))))

(comment

  (parse-file "profiles/demo/resources/ant.json")
  (print-table (parse-file "profiles/demo/resources/ant.json"))
  (study-raw "profiles/demo/resources/ant.json")

  ;
  )
