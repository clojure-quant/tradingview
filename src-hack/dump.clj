(ns tradingview.dump
  (:use [compojure.core]
        [ring.middleware.params]
        [ring.middleware.multipart-params]
        [ring.adapter.jetty]
        [hiccup.core]
        [clojure.java.io]
        [cheshire.core :refer :all]
        [tradingview.json :refer [parse-text]]
        )
  (:import [java.io File]))


(defn unzip [file-name]
  (with-open [in (java.util.zip.GZIPInputStream.
                  (clojure.java.io/input-stream
                   file-name))]
    (slurp in))
  )


(comment
  {:description "bitcoin halfing cycles"
    :content {:filename "blob.gz"
              :content-type "application/gzip"
              :tempfile "#object[java.io.File 0x2b43b9b9 /tmp/ring-multipart-579299764763992157.tmp]"
              :size 109358}
      :savingToken "0.9223671831378418"
      :listed_exchange "BITFINEX"
      :symbol "BITFINEX:BTCUSD"
      :name "bitcoin halfing cycles"
      :is_realtime 1
      :publish_request_id "5pwlao44yl3"
      :short_name "BTCUSD"
      :resolution "1D"
      :legs [{:symbol "BITFINEX:BTCUSD" :pro_symbol "BITFINEX:BTCUSD"}]
      :id 12630490
      :exchange "BITFINEX"
      :symbol_type "bitcoin"}
)


; {:size 88855,
;  :tempfile #<File /var/folders/hq/ym6xf479.tmp>,
; :content-type application/pdf,
;  :filename slides.pdf}

(def json-fn "resources/tvhack.json")

(defn tradingview-status []
  (-> json-fn
      (slurp)
      (parse-string)))


(defn upload-file [file]
  (let [file-name (:filename file)
        size (:size file)
        content-type (:content-type file)
        actual-file (:tempfile file)
        _ (println "uncompressing " actual-file)
        json-in (unzip actual-file)
        converted (parse-text json-in)
        _ (spit json-fn (generate-string {:data converted})) ; saves data as json
        ]

    (do
      ;(copy actual-file (File. (format "/Users/milinda/Desktop/%s" file-name)))
      (println file-name " size:" size "@" actual-file)
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (html [:h1 file-name]
                   [:h1 size]
                 )}
       )))

(comment

   (tradingview-status)

  )
