(ns friendly-badge-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [friendly-badge-api.core :as core]
            [clojure.data.json :as json]))

(defroutes app-routes
  (GET "/:user/:repo/badge.json" [user repo debug]
       {:headers {"Content-Type" "application/json"}
        :body (json/write-str (core/get-sentiment-summary user repo :debug? debug)
                              :key-fn (fn [s] (-> s name
                                                  (clojure.string/replace "-" "_"))))})


  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
