(ns time.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

;Define time-collection with existinf json
(def times-collection (atom (json/read-str (slurp "resources/test.json"))))

;Collection Helper functions to add a new block
(defn addtime [time location date]
  (swap! times-collection conj {:time time :location location :date date})
  (spit "resources/test.json" (str (json/write-str @times-collection)))
  )

  (defn clear []
  (spit "resources/test.json" (str (json/write-str [])))
  (reset! times-collection (json/read-str (slurp "resources/test.json")))
  )

;Root page returning help
(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html", "access-control-allow-origin" "*", "access-control-allow-methods" "get" }
   :body    "Try '/times'"})

;Returns a list of times from JSON
(defn times-handler [req]
    (def times (json/read-str (slurp "resources/test.json")))
      {:status  200
         :headers {"Content-Type" "text/json", "access-control-allow-origin" "*", "access-control-allow-methods" "get"}
         :body (json/write-str times)}
   )

;Helper to get parameters
(defn getparameter [req pname] (get (:params req) pname))

;Add new times
(defn new-times-handler [req]
    (-> (let [p (partial getparameter req)]
    (addtime (p :time) (p :location) (p :date))))
    (def times (json/read-str (slurp "resources/test.json")))
  {:status  200
    :headers {"Content-Type" "text/json", "access-control-allow-origin" "*", "access-control-allow-methods" "get"}
    :body (json/write-str times)}
  )

  (defn clear-handler [req]
    (clear)
  {:status  200
    :headers {"Content-Type" "text/html", "access-control-allow-origin" "*", "access-control-allow-methods" "get"}
    :body "Cleared!"}
  )

;Routes
(defroutes app-routes
  (GET "/" [] simple-body-page)
  (GET "/times" [] times-handler)
  (GET "/times/add" [] new-times-handler)
  (GET "/clear" [] clear-handler)
  (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "4000"))]
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/")))
)