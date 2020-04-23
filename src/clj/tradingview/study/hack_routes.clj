(ns tradingview.study.hack-routes
  (:require
   [clojure.string :as str]
   [ring.util.response :as response]
   [ring.util.http-response :refer [ok]]
   [compojure.core :refer [defroutes GET]]
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


(def json-fn "resources/tvhack.json")

(defn tradingview-status []
  (-> json-fn
      (slurp)
      ;(parse-string)
      ))





(defn tvhack-routes [tv]
  (sweet/context "/tvhack" [] :tags ["tvhack"]

    (sweet/GET "/data" []
      :summary "gets tradingview hack data"
      (ok {:result (tradingview-status)}))

    (sweet/POST "/dump" {params :params}
      (let [file-params (:content params)
            tv-params (dissoc params :content)
            {:keys [id name symbol resolution]} tv-params
            id (or id 77)
            name (or name "unknown")
            symbol (or symbol "unknown")
            ;resolution (or resolution "1D")
            ]
        (println "tradingview dump received, params: " params)
        ;(.save-template tv 77 77 {:name "test" :content (get-zip-contents file-params)})
        (println "saving tradingview chart-id " id " name: " name " symbol: " symbol)
        (.modify-chart tv 77 77 id
                       (assoc tv-params :content  (get-zip-contents file-params)))
        (response/redirect "https://www.tradingview.com/savechart/bongistan")))

   ;(sweet/GET "/tvdata" [] (response/response (tradingview-status)))
    ))
