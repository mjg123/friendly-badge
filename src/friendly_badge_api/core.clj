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

(defn sum-of [m ks]
  (reduce + (map #(or (m %) 0) ks)))

(def role-weights
  {:COLLABORATOR 2
   :MEMBER 2
   :OWNER 2
   :FIRST_TIMER 1
   :FIRST_TIME_CONTRIBUTOR 1
   :CONTRIBUTOR 1
   :NONE 0.5})

(defn weighted-avg-sentiment [sentiments]
  (let [[total-weight total-weighted-score]
        (->> sentiments
             (map (fn [[role score]]
                    (vector (role-weights role) score)))
             (reduce (fn [[acc-w acc-s] [weight score]]
                       (vector (+ acc-w weight) (+ acc-s (* weight score)))) [0 0]))]
    (/ total-weighted-score total-weight)))

(defn badge-url [score]
  (format "https://img.shields.io/static/v1.svg?label=FriendlyBadge&message=%.2f&color=green" score))


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

        summary-by-role (into {}
                              (map (fn [[association scores]]
                                     (vector (keyword association) (avg (map :score scores))))
                                   (group-by :author-association comment-sentiment)))
        
        summary {:repo-url (format "https://github.com/%s/%s" owner repo)
                 :repo-badge-url (badge-url (weighted-avg-sentiment summary-by-role))
                 :badge-docs-url "https://github.com/mjg123/friendly-badge#friendly-badge-api"
                 :sentiment {:avg-sentiment-by-project-role summary-by-role
                             :weighted-avg-sentiment (weighted-avg-sentiment summary-by-role)}
                 :comment-summary {:count (count comments)
                                   :newest (:updated-at (first comments))
                                   :oldest (:updated-at (last comments))}}]
    
    (if debug?
      (-> summary
          (assoc :debug {:all-comments-by-score (sort-by :score comment-sentiment)
                         :gh-api (:debug gh-comments)})
          (assoc-in [:sentiment :extremes] (map #(select-keys % [:score :body :html-url])
                                                ((juxt first last) (sort-by :score comment-sentiment)))))
      summary)))

