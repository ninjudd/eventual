# eventual

Server-Sent Event and EventSource helpers for core.async.

## Installation

To use eventual, add the following to your `:dependencies`:

    [com.ninjudd/eventual "0.5.0"]

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

## Event Metadata

By default, only `message` events are sent and received. However, you can send diffent event types
by providing metadata on the events you put onto the channel. You can also provide an event id this
way.

```clj
(go ...
  (>! events (with-meta event {:event-type :add, :event-id 1234})))
```

To receive events besides `message` on the client, you have to provide a list of events you
handle. Events taken from the channel will have metadata with their type and id.

```clj
(edn-events "http://.../events" [:add :remove])
```

## License

Copyright © 2015 Justin Balthrop

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
