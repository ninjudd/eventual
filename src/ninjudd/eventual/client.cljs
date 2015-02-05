(ns ninjudd.eventual.client
  (:require [cljs.core.async :as async]
            [cljs.reader :refer [read-string]]))

(defn make-event [f e]
  (-> (f (.-data e))
      (vary-meta merge {:event-id (.-lastEventId e)
                        :event-type (keyword (.-type e))})))

(def error-retry-ms 5000)

(defn event-source [f url & [event-types]]
  (let [event-types (or event-types [:message])
        channel (async/chan)
        event-source (atom nil)]
    (letfn [(init []
              (let [source (new js/EventSource url)
                    handler (fn [e]
                              (async/put! channel (make-event f e)))]
                (doseq [event-type event-types]
                  (.addEventListener source (name event-type) handler))
                (set! (.-onerror source)
                      (fn [error]
                        (js/setTimeout (fn []
                                         (.close source)
                                         (reset! event-source (init)))
                                       error-retry-ms)))
                source))]
      (reset! event-source (init)))
    {:event-source event-source
     :channel channel}))

(defn edn-event-source [url & [event-types]]
  (event-source read-string url event-types))

(defn json-event-source [url & [event-types]]
  (event-source #(.parse js/JSON %) url event-types))

(defn close-event-source! [{:keys [channel event-source]}]
  (when-let [source @event-source]
    (.close source))
  (async/close! channel))
