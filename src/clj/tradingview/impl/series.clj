(ns tradingview.impl.series
  (:require
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   [clj-time.format :as fmt]
   [tradingview.impl.time :refer [to-epoch-no-ms to-date]]
   [tradingview.mongo.series :refer [available-range load-series]]))


; history

(def empty-result- {:t [] :o [] :h [] :l [] :c [] :v []})

;; Remark: Bar time for daily bars should be 00:00 UTC and is expected to be a trading day (not a day when the session starts). Charting Library aligns the time according to the Session from SymbolInfo.
;; Remark: Bar time for monthly bars should be 00:00 UTC and is the first trading day of the month.


(defn field-or-default [field bar default]
  (let [v (field bar)]
    (if (nil? v)
      default
      v)))

(defn add-bar- [result bar]
  (let [c (:close bar)]
    {:t (conj (:t result) (to-epoch-no-ms (:date bar)))
     :o (conj (:o result) (field-or-default :open bar c))
     :h (conj (:h result) (field-or-default :high bar c))
     :l (conj (:l result) (field-or-default :low bar c))
     :c (conj (:c result) c)
     :v (conj (:v result) (field-or-default :volume bar c))}))

(defn convert-bars [series]
  (reduce add-bar- empty-result- series))


; nextTime is the time of the closest available bar in the past.


(defn series-result- [series]
  (->  series
       convert-bars
       (assoc :s "ok")))


; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1567457308&to=1568321308
; {"s":"no_data",
; "nextTime":1522108800
;}

; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1487289600&to=1488499199
; {
;  "t":[1487289600,1487635200,1487721600,1487808000,1487894400,1488153600,1488240000,1488326400,1488412800],
;  "o":[135.1,136.23,136.43,137.38,135.91,137.14,137.08,137.89,140],
;  "h":[135.83,136.75,137.12,137.48,136.66,137.435,137.435,140.15,140.2786],
;  "l":[135.1,135.98,136.11,136.3,135.28,136.28,136.7,137.595,138.76],
;  "c":[135.72,136.7,137.11,136.53,136.66,136.93,136.99,139.79,138.96],
;  "v":[22198197,24507156,20836932,20788186,21776585,20257426,23482860,36414585,26210984],
; "s":"ok"}


(defn interval [resolution]
  (case resolution
    "D"  :daily
    "1D" :daily
    "M"  :monthly
    "1M" :monthly))


(def yyyyMMdd-
  (fmt/formatter "yyyy-MM-dd"))

(defn dt2str [epoch]
  (fmt/unparse yyyyMMdd- epoch))


(defn no-data [db-end-date]
  {:s "no_data"
    ;:t [] :o [] :h [] :l [] :c [] :v []
    ; :nextTime  (to-epoch-no-ms- db-end-date)
   })


(defn tradingview-series [db symbol resolution from to]
  (let [dt-from (to-date from)
        dt-to (to-date to)
        frequency (interval resolution)
        db-data (available-range db symbol)
        _ (println "tradingview-history " symbol frequency (dt2str dt-from) "-" (dt2str dt-to))
        series (load-series db symbol frequency dt-from dt-to)]
    (if (= 0 (count series))
      (no-data (:end db-data))
      (series-result- series))))



; s: status code. Expected values: ok | error | no_data
; errmsg: Error message. Should be present only when s = 'error'
; t: Bar time. Unix timestamp (UTC)
; c: Closing price
; o: Opening price (optional)
; h: High price (optional)
; l: Low price (optional)
; v: Volume (optional)
; nextTime: Time of the next bar if there is no data (status code is no_data) in the requested period (optional)


(comment

  (println (> 0 0))

  ; mongodb tests
  (load-symbol "DAX Index" [:symbol :name])
  (mongo.series/load-series-partial "DAX Index", (-> 14 t/days t/ago) (t/now))
  (mongo.series/load-series-partial "BP/ LN Equity", (-> 14 t/days t/ago) (t/now))

  ; epoch conversions
  (to-epoch-no-ms- (t/now))
  (to-epoch-no-ms- (-> 14 t/days t/ago))
  (type (to-date- 1487289600))
  (type (t/date-time 2010 10 3))

  (epoch2str 1487289600)
  (c/from-long 1487289600000)

  (println yyyyMMdd-)
  (fmt/unparse yyyyMMdd- (t/date-time 2010 10 3))

  ; HISTORY endpoint
  ;https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1567457308&to=1568321308
  (history "DAX Index" "D" 1567165656 1568375219)
  (history "BP/ LN Equity" "D" 1567165656 1568375219)

  (server-time)

  (interval "D"))
