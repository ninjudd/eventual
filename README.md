# eventual

Server-Sent Event and EventSource helpers for core.async.

## Installation

To use eventual, add the following to your `:dependencies`:

    [com.ninjudd/eventual "0.1.0"]

## Server

To create a handler that returns a stream of Server-Sent Events, you need use
[ring-async](http://github.com/ninjudd/ring-async) or some other servlet adapter that supports
asynchronous responses by returning a core.async channel for body.

```clj
(ns server-events-sample
  (:require [clojure.core.async :refer [go >! chan close!]]
            [ninjudd.eventual.server :refer [edn-events]]
            [ring.adapter.jetty-async :refer [run-jetty-async]]))

(defn sse-handler [request]
  (let [events (chan)]
    (go (loop [...]
          (if ...
            (>! events event)
            (recur ...)))
        (close! events))
    (edn-events events)))

(defn start []
  (run-jetty-async sse-handler {:join? false :port 8000}))
```

## Client

To connect to a server endpoint that returns an event stream in ClojureScript, eventual provides a
helper function that returns a core.async channel but user `EventSource` behind the scenes.

```clj
(ns client-events-sample
  (:require [cljs.core.async :refer [<!]]
            [ninjudd.eventual.client :refer [edn-events]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(let [events (edn-events "http://.../events")]
  (go (loop [...]
        (if-let [event (<! events)]
          ...
          (recur ...)))))
```

## License

Copyright © 2014 Justin Balthrop

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
