(ns tradingview.study.hack-routes
  (:require
   [clojure.string :as str]
   [cheshire.core :refer [parse-string]]
   [ring.util.response :as response]
   [ring.util.http-response :refer [ok]]
   [compojure.api.sweet :as sweet]
   [clojure.java.io :refer [input-stream]])
  (:import [java.io File])
  (:import [java.util.zip GZIPInputStream]))

(defn unzip [file-name]
  (with-open [in (GZIPInputStream. (input-stream file-name))]
    (slurp in)))

(defn get-zip-contents
  "receives an attachment structure and returns the uncompressed content

   {:size 88855,
    :tempfile #<File /var/folders/hq/ym6xf479.tmp>,
    :content-type application/pdf,
    :filename slides.pdf}   "
  [{:keys [filename size tempfile content-type]}]
  (let [_ (println "uncompressing received zip file" tempfile)
        data-raw (unzip tempfile)
        _ (spit "/tmp/tv.json" data-raw)]
    (println "unzipped " filename " size:" size)
    data-raw))


(defn tvhack-api-routes [tv]
  (sweet/context "/tvhack" [] :tags ["tvhack"]
    (sweet/POST "/dump" {params :params}
      (let [file-params (:content params)
            tv-params (dissoc params :content)
            {:keys [id name symbol resolution]} tv-params
            id (or (when (not (nil? id)) (Integer/parseInt id)) id 77)
            name (or name "unknown")
            symbol (or symbol "unknown")
            ;resolution (or resolution "1D")
            ]
        (println "saving tradingview chart-id " id " name: " name " symbol: " symbol)
        (->> (get-zip-contents file-params)
             (parse-string)
             (assoc tv-params :content)
        ;     #(dissoc % :savingToken :id)
             (.modify-chart tv 77 77 id))
        (response/redirect "https://www.tradingview.com/savechart/bongistan")))))
