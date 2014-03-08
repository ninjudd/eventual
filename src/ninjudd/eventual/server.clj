(ns ninjudd.eventual.server
  (:require [clojure.core.async :refer [go <! >! close! chan alts! timeout]]
            [cheshire.core :as json]))

(defn add-sse-headers [request]
  (update-in request [:headers] assoc
             "Content-Type" "text/event-stream; charset=utf-8"
             "Cache-Control" "no-cache"))

(defn sse-channel [f events & [opts]]
  (let [out (chan)
        disconnect (if-let [t (:timeout opts)]
                     (timeout t)
                     (chan))]
    (go (when-let [retry (:client-retry opts)]
          (>! out (str "retry: " retry "\n")))
        (loop []
          (let [keepalive (timeout (:keepalive opts 25000))
                [event ch] (alts! [events keepalive disconnect])]
            (cond
             (= ch events)
             (when event
               (when-let [event-id (:event-id (meta event))]
                 (>! out (str "id: " event-id "\n")))
               (when-let [event-type (:event-type (meta event))]
                 (>! out (str "event: " event-type "\n")))
               (>! out (str "data: " (f event) "\n\n"))
               (recur))
             (= ch keepalive)
             (do (>! out ":keepalive\n")
                 (recur)))))
        (close! out))
    out))

(defn edn-events [events & [opts]]
  (add-sse-headers
   {:body (sse-channel pr-str events opts)}))

(defn json-events [events & [opts]]
  (add-sse-headers
   {:body (sse-channel json/generate-string events opts)}))
