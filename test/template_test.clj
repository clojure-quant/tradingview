(ns template-test
  (:require
   [clojure.test :refer :all]
   [tradingview.mongo.db :refer [disconnect]]
   [tradingview.impl.core :refer [tradingview-config!]]))


(def template-id "5d87c9db3e4d5711b9cd0cc7")
(def template-name "moving average 200 with bollinger")

#_(save-chart 10 10 {:symbol "AAPL"
                     :resolution "M"
                     :name "BONGO"
                     :content "HO HO HO"})

(def chart  {:symbol "QQQ"
             :resolution "D"
             :name "WILLY"
             :content "HA HA HA"})


(def state (atom nil))

(defn my-test-fixture [f]
  (reset! state (tradingview-config!))
  (f)
  (disconnect @state))

(use-fixtures :once my-test-fixture)


(deftest template
  (let [data (.load-template (:tradingview @state) 1 1 template-id)
        _ (println "template data: " data)]
    (is (=  (:name data) template-name))))

(deftest storage
  (let [chart-id (.save-chart (:tradingview @state) 10 10 chart)
        _ (println "chart id: " chart-id)
        chart2 (.load-chart (:tradingview @state) 10 10 chart-id)]
    (is (=  chart (dissoc chart2 :id :timestamp)))))