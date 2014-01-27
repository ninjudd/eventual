(ns ninjudd.eventual.server-test
  (:use clojure.test)
  (:require [clojure.core.async :refer [go >! <! timeout chan close! <!!]]
            [ninjudd.eventual.server :refer [edn-events json-events]]
            [clojure.string :refer [split]]))

(defn slurp-events [response]
  (let [body (:body response)]
    (split (<!! (clojure.core.async/reduce str "" body))
           #"\n\n")))

(deftest test-edn-events
  (let [events (chan)]
    (go (dotimes [i 5]
          (>! events {:foo i}))
        (close! events))
    (is (= ["data: {:foo 0}"
            "data: {:foo 1}"
            "data: {:foo 2}"
            "data: {:foo 3}"
            "data: {:foo 4}"]
           (slurp-events (edn-events events))))))

(deftest test-json-events
  (let [events (chan)]
    (go (dotimes [i 5]
          (>! events {:foo i}))
        (close! events))
    (is (= ["data: {\"foo\":0}"
            "data: {\"foo\":1}"
            "data: {\"foo\":2}"
            "data: {\"foo\":3}"
            "data: {\"foo\":4}"]
           (slurp-events (json-events events))))))
