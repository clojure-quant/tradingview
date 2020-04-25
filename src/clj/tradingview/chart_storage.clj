(ns tradingview.chart-storage
  (:require
   [cheshire.core :refer [parse-string]]))

(defn unpack-chart [content-str]
  (let [chart (parse-string content-str true)
        data (if-let [content (:content chart)]
               (dissoc (merge chart (parse-string content true)) :content)
               chart)
        data (if-let [legs (:legs data)]
               (assoc data :legs (parse-string legs true))
               data)
        ]
    data))


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
