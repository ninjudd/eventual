(ns ninjudd.eventual.client
  (:require [cljs.core.async :as async]
            [cljs.reader :refer [read-string]]))

(defn make-event [f event]
  (-> (f (.-data event))
      (vary-meta merge {:event-id (.-lastEventId event)
                        :event-type (keyword (.-type event))})))

(defn event-source [f url & [event-types]]
  (let [event-types (or event-types [:message])
        source (new js/EventSource url)
        channel (async/chan)]
    (doseq [event-type event-types]
      (.addEventListener source (name event-type)
                         (fn [event]
                           (async/put! channel (make-event f event)))))
    (set! (.-onerror source)
          (fn [error]
            (async/close! channel)))
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
