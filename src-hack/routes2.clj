


 (GET "/dump" [] handle-dump)
(POST "/dump" [] handle-dump)

(POST "/dumptv" {params :params}
  (let [file (:content params)]
    (println "dump-tradingview params: " params)
    (upload-file file)
    (response/redirect "https://www.tradingview.com/savechart/bongistan")))

   ;(GET "/tvdata" [] (response/response (tradingview-status)))
