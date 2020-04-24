(ns demo.main
  (:require
   [demo.jetty :refer [run-jetty-blocking]]
   [demo.routes :refer [routes-ext]])
  (:gen-class))

(defn -main
  "Entry point wenn running web-server ' "
  [& args]
  (println "tradingview web starting ..")
  (run-jetty-blocking routes-ext 8087))