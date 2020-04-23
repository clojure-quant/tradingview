(ns tradingview.middleware
  (:require
   [ring.util.http-response :refer [ok]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   ;[ring.middleware.reload :refer [wrap-reload]]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.multipart-params :refer [wrap-multipart-params]]
   [ring.middleware.gzip :refer [wrap-gzip]]))

(defn allow-cross-origin
  "Middleware function to allow cross origin requests from browsers.
   When a browser attempts to call an API from a different domain, it makes an OPTIONS request first to see the server's
   cross origin policy.  So, in this method we return that when an OPTIONs request is made.
   Additionally, for non OPTIONS requests, we need to just returm the 'Access-Control-Allow-Origin' header or else
   the browser won't read the data properly.
   The above notes are all based on how Chrome works. "
  ([handler]
   (allow-cross-origin handler "*"))
  ([handler allowed-origins]
   (fn [request]
     (if (= (request :request-method) :options)
       (-> (ok)                     ; Don't pass the requests down, just return what the browser needs to continue.
           (assoc-in [:headers "Access-Control-Allow-Origin"] allowed-origins)
           (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,POST,DELETE")
           (assoc-in [:headers "Access-Control-Allow-Headers"] "X-Requested-With,Content-Type,Cache-Control,Origin,Accept,Authorization")
           (assoc :status 200))
       (-> (handler request)         ; Pass the request on, but make sure we add this header for CORS support in Chrome.
           (assoc-in [:headers "Access-Control-Allow-Origin"] allowed-origins))))))

(defn wrap-middleware [routes]
  (-> routes
      (allow-cross-origin)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false)) ; allow POST
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])
      (wrap-params)
      (wrap-multipart-params)
      ;(wrap-reload) ;wrap reload isn't needed when the clj sources are watched by figwheel
      (wrap-gzip)))
