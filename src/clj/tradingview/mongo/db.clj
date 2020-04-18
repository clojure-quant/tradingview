(ns tradingview.mongo.db
  (:require
   [clojure.tools.logging :refer [info]]
   [clj-ssh.ssh :as ssh]
   [monger.core :as mg]
   [monger.collection :as mc]
   [monger.joda-time])
  (:import [com.mongodb MongoOptions ServerAddress]))


; MOUNT SERVICE DEFINITION


(defn make-tunnel [remote-ip remote-port local-port private-key username]
  (info "creating ssh tunnel to" remote-ip ":" remote-port)
  (let [agent (ssh/ssh-agent {})]
    (ssh/add-identity agent {:private-key private-key})
    (let [session (ssh/session agent remote-ip {:username username :strict-host-key-checking :no})]
      (ssh/connect session
                   (ssh/forward-local-port session local-port remote-port)))))

(defn connect [config]
  (let [config (:mongo config)
        tunnel (:tunnel config)
        _ (when tunnel (make-tunnel (:ip tunnel) (:remote-port tunnel) (:mongo-port config) (slurp (:private-key-fn tunnel)) (:username tunnel)))
        conn (mg/connect {:host (:mongo-ip config) :port (:mongo-port config)}) ; 29017 on client via tunnel
        _ (info "connecting database port " (:mongo-port config))
        db (mg/get-db conn (:mongo-db config))]
    {:db db :conn conn}))

(defn disconnect [conn-db]
  (info "disconnecting database..")
  (mg/disconnect (:conn conn-db)))


; ACTIONS


(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn capitalize-category [data]
  (update data :category clojure.string/capitalize))

(defn separate-category [instrument]
  (->> instrument
       (:symbol)
       (re-matches #"(.*)\s(\w+)\s*")
       (drop 1)
       (zipmap [:symbol :category])
       (capitalize-category)
       (merge instrument)
       (#(assoc % :symbol (str (:symbol %) " " (:category %))))))

; (defn load-symbol [db symbol]
;  (mc/find-one-as-map  db "instruments" { :symbol symbol }))

(comment ; *************************************************************************

; test db connection
  (do
    (def test- (connect))
    (print test-)
    (println (mc/find-one-as-map (:db test-) "instruments" {:symbol "DAX Index"}))
    (disconnect test-))


; use db services (requires started mount services)


  (println (mc/find-one-as-map db "instruments" {:symbol "DAX Index"}))

  (separate-category {:symbol "MO equity " :name "jghf "})
  (in? ["Equity" "Index" "Curncy"] (:category {:category "Index" :bonho 88}))


  ; ********************************************************************************
  )