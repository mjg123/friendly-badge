(ns friendly-badge-api.secrets)

(def azure-key (System/getenv "AZURE_KEY"))
(def gh-client-id (System/getenv "GH_CLIENT_ID"))
(def gh-client-secret (System/getenv "GH_CLIENT_SECRET"))
