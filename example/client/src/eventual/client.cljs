(ns eventual.client
  (:require [cljs.core.async :refer [<!]]
            [ninjudd.eventual.client :refer [edn-events]]
            [domina :refer [append! by-id]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defn init []
  (let [events (edn-events "/events")]
    (go (loop []
          (when-let [event (<! events)]
            (append! (by-id "events")
                     (prn-str event))
            (recur))))))

(init)
