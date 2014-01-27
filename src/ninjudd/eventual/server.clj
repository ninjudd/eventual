(ns ninjudd.eventual.server
  (:require [clojure.core.async :refer [go <! >! close! chan alts! timeout]]
            [cheshire.core :as json]))

(defn add-sse-headers [request]
  (update-in request [:headers] assoc
             "Content-Type" "text/event-stream; charset=utf-8"
             "Cache-Control" "no-cache"
             "Connection" "keep-alive"))

(defn sse-channel [f events]
  (let [out (chan)]
    (go (loop []
          (let [[event ch] (alts! [events (timeout 3000)])]
            (if (= ch events)
              (when event
                (>! out (str "data: " (f event) "\n\n"))
                (recur))
              (do
                (>! out ":keepalive\n")
                (recur)))))
        (close! out))
    out))

(defn edn-events [events]
  (add-sse-headers
   {:body (sse-channel pr-str events)}))

(defn json-events [events]
  (add-sse-headers
   {:body (sse-channel json/generate-string events)}))
