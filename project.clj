(defproject friendly-badge-api "0.1.0-SNAPSHOT"
  :description "A badge for your GitHub Readmes to show how friendly your community is"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "3.10.0"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler friendly-badge-api.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
