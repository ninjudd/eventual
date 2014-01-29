(defproject eventual-client-example "0.1.0"
  :description "Example EventSource client using eventual."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2127"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [com.ninjudd/eventual "0.1.1"]
                 [domina "1.0.2"]]
  :plugins [[lein-cljsbuild "1.0.1"]]
  :source-paths ["src"]
  :aliases {"watch" ["cljsbuild" "auto" "eventual-client-example"]
            "clean" ["cljsbuild" "clean"]}
  :cljsbuild {
    :builds [{:id "eventual-client-example"
              :source-paths ["src"]
              :compiler {
                :output-to "eventual-client-example.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
