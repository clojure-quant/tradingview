(ns tradingview.routes
  (:require
   [schema.core :as s]
   [cheshire.core :refer [parse-string generate-string]]
   [ring.util.response :refer [response]]
   [ring.util.http-response :refer [ok]]
   [compojure.core :refer [routes GET]]
   [compojure.api.sweet :as sweet]
   [tradingview.impl.time :refer [server-time]]
   [tradingview.middleware :refer [wrap-middleware]]
   [tradingview.study.hack-routes :refer [tvhack-api-routes tvhack-ui-routes]]))

(s/defschema Chart
  {:symbol String
   :resolution String
   :name  String
   :content String
      ;:id     Long
     ;:price Double
     ;;:hot   Boolean
    ;;(s/optional-key :description) String
   })

(s/defschema Template
  {:name String
   :content String})

(defn unpack-chart [content-str]
  (let [chart (parse-string content-str)
        content (parse-string (get chart "content"))
        legs (parse-string (get chart "legs"))]
    (merge chart {"content" content
                  "legs" legs})))

(defn pack-chart [chart-unpacked]
  (let [legs (generate-string (get chart-unpacked :legs))
        content (generate-string (get chart-unpacked :content))
        chart (merge chart-unpacked {:content content
                                     :legs legs})
        ;_ (println "chart: " chart)
        ]
    ;(generate-string chart)
    chart))

(defn save-chart-wrapped [tv client-id user-id content options]
  (let [chart (unpack-chart content)
        data (merge chart options)]
    {:status "ok" :id (.save-chart tv client-id user-id data)}))

(defn modify-chart-wrapped [tv client-id user-id chart-id content options]
  (let [chart (unpack-chart content)
        data (merge {:chart_id chart-id} options chart)]
    (.modify-chart tv client-id user-id chart-id data)
    {:status "ok"}))

(defn save-or-modify-chart [tv client-id user-id chart-id content options]
  (if (= chart-id 0)
    (save-chart-wrapped   tv client-id user-id          content options)
    (modify-chart-wrapped tv client-id user-id chart-id content options)))

(defn load-chart [tv client user chart]
  (pack-chart
   (.load-chart tv client user chart)))

(defn routes-storage [tv]
  (sweet/context "/tradingviewstorage" [] :tags ["storage"]

    ; charts
    (sweet/GET "/1.1/charts" []
      :query-params [client :- s/Int
                     user :- s/Int
                     {chart :- s/Int 0}]
      (ok (if (= chart 0)
            {:status "ok" :data (.chart-list tv client user)}
            {:status "ok" :data (load-chart tv client user chart)})))

    (sweet/POST "/1.1/charts" []
      :query-params [client :- s/Int
                     user :- s/Int
                     {chart :- s/Int 0}]
      :consumes ["application/x-www-form-urlencoded"]
      :form-params [content
                    name
                    symbol
                    resolution]
      (ok (save-or-modify-chart tv client user chart
                                content
                                {:name name
                                 :symbol symbol
                                 :resolution resolution})))

    (sweet/PUT "/1.1/charts" []
      :query-params [client :- s/Int
                     user :- s/Int
                     {chart s/Int}]
      :body [chart-data Chart]
      (ok (modify-chart-wrapped tv client user chart chart-data {})))

    (sweet/DELETE "/1.1/charts" []
      :query-params [client :- s/Int user :- s/Int {chart :- s/Int 0}]
      (ok (do (.delete-chart tv client user chart) {:status "ok"})))


        ; templates


    (sweet/GET "/1.1/study_templates" []
      :query-params [client :- s/Int user :- s/Int {chart :- s/Int 0}]
      (ok (if (= chart 0)
            {:status "ok" :data (.load-template tv client user)}
            {:status "ok" :data (.load-template tv client user chart)})))

    (sweet/POST "/1.1/study_templates" []
      :query-params [client :- s/Int user :- s/Int]
      :form-params [name content]
      (ok (do (.save-template tv client user {:name name :content content})
              {:status "ok"})))
              ;(ok {:status "ok" :id (save-template client user template-data)} ))

    (sweet/DELETE "/1.1/study_templates" []
      :query-params [client :- s/Int user :- s/Int {template :- s/Str ""}]
      (ok (do (.delete-template tv client user template) {:status "ok"})))))

(defn routes-data [tv]
  (sweet/context "/tradingview" [] :tags ["data"]

    (sweet/GET "/config" []
      :query-params []
      (ok (.config tv)))

    (sweet/GET "/time" []
      :query-params []
      :return Long
      (ok (server-time)))

    (sweet/GET "/search" []
      :query-params [query :- String
                     type :- String
                     exchange :- String
                     limit :- Long]
      (ok (.search tv query type exchange limit)))

    (sweet/GET "/symbols" []
      :query-params [symbol :- String]
      (ok (.symbol-info tv symbol)))

      ; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1567457308&to=1568321308
    (sweet/GET "/history" []
      :query-params [symbol :- String resolution from :- Long to :- Long]
      (ok (.load-series tv symbol resolution from to)))))

(defn create-tradingview-routes! [tv]
  (wrap-middleware
   (routes
    (tvhack-ui-routes tv)
    (sweet/api
     {:swagger
      {:ui   "/docs"
       :spec "/swagger.json"
       :data {:info {:title "tradingview"}
              :tags [{:name "tradingview" :description "tradingview.com chart api"}]}}}
     (sweet/context "/api" [] :tags ["tradingview"]
       (routes-data tv)
       (routes-storage tv))
     (tvhack-api-routes tv)))))


