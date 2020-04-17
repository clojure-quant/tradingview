(ns web.routes.tvhack
  (:require
    [schema.core :as s]
    [ring.util.http-response :refer [ok]]
    [compojure.core :refer :all] ; [compojure.core :refer [defroutes routes]]
    [compojure.route :as route]
    [compojure.api.sweet :as sweet]
    [cheshire.core :refer :all]
    [clj-time.core :as t] ; current time
    [tradingview.dump :refer [tradingview-status]]
    ))

(def tvhack-routes
    (sweet/context "/tvhack" [] :tags ["tvhack"]

      (sweet/GET "/data" []
         :summary "gets tradingview hack data"
         (ok {:result (tradingview-status)}))

    ))

(comment


  )
