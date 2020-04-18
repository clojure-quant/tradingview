(ns tradingview.impl.template
  (:require
   [clj-time.core :as t]
   [monger.collection :as mc]))

(defn load-template
  ([db client-id user-id] ; LIST
   (-> (mc/find-maps db "tvtemplate"
                     {:client_id client-id :user_id user-id}
                     {:_id 0 :name 1})))
  ([db client-id user-id chart-id] ; ONE
   (mc/find-one-as-map db "tvtemplate"
                       {:client_id client-id :user_id user-id :_id chart-id}
                       {:_id 0 :name 1 :content 1})))


; POST REQUEST: charts_storage_url/charts_storage_api_version/charts?client=client_id&user=user_id&chart=chart_id


(defn save-template
  [db client_id user_id data]
  (let [query {:client_id client_id :user_id user_id :name (:name data)}
        doc (merge data query)]
    (mc/update db "tvtemplate" query doc {:upsert true})
    nil))

(defn modify-template--unused
  [db client_id user_id chart_id data]
  (let [query {:client_id client_id :user_id user_id :chart_id chart_id}
        doc (merge data query)
        doc (merge doc {:timestamp (t/now)})]
    (mc/update db "tvtemplate" query doc {:upsert false})))

(defn delete-template
  [db client_id user_id name]
  (mc/remove db "tvtemplate"
             {:client_id client_id :user_id user_id :name name}))

