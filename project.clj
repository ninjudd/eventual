(defproject com.ninjudd/eventual "0.3.1"
  :description "Server-Sent Event and EventSource helpers for core.async."
  :url "http://github.com/ninjudd/eventual"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]]
  :profiles {:dev {:dependencies [[cheshire "5.2.0"]]}})
