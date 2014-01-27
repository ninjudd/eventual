(ns ninjudd.eventual.client
  (:require [cljs.core.async :refer [put! chan]]
            [cljs.reader :refer [read-string]]))

(defn event-source-channel [f url]
  (let [source (new js/EventSource url)
        out (chan)]
    (.addEventListener source "message"
                       (fn [e]
                         (put! out (f (.-data e)))))
    out))

(defn edn-events [url]
  (event-source-channel read-string url))

(defn json-events [url]
  (event-source-channel #(.parse js/JSON %) url))
