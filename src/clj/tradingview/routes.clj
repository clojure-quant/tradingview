(ns tradingview.routes
  (:require
   [clojure.string :as str]
   [schema.core :as s]
   [ring.util.response :as response]
   [ring.util.http-response :refer [ok]]
   [compojure.core :refer [defroutes GET]]
   ;[compojure.route :as route]
   [compojure.api.sweet :as sweet]
  ; [cheshire.core :refer :all]
  ; [cemerick.url :refer (url url-encode map->query)]
   [tradingview.impl.time :refer [server-time]]
   [tradingview.middleware :refer [wrap-middleware]]))

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

(defn save-chart-wrapped [tv client user name content symbol resolution]
  (let [data {:name name :content content :symbol symbol :resolution resolution}]
    {:status "ok" :id (.save-chart tv client user data)}))

(defn modify-chart-wrapped [tv client user chart name content symbol resolution]
  (let [data {:name name :content content :symbol symbol :resolution resolution :chart_id chart}]
    (.modify-chart tv client user chart data)
    {:status "ok"}))

(defn save-or-modify-chart [tv client user chart name content symbol resolution]
  (if (= chart 0)
    (save-chart-wrapped tv  client user       name content symbol resolution)
    (modify-chart-wrapped tv client user chart name content symbol resolution)))

(defn routes-storage [tv]
  (sweet/context "/tradingviewstorage" [] :tags ["storage"]

        ; storage
    (sweet/GET "/1.1/charts" []
      :query-params [client :- s/Int user :- s/Int {chart :- s/Int 0}]
      (ok (if (= chart 0)
            {:status "ok" :data (.load-chart tv client user)}
            {:status "ok" :data (.load-chart tv client user chart)})))

    (sweet/POST "/1.1/charts" []
      :query-params [client :- s/Int user :- s/Int {chart :- s/Int 0}]
      :consumes ["application/x-www-form-urlencoded"]
      :form-params [name content symbol resolution]
           ;(ok {:status "ok" :id (save-chart client user chart-data)} ))
      (ok (save-or-modify-chart tv client user chart name content symbol resolution)))

    (sweet/PUT "/1.1/charts" []
      :query-params [client :- s/Int user :- s/Int {chart s/Int}]
      :body [chart-data Chart]
      (ok (do (.modify-chart tv client user chart chart-data)
              {:status "ok"})))

    (sweet/DELETE "/1.1/charts" []
      :query-params [client :- s/Int user :- s/Int {chart :- s/Int 0}]
      (ok (do (.delete-chart tv client user chart) {:status "ok"})))


        ; storage


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
  (defroutes tradingview-routes-raw
    (sweet/api
     {:swagger
      {:ui   "/docs"
       :spec "/swagger.json"
       :data {:info {:title "tradingview"}
              :tags [{:name "tradingview" :description "tradingview.com chart api"}]}}}
     (sweet/context "/api" [] :tags ["tradingview"]
       (routes-data tv)
       (routes-storage tv))))
  (def tradingview-routes
    (wrap-middleware tradingview-routes-raw)))


