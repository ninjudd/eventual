(ns eventual.server
  (:gen-class))

(defn -main [& args]
  (require 'eventual.servlet)
  ((ns-resolve 'eventual.servlet 'start)))
