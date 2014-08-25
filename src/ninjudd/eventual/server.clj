(ns ninjudd.eventual.server
  (:require [clojure.core.async :refer [go <! >! close! chan alts! timeout]]
            [cheshire.core :as json]))

(defn add-sse-headers [request]
  (update-in request [:headers] assoc
             "Content-Type" "text/event-stream; charset=utf-8"
             "Cache-Control" "no-cache"))

(defn data [& args]
  (.getBytes (apply str args) "UTF-8"))

(defn sse-channel [f events & [opts]]
  (let [out (chan)
        disconnect (if-let [t (:timeout opts)]
                     (timeout t)
                     (chan))]
    (go (when-let [retry (:client-retry opts)]
          (>! out (data "retry: " retry "\n")))
        (loop []
          (when-not @(.closed out)
            (let [keepalive (timeout (:keepalive opts 25000))
                  [event ch] (alts! [events keepalive disconnect])]
              (cond
               (= ch events)
               (when event
                 (when-let [event-id (:event-id (meta event))]
                   (>! out (data "id: " event-id "\n")))
                 (when-let [event-type (:event-type (meta event))]
                   (>! out (data "event: " (name event-type) "\n")))
                 (>! out (data "data: " (f event) "\n\n"))
                 (>! out :flush)
                 (recur))
               (= ch keepalive)
               (do (>! out (data ":keepalive\n"))
                   (>! out :flush)
                   (recur))))))
        (close! events)
        (close! out))
    out))

(defn edn-events [events & [opts]]
  (add-sse-headers
   {:body (sse-channel pr-str events opts)}))

(defn json-events [events & [opts]]
  (add-sse-headers
   {:body (sse-channel json/generate-string events opts)}))
