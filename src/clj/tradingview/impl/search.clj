(ns tradingview.impl.search
  (:require
   [monger.core :as mg]
   [monger.collection :as mc]
   [monger.joda-time]
   [monger.query :refer [with-collection paginate fields find sort]]
   
 [tradingview.impl.symbol :refer [tradingview-symbol-info]]
   ))


(defn search-instrument [db query category exchange limit]
  (let [query {:$or [{:name {:$regex query :$options "i"}}
                     {:symbol {:$regex query :$options "i"}}]}

        query (if (= category "") query (assoc query :category category))
        query (if (= exchange "") query (assoc query :exchange exchange))]
    ;(mc/find-maps db "instruments" query
    ;; find top 10 scores
    (with-collection db "instruments"
      (find query)
      ;(fields [:symbol :name :exchange :category]) ;
      (fields {:symbol-only 1 :symbol 1 :name 1 :exchange 1 :category 1 :_id 0})
      (sort {:name 1})
      (paginate :page 1 :per-page limit))))


;https://demo_feed.tradingview.com/search?query=B&type=stock&exchange=NYSE&limit=10
;[{"symbol":"BLK","full_name":"BLK","description":"BlackRock, Inc.","exchange":"NYSE","type":"stock"},
;  {"symbol":"BA","full_name":"BA","description":"The Boeing Company","exchange":"NYSE","type":"stock"}]


(defn if-empty-default-value- [field default-value]
  (if (nil? field)
    default-value
    (if (= "" field)
      default-value
      field)))


(defn search-conversion [row]
  (-> row
      (tradingview-symbol-info)
      (clojure.set/rename-keys {:tradingview :full_name
                                :display :symbol})))


(defn convert-search- [rows]
  (map search-conversion rows))




(defn tradingview-search-request
  "searches by name/symbol, gives list of symbols"
  [db query type exchange limit]
  (-> query
      (search-instrument db (if-empty-default-value- type "")  (if-empty-default-value- exchange "") limit)
      (convert-search-)))


(comment
    ; SEARCH endpoints
  (search "C" "Index" "" 2))
