(ns tradingview.impl.time
  (:require
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   [clj-time.format :as fmt]))

; date conversion

(defn to-date [epoch-no-ms]
  (c/from-long (* epoch-no-ms 1000)))

(defn to-epoch-no-ms [date]
  (int (/ (c/to-long date) 1000)))

(defn server-time 
  "tradingview server has to publish a server time"
  []
  (to-epoch-no-ms (t/now)))


(defn date->ui-int
  "date => integer YYYYMMDD"
  [date]
  (let [day (t/day date)
        hour (t/hour date)
        min (t/minute date)
        sec (t/second date)]
    (+ (* day 1000000) (* hour 10000) (* min 100) sec)))