(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

; Simple Body Page
(defn simple-body-page [req] ;(3)
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Try '/times'"})

;new
(defn times-handler [req]
   (def times (json/read-str (slurp "resources/test.json")))
      {:status  200
         :headers {"Content-Type" "text/json"}
         :body (json/write-str times)}
   )

; Our main routes
(defroutes app-routes
  (GET "/" [] simple-body-page)
  (GET "/times" [] times-handler)
  (route/not-found "Error, page not found!"))

; Our main entry function
(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))