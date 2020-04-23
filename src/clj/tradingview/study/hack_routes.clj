(ns tradingview.study.hack-routes
  (:require
   [clojure.string :as str]
   [ring.util.response :as response]
   [ring.util.http-response :refer [ok]]
   
   [schema.core :as s]
   [compojure.core :refer [defroutes GET]]
   [compojure.api.sweet :as sweet]
   [clojure.java.io :refer [input-stream]]
   )
  (:import [java.io File])
  (:import [java.util.zip GZIPInputStream])

  )

(defn unzip [file-name]
  (with-open [in (java.util.zip.GZIPInputStream.
                  (input-stream
                   file-name))]
    (slurp in)))


(defn upload-file [file]
  (let [file-name (:filename file)
        size (:size file)
        ;content-type (:content-type file)
        actual-file (:tempfile file)
        _ (println "uncompressing " actual-file)
        json-in (unzip actual-file)
        _ (println "json-in: " json-in)
        ;converted (parse-text json-in)
        data (slurp json-in)
        _ (spit "/tmp/tv.json" data) ; (generate-string #_{:data converted})) ; saves data as json
        ]
    (println "uploaded " file-name " size:" size "@" actual-file)))


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
   :symbol_type "bitcoin"})

; {:size 88855,
;  :tempfile #<File /var/folders/hq/ym6xf479.tmp>,
; :content-type application/pdf,
;  :filename slides.pdf}

(def json-fn "resources/tvhack.json")

(defn tradingview-status []
  (-> json-fn
      (slurp)
      ;(parse-string)
      ))


(def tvhack-routes
  (sweet/context "/tvhack" [] :tags ["tvhack"]

    (sweet/GET "/data" []
      :summary "gets tradingview hack data"
      (ok {:result (tradingview-status)}))

    ;(sweet/GET "/dump" [] handle-dump)
    ;(sweet/POST "/dump" [] handle-dump)
    ;

    (sweet/POST "/dump" {params :params}
      (let [file (:content params)]
        (println "dump-tradingview params: " params)
        (upload-file file)
        (response/redirect "https://www.tradingview.com/savechart/bongistan")))

   ;(sweet/GET "/tvdata" [] (response/response (tradingview-status)))
    ))
