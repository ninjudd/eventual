(ns ninjudd.eventual.client
  (:require [cljs.core.async :as async]
            [cljs.reader :refer [read-string]]))

(defn event-source [f url & [event-types]]
  (let [event-types (or event-types [:message])
        source (new js/EventSource url)
        channel (async/chan)
        handler (fn [e]
                  (async/put! channel (-> (f (.-data e))
                                          (vary-meta merge {:event-id (.-lastEventId e)
                                                            :event-type (keyword (.-type e))}))))]
    (doseq [event-type event-types]
      (.addEventListener source (name event-type) handler))
    {:event-source source
     :channel channel}))

(defn edn-event-source [url & [event-types]]
  (event-source read-string url event-types))

(defn json-event-source [url & [event-types]]
  (event-source #(.parse js/JSON %) url event-types))

(defn close-event-source! [{:keys [channel event-source]}]
  (when event-source
    (.close event-source))
  (async/close! channel))
