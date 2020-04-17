(ns tradingview.impl.time
  (:require
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   [clj-time.format :as fmt]))

; date conversion

(defn to-date- [epoch-no-ms]
  (c/from-long (* epoch-no-ms 1000)))

(defn to-epoch-no-ms- [date]
  (int (/ (c/to-long date) 1000)))

(defn server-time []
  (to-epoch-no-ms- (t/now)))