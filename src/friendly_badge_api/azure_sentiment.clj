(ns friendly-badge-api.azure-sentiment
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [friendly-badge-api.secrets :as secrets]))

(def url "https://eastus.api.cognitive.microsoft.com/text/analytics/v2.1/sentiment")

(defn create-post-body [strs]
  (->> strs
       (map-indexed (fn [i s] {:id (inc i)
                               :language "en"
                               :text s}))
       (assoc {} :documents)
       (json/write-str)))

(defn sentiments [strs]
  (->
   (client/post url {:body (create-post-body strs)
                     :content-type :json
                     :headers {"Ocp-Apim-Subscription-Key" secrets/azure-key}})
   :body
   (json/read-str :key-fn keyword)))


(def sentiments* (memoize sentiments))


