(ns series.csv
  (:require
   [clojure.data.csv :as csv]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [clj-time.core :as t]
   [clj-time.format :as fmt]
   [taoensso.tufte :as tufte :refer (defnp p profiled profile)]))

(def base-directory "../DAILY/")



(defn remove-nil [list]
  (p :csv-remove-nil
     (remove #(nil? (:PX_LAST %)) list)))

(defn remove-empty [list]
  (p :csv-remove-empty
     (remove #(= (:PX_LAST %) "") list)))



(defn filter-start-date [series date-start]
  (let [is-after (fn [bar] (t/after? (:date bar) date-start))]
    (filter is-after series)))


(defn filter-since-1990 [series]
  (p :csv-filter (doall (filter-start-date series (t/date-time 1989 12 31)))))

(defn load-series [symbol]
  (p :csv-load (doall (load-csv- base-directory symbol))))


(defn available-range [symbol]
  (let [data (load-csv- base-directory symbol)
        start (first data)
        end (last data)]
    {:start (:date start)  :end (:date end)}))


; UNIVERSE *****************************************

(defn symbols-with-timeseries []
  (->> (mapv str (filter #(.isFile %) (file-seq (clojure.java.io/file base-directory))))
        ;(map str/split)
       (map #(str/split % #"/"))
       (map last)
       (map #(str/split % #".csv"))
       (map first)
       (vec)))




;(with-open [reader (io/reader "in-file.csv")]
;  (doall
;    (csv/read-csv reader)))


(comment


  (clojure.string/escape "I want 1 < 2 as HTML, & other good things."
                         {\< "&lt;", \> "&gt;", \& "&amp;"})


  (def symbol-
  ;"DAX Index"
    "MCD UN Equity"
  ;"ADS GY Equity"
    )

  (def test-
    (load-series symbol-))

; evaluate first 5 elements of the lazy list
  (take 5 test-)
  (doall test-)

  (take 5 (filter-since-1990 test-))

  (profile {} (p :csv (do (load-series "AAPL US Equity") nil)))

  (count (load-series "AAPL US Equity"))


  (profile {} (p :slurp (doall (slurp "../DAILY/AAPL US Equity.csv"))))

  (available-range "MSFT US Equity")

; UNIVERSE *****************

  (symbols-with-timeseries)
  (count (symbols-with-timeseries))

  (let [x (symbols-with-timeseries)]
    (for [s x]
      (println "* " s)))

  (map println (symbols-with-timeseries))
  (type (symbols-with-timeseries))

; ***************************************************************
  )
