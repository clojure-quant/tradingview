(ns api.my-host)

;; Detect hostname and switch between DEV and PROD urls.

(defn get-browser-hostname []
  (let [hostname js/window.location.hostname]
        (js/console.log "hostname is: " hostname)
        hostname
        ))

(def config
     {:endpoint "/api/"
      :dev {
         :protocol "http"
         :ip "localhost"
         :port 5005
       }
       :production {
          :protocol "https"
          :ip "quant.hoertlehner.com"
          :port 443
       }
       })


(defn make-url [envi]
  (let [setting (envi config)]
    (str (:protocol setting) "://" (:ip setting) ":" (:port setting) (:endpoint config))))

  ;(str "http://" browser-hostname ":5005/api/")
;  (str "https://" browser-hostname "/api/"))

(defn dev? []
  (let [browser-hostname (get-browser-hostname)]
    (or (nil? browser-hostname) (= "localhost" browser-hostname))))


; bad ass trick by esteban to not need to change the api endpoints depending on the machine it is running on!!
(def base-url
   (let [url (if (dev?)
                 (make-url :dev) ; use dev when no hostname is being returned.
                 (make-url :production))]
    (js/console.log "Api Endpoint BaseURL:" url)
    url))



(defn create-url [partial-url]
  "partial-url is the realtive url on our site
   Prepends Site host info before the partial-url to make a valid request
   depending on the dev environment
  "
  (str base-url partial-url))




(comment

  (get-hostname)

  )
