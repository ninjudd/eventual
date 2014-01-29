(defproject eventual-server-example "0.1.0-SNAPSHOT"
  :description "Example Server-Sent Events server using eventual."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.ninjudd/ring "1.2.2"]
                 [com.ninjudd/ring-async "0.2.3-SNAPSHOT"]
                 [com.ninjudd/eventual "0.1.1-SNAPSHOT"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [compojure "1.1.5"]
                 [cheshire "5.2.0"]]
  :main eventual.server)
