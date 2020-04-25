(ns tradingview.study.hack-routes
  (:require
   [cheshire.core :refer [parse-string]]
   [ring.util.response :refer [response redirect]]
   [ring.util.http-response :refer [ok]]
   [compojure.core :refer [routes GET]]
   [compojure.api.sweet :as sweet]
   [clojure.java.io :refer [input-stream]]
   [tradingview.chart-storage :refer [save-or-modify-chart]]
   [tradingview.study.extract :refer [chart-extract chart-extract-page]]
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
        _ (spit "/tmp/tv-dump.json" data-raw)]
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
        json (:content chart)]
    json))


(defn tvhack-api-routes [tv]
  (sweet/context "/tvhack" [] :tags ["tvhack"]

    (sweet/GET "/json" []
      :query-params [id :- Long]
      (ok (chart-json tv id)))

    (sweet/GET "/extract" []
      :query-params [id :- Long]
      (ok (chart-extract tv id)))

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
        (save-or-modify-chart tv 77 77 id
                              (get-zip-contents file-params)
                              {:name name
                               :resolution resolution
                               :symbol symbol})
        (redirect "https://www.tradingview.com/savechart/bongistan")))))
