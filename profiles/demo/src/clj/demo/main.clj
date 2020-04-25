(ns demo.main
  (:require
   [demo.jetty :refer [run-jetty-blocking start-jetty]]
   [demo.routes :refer [routes-ext]])
  (:gen-class))

(defn start []
  (println "tradingview web starting ..")
  (start-jetty routes-ext 8087))

(defn -main
  "Entry point to run web-server"
  [& args]
  (println "tradingview web starting ..")
  (run-jetty-blocking routes-ext 8087))