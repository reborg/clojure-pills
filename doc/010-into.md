## 010 into

Screencast link: https://youtu.be/XMz14mxRFog

Also see chapter 7 "Collections" as soon as available on the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### Summary

* "changing" the type of a collection
* follows "conj" semantic
* merging and nils
* transducers enabled!
* metadata handling
* mutables are not supported
* Example: keeping the original type
* Implementation details and performances

### At the REPL

```clojure
(into #{} (range 10))
(into [] (range 10))
(into (sorted-set) (range 10))
(into {} [[:a 1] [:b 2] [:c 3]])

(into '() [1 2 3])
(import 'clojure.lang.PersistentQueue)
(def q (into (PersistentQueue/EMPTY) [1 2 3]))
(peek q)

(into (range 10 20) (range 10))
(into nil #{1 2 3})
(into nil nil)

(into (vector-of :int)
      (comp (map inc) (filter even?))
      (range 10))

(defn sign [c] (with-meta c {:signature (apply str c)}))
(meta (into (sign [1 2 3]) (sign (range 10))))

(into (transient []) (range 10))
(into (int-array []) (range 10))
(import 'java.util.ArrayList) (into (ArrayList.) (range 10))

(defn maintain [fx f coll]
  (into (empty coll) (fx f coll)))

(->> #{1 2 3 4 5} (maintain map inc) (maintain filter odd?))

(->> {:a 1 :b 2 :c 5} (maintain filter (comp odd? last)))


(require '[criterium.core :refer [quick-benchmark]])

(defmacro b [expr]
  `(str (first (:mean (quick-benchmark ~expr {}))) " secs"))

(b (into '() (range 1e6)))
(b (into [] (range 1e6)))
```
