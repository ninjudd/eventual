(ns ninjudd.eventual.client
  (:require [cljs.core.async :as async]
            [cljs.reader :refer [read-string]]))

(defn event-source [f url]
  (let [source (new js/EventSource url)
        channel (async/chan)]
    (.addEventListener source "message"
                       (fn [e]
                         (async/put! channel (f (.-data e)))))
    {:event-source source
     :channel channel}))

(defn edn-event-source [url]
  (event-source read-string url))

(defn json-event-source [url]
  (event-source #(.parse js/JSON %) url))

(defn close! [{:keys [channel event-source]}]
  (when event-source
    (.close event-source))
  (async/close! channel))
