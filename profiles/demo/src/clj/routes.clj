(ns web.routes
  (:require
    [ring.util.response :as response]
    [ring.util.http-response :refer :all]
    [compojure.core :refer :all] ; [compojure.core :refer [defroutes routes]]
    [compojure.route :as route]
    [compojure.api.sweet :as sweet]
    [cheshire.core :refer :all]

    [web.routes.demo]
    [web.routes.universe]
    [web.routes.portfoliolist]
    [web.routes.seasonal]
    [web.routes.fundamental]
    [web.routes.bloomberg]
    [web.routes.tradingview]
    [web.routes.tvhack]
    [tradingview.dump :refer [upload-file tradingview-status]]

    [web.views.views :as my-views]
    [ring.handler.dump :refer [handle-dump]]
))


(defroutes app-routes
   ; (route/resources "/" {:root "public"})

   (GET "/dev" [] (-> (response/resource-response "index.html" {:root "public"})
                   (response/content-type "text/html")))

   (GET "/demo" [] (-> (response/resource-response "tradingview.html" {:root "public"})
                   (response/content-type "text/html")))

   (GET "/dump" [] handle-dump)
   (POST "/dump" [] handle-dump)

   (POST "/dumptv" {params :params}
         (let [file (:content params)]
           (println "dump-tradingview params: " params)
           (upload-file file)
           (response/redirect "https://www.tradingview.com/savechart/bongistan")
           )
           )

   ;(GET "/tvdata" [] (response/response (tradingview-status)))




  ; Views
  (GET "/approuter" [] (my-views/app-router-page)) ; reagent apps

  web.routes.tradingview/routes-redirect-history
  web.routes.tradingview/routes-redirect-other

  ; Api
  (sweet/api
     {:swagger
       {:ui   "/docs"
        :spec "/swagger.json"
        :data {:info {:title "clojureQuant"}
               :tags [
                  {:name "tradingview" :description "tradingview.com chart api"}
              ]}}}
     web.routes.tradingview/add-routes
  )

  (route/not-found "<h1> Sorry! Page not found.</h1>")

)
