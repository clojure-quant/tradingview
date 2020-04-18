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

(def yyyyMMdd
  (fmt/formatter "yyyy-MM-dd"))

(defn dt2str [epoch]
  (fmt/unparse yyyyMMdd epoch))

(comment

    ; epoch conversions
  (to-epoch-no-ms (t/now))
  (to-epoch-no-ms (-> 14 t/days t/ago))
  (type (to-date 1487289600))
  (type (t/date-time 2010 10 3))

  (dt2str 1487289600)
  (c/from-long 1487289600000)

  (println yyyyMMdd)
  (fmt/unparse yyyyMMdd (t/date-time 2010 10 3))

  (server-time)

  ;
  )