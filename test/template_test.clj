(ns template-test
  (:require
   [clojure.test :refer :all]
   [tradingview.mongo.db :refer [connect disconnect]]
   [tradingview.impl.core :refer [tradingview!]]))


(def config
  {:mongo {:mongo-ip "127.0.0.1"
           :mongo-port 27017
           :mongo-db "seasonality"}})

(defn tradingview-config! []
  (let [mongo (connect config)
        db (:db mongo)
        tv (tradingview! db)]
    (assoc mongo :tradingview tv)))


(def state (atom nil))

(defn my-test-fixture [f]
  (reset! state (tradingview-config!))
  (f)
  (disconnect @state))

(use-fixtures :once my-test-fixture)


(def demochart  {:symbol "QQQ"
                 :resolution "D"
                 :name "WILLY"
                 :content "HA HA HA"})


(deftest storage-test
  (let [chart-id (.save-chart (:tradingview @state) 10 10 demochart)
        _ (println "chart id: " chart-id)
        chart (.load-chart (:tradingview @state) 10 10 chart-id)
        _ (.delete-chart (:tradingview @state) 10 10 chart-id)]
    (is (=  demochart (dissoc chart :id :timestamp)))))


(def demo-template {:name "demo-mania"
                    :content "mega"
                    })


#_(deftest template-test
  (let [template-id (.save-template (:tradingview @state) 10 10 demo-template)
        _ (println "template id: " template-id)
        data nil
       ; data (.load-template (:tradingview @state) 10 10 template-id) ;"5d87c9db3e4d5711b9cd0cc7")
       ; _ (println "template data: " data)
        ]
    (is (=  (:name data) "moving average 200 with bollinger"))))


(deftest search-test
  (let [result (.search (:tradingview @state) "CAC" "Index" "" 2)
        _ (println "search result: " result)]
    (is (=  2 (count result)))))

