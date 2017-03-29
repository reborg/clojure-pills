(ns clojure-pills.into)

"Clojure Pills - 010 into"

;; "changing" the type of a collection
(into #{} (range 10))
(into [] (range 10))
(into (sorted-set) (range 10))
(into {} [[:a 1] [:b 2] [:c 3]])

;; follows "conj" semantic
(into '() [1 2 3])
(import 'clojure.lang.PersistentQueue)
(def q (into (PersistentQueue/EMPTY) [1 2 3]))
(peek q)

;; merging and nils
(into (range 10 20) (range 10))
(into nil #{1 2 3})
(into nil nil)

;; transducers enabled!
(into (vector-of :int)
      (comp (map inc) (filter even?))
      (range 10))

;; metadata handling
(defn sign [c] (with-meta c {:signature (apply str c)}))
(meta (into (sign [1 2 3]) (sign (range 10))))

;; mutables are not supported
(into (transient []) (range 10))
(into (int-array []) (range 10))
(import 'java.util.ArrayList) (into (ArrayList.) (range 10))

;; Example: keeping the original type

(defn maintain [fx f coll]
  (into (empty coll) (fx f coll)))

(->> #{1 2 3 4 5} (maintain map inc) (maintain filter odd?))

(->> {:a 1 :b 2 :c 5} (maintain filter (comp odd? last)))

;; Implementation details and performances

(require '[criterium.core :refer [quick-benchmark]])

(defmacro b [expr]
  `(str (first (:mean (quick-benchmark ~expr {}))) " secs"))

(b (into '() (range 1e6)))
(b (into [] (range 1e6)))
