(ns tradingview.study.extract
  (:require
   [clojure.string]
   [cheshire.core :refer [parse-string generate-string]]
   [clojure.pprint :refer :all]
   [clojure.walk]))


(defn source [chart-idx pane-idx _ #_source-idx source]
  (let [{:keys [id type state points]} source
        {:keys [symbol lastUpdateTime]} state
        [a b c] points
        type (clojure.string/replace type #"LineTool" "")]
    {:chart chart-idx
     :pane pane-idx
     :id id
     :symbol symbol
     :type type
     :time-update lastUpdateTime
     ;:group (get-in t [ :ownerSource])
     ;:left (get-in t [:state :leftEnd])
     ;:right (get-in t [:state :rightEnd])
     :a-t (:time_t a)
     :a-p (:price a)
     :b-t (:time_t b)
     :b-p (:price b)
     :c-t (:time_t c)
     :c-p (:price c)}))

(defn pane [chart-idx pane-idx pane]
  (map-indexed (partial source chart-idx pane-idx) (:sources pane)))

(defn chart [chart-idx chart]
  (map-indexed (partial pane chart-idx) (:panes chart)))

(defn extract [content]
  (->> (map-indexed chart (:charts content))
       (flatten)
       ;(filter #(= (:type %) "LineToolTrendLine"))
       ;(map #(dissoc % :type))
       ))
; LineToolGannFan

(defn chart-extract [tv id]
  (let [chart (.load-chart tv 77 77 id)
        content (clojure.walk/keywordize-keys (:content chart))
        ;_ (println "content: " content)
        ]
    (extract content)))

(defn chart-extract-page [tv id]
  (println "chart extract chart-id: " id)
  (let [id (Integer/parseInt id)
        extracted (chart-extract tv id)
        ;s "AV:DAX_Index"
        s "BITMEX:XBTUSD"
        extracted (filter #(= s (:symbol %)) extracted)
        ;_ (println "extracted: " extracted)
        ]
    (with-out-str (print-table [;:id
                                ;:time-update
                                :chart
                                :pane
                                :symbol
                                :type
                                :a-t
                                :a-p
                                :b-t
                                :b-p
                                :c-t
                                :c-p] extracted))))


(comment

  (chart-extract demo.routes/tv 12630490))

