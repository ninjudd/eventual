(ns eventual.servlet
  (:require [clojure.core.async :refer [go >! <! timeout chan close!]]
            [ninjudd.eventual.server :refer [edn-events]]
            [compojure.core :refer [routes]]
            [compojure.route :refer [resources]]
            [ring.adapter.jetty-async :refer [run-jetty-async]]))

(def current-id (atom 0))

(defn sse-handler [request]
  (when (and (= "/events" (:uri request))
             (= :get (:request-method request)))
    (swap! current-id inc)
    (let [events (chan)
          id @current-id]
      (go (dotimes [i 1000]
            (let [event {:id id :count i :now (java.util.Date.)}]
              (>! events event))
            (<! (timeout (int (rand 10000)))))
          (close! events))
      (edn-events events))))

(defn start []
  (run-jetty-async
   (routes (resources "/")
           sse-handler)
   {:join? false :port 3000}))
