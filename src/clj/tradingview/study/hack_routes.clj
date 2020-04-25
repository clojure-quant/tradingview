(ns tradingview.study.hack-routes
  (:require
   [cheshire.core :refer [parse-string]]
   [ring.util.response :refer [response redirect]]
   [ring.util.http-response :refer [ok]]
   [compojure.core :refer [routes GET]]
   [compojure.api.sweet :as sweet]
   [clojure.java.io :refer [input-stream]]
   [tradingview.study.extract :refer [chart-extract-page]]
   [tradingview.study.views :refer [chart-list-page hacked-chart-json-visual-page]])
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

(defn tvhack-ui-routes [tv]
  (routes
    (GET "/hacked-chart-list" []
         (chart-list-page tv))
    (GET "/hacked-chart-json-visual" [id]
         (hacked-chart-json-visual-page tv id))
    (GET "/hacked-chart-extract" [id]
         (response (chart-extract-page tv id)))))


(defn chart-json [tv id]
  (println "chart-json chart-id: " id)
  (let [chart (.load-chart tv 77 77 id)
        json (:content chart)
        ;json-str (generate-string (:content chart))
        ;_ (println "json: " json-str)
        ]
    json))


(defn tvhack-api-routes [tv]
  (sweet/context "/tvhack" [] :tags ["tvhack"]

    (sweet/GET "/json" []
      :query-params [id :- Long]
      (ok (chart-json tv id)))

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
        (redirect "https://www.tradingview.com/savechart/bongistan")))))
