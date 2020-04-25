(ns tradingview.routes
  (:require
   [schema.core :as s]
   [cheshire.core :refer [generate-string]]
   [ring.util.http-response :refer [ok]]
   [compojure.core :refer [routes]]
   [compojure.api.sweet :as sweet]
   [tradingview.impl.time :refer [server-time]]
   [tradingview.middleware :refer [wrap-middleware]]
   [tradingview.chart-storage :refer [save-or-modify-chart modify-chart-wrapped]]
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


(defn format-chart [data]
  (let [{:keys [charts layout legs]} data
        content (select-keys data [:publish_request_id
                                   :name
                                   :description
                                   :resolution
                                   :symbol_type
                                   :exchange
                                   :listed_exchange
                                   :symbol
                                   :short_name
                                   :is_realtime])
        content (if legs
                  (assoc content :legs (generate-string legs))
                  content)
        content-inner {:content (generate-string
                                 {:layout layout
                                  :charts charts})}
        content (merge content content-inner)]
     {:timestamp (:timestamp data)
      :name (:name data)
      :id (:id data)
      :content (generate-string content)}))

(defn load-chart [tv client user chart]
  (let [data (.load-chart tv client user chart)
        ;_ (println "chart: " data)
        ]
    (if (nil? data)
      {:status "error" :error "chart not found"}
      {:status "ok" :data (format-chart data)})))



(defn routes-storage [tv]
  (sweet/context "/tradingviewstorage" [] :tags ["storage"]

    ; charts
    (sweet/GET "/1.1/charts" []
      :query-params [client :- s/Int
                     user :- s/Int
                     {chart :- s/Int 0}]
      (ok (if (= chart 0)
            {:status "ok" :data (.chart-list tv client user)}
            (load-chart tv client user chart))))

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


