(ns friendly-badge-api.core
  (:require [clojure.data.json :as json]
            [friendly-badge-api.azure-sentiment :as az]
            [friendly-badge-api.gh-query :as gh]))

(defn select-keys* [m paths]
  (into {} (map (fn [p]
                  [(last p) (get-in m p)]))
        paths))

(defn avg [nums] (/ (reduce + nums) (count nums)))

;; ---------------------------------------------------------

(defn get-sentiment-summary [owner repo & {:keys [debug?]}]
  (let [gh-comments (gh/get-comments owner repo)
        
        comments
        (->> (:comments gh-comments)
             (map #(select-keys* % [[:user :login]
                                    [:updated-at]
                                    [:author-association]
                                    [:body]
                                    [:html-url]])))

        sentiments (az/sentiments* (map :body comments))

        comment-sentiment (map (fn [c s]
                                 (assoc c :score (:score s)))
                               comments (:documents sentiments))

        summary {:repo-url (format "https://github.com/%s/%s" owner repo)
                 :sentiment {:summary-by-project-role
                             (into {}
                                   (map (fn [[association scores]]
                                          (vector (keyword association) (avg (map :score scores))))
                                        (group-by :author-association comment-sentiment)))
                             :extremes
                             (map #(select-keys % [:score :body :html-url])
                                  ((juxt first last) (sort-by :score comment-sentiment)))}

                 :comment-summary {:count (count comments)
                                   :newest (:updated-at (first comments))
                                   :oldest (:updated-at (last comments))}}]
    
    (if debug?
      (assoc summary :debug {:all-comments-by-score (sort-by :score comment-sentiment)
                             :gh-api (:debug gh-comments) })
      summary)))

