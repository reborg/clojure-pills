## 009 diff

Screencast link: https://youtu.be/-KQ5K-MW4AU

Also see chapter 6.6 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### Summary

* Need to require namespace, early example of usage of protocols
* Arbitrarily nested data structures
* Comparable categories
* What is not comparable?
* How to use the results of diff to build additional processing of differences
* Complexity of traversal, stack consumption.

### At the REPL

```clojure
(require '[clojure.data :as d])

(d/diff {'a \1 :b "2"} {:b "2" :c "3"})

(d/diff {'a \1 :b [1N 2N {:k "v1"}]}
        {:c \3 :b [1N 2N {:k "v2"}]})

;; ===== What can be compared? How are they compared? =====

(d/diff (hash-set 1 2 3) (sorted-set 2 3 4))
(d/diff (array-map :a 2 :b 4) (hash-map :a 2 :c 5))
(import 'java.util.ArrayList)
(d/diff (doto (ArrayList.) (.add 1) (.add 2)) (vector-of :int 1 3))

;; ===== Careful! =====

(d/diff (vector 1 2 3) (sorted-set 2 3 4))
(d/diff [1 2 nil 4] [1 2 3 4]) ;; easy to get confused by nils
(d/diff [1 2 3] [1. 2. 3.]) ;; careful, "= semantic" not ==

;; ===== How to use? For example test automation for html/json/edn  =====

(require '[clojure.edn :as edn])
(def prj1 (edn/read-string (slurp "project.clj")))
(def prj2 (edn/read-string (slurp "sample-project.clj")))
(def d (d/diff prj1 prj2))

(def d1 (first d))
(def d2 (second d))
(def d3 (last d))

(require '[clojure.set :as s])
(defn to-hash [d] (apply hash-map (remove nil? d)))
(defn to-keys [d] (into #{} (keys (to-hash d))))

(to-keys d1)

(defn added [d1 d2] (s/difference (to-keys d2) (to-keys d1)))
(defn removed [d1 d2] (s/difference (to-keys d1) (to-keys d2)))
(defn changed [d1 d2] (s/difference (s/union (to-keys d1) (to-keys d2)) (added d1 d2)))

(added d1 d2)
(removed d1 d2)
(changed d1 d2)

[(:dependencies (to-hash d1)) (:dependencies (to-hash d2))]

;; ===== Implementation details =====

(defn generate [n]
  (reduce (fn [m e] (assoc-in m (range e) {e e})) {} (range 1 n)))

(generate 10)
(generate 11)
(d/diff (generate 10) (generate 11))

;; short-circuiting
(doseq [i (range 10 20)]
  (print i "level deep: ")
  (time (d/diff (generate i) (generate i))))

;; walk-all worst case
(doseq [i (range 10 20)]
  (print i "level deep: ")
  (time (d/diff (generate i) (generate (inc i)))))

```
