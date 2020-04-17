(ns tradingview.mongo.series
  (:require
   [clj-time.core :as t]
   [monger.core :as mg]
   [monger.collection :as mc]
   [monger.operators :refer :all]
   [monger.joda-time]))

(defn remove-nil [series]
  (remove #(nil? (:close %)) series))

(defn remove-empty [series]
  (remove #(= (:close %) "") series))


(defn sanitize-series [series]
  (->> series
       (map (fn [row]
              (-> row
                  (clojure.set/rename-keys
                   {:PX_LAST :close
                    :PX_HIGH :high
                    :PX_LOW :low
                    :PX_OPEN :open
                    :PX_VOLUME :volume}))))
       (remove-nil)
       (remove-empty)))


(defn save-series [db symbol series]
  (mc/update db "series"
             {:symbol symbol}
             {:symbol symbol
              :series series
              :save-date (.toDateMidnight (t/now))
              :start (:date (first series))
              :end (:date (last series))}
             {:upsert true}))

(defn append-series [db symbol series]
  (mc/update db "series"
             {:symbol symbol}
             {:$push {:series {:$each series}}
              :$set {:end (:date (last series))
                     :save-date (.toDateMidnight (t/now))}}))

(defn insert-series [db symbol series]
  (mc/update db "series"
             {:symbol symbol}
             {:$push {:series {:$each series
                               :$position 0}}
              :$set {:start (:date (first series))
                     :save-date (.toDateMidnight (t/now))}}))



;{$gte ["bongo.date" first]}

(defn load-series-partial [db symbol start end]
  (:series (first (mc/aggregate db "series" [{:$match {:symbol symbol}}
                                             {:$project
                                              {:_id 0
                                               :series {:$filter {:input "$series"
                                                                  :as "series"
                                                                  :cond {"$and" [{"$gte" ["$$series.date" start]}
                                                                                 {"$lte" ["$$series.date" end]}]}}}}}]))))



(defn load-series
  ([db symbol]
   (as-> {:symbol symbol} x
     (mc/find-one-as-map db "series" x)
       ;(dissoc x :_id) ; dont give back the mongo-db-id (this fucks up json serialization)
     (:series x)
     (sanitize-series x)))
  ([db symbol _ start end] ; todo: frequency conversion not implemented
   (as-> {:symbol symbol} x
     (mc/find-one-as-map db "series" x)
       ;(dissoc x :_id) ; dont give back the mongo-db-id (this fucks up json serialization)
     (:series x)
     (sanitize-series x))))


(defn available-range [db symbol]
  (if (vector? symbol)
    (mc/find-maps db "series" {:symbol {:$in symbol}} {:symbol 1 :start 1 :end 1 :save-date 1 :_id 0})
    (mc/find-one-as-map db "series" {:symbol symbol} {:symbol 1 :start 1 :end 1 :save-date 1 :_id 0})))

(defn symbols-with-timeseries
  ([db]
   (symbols-with-timeseries db {}))
  ([db start end]
   (symbols-with-timeseries db {:start {:$lte start}
                             :end {:$gte end}}))
  ([db query]
   (mc/find-maps db "series" query {:symbol 1} {:symbol 1 :start 1 :end 1 :save-date 1})))


(defn change-symbol [db symbol-old symbol-new]
  (mc/update db "series"
             {:symbol symbol-old}
             {:$set {:symbol symbol-new}}
             {:upsert false}))



(comment

  (append-series "DAX Index" [{:close -1} {:close -2 :date (t/now)}])

  (insert-series "ATX Index" [{:close -1 :date (t/date-time 1951 6 1)}
                              {:close -2 :date (t/date-time 1951 6 1)}])


  (symbols-with-timeseries)

  (available-range "BP/ LN Equity")
  (available-range ["BP/ LN Equity" "DAX Index" "ATX Index"])


  (->> "BP/ LN Equity"
    ;"DAX Index"
    ; "EBS AV Equity"
       (load-series)
       (take 5)
       (println)
    ;(count)
       ))
