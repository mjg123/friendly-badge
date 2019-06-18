(ns friendly-badge-api.gh-query
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [friendly-badge-api.secrets :as secrets]))

(def url-template "https://api.github.com/repos/%s/%s/issues/comments")

(defn hyphenated-kw [s]
  (-> s
      (clojure.string/replace "_" "-")
      keyword))

(defn get-comments [owner repo]


  (let [resps (map (fn [page]
                     (-> (client/get (format url-template owner repo)
                                     {:query-params {:sort "created"
                                                     :direction "desc"
                                                     :page page
                                                     :client_id secrets/gh-client-id
                                                     :client_secret secrets/gh-client-secret}})))
                   ;; pages 1-4, which is 120 comments in total
                   (range 1 5))] 
        
    {:comments
     (apply concat
            (map (fn [r]
                   (json/read-str (:body r) :key-fn hyphenated-kw))
                 resps))
     :debug {:gh-ratelimit-remaining (-> resps last :headers (get "X-RateLimit-Remaining"))
             :gh-ratelimit-reset (-> resps last :headers (get "X-RateLimit-Reset"))}}))


(comment
  (get-comments "mjg123" "pacman")
  )
