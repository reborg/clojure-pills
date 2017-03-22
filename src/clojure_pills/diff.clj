(ns clojure-pills.diff)

"Clojure Pills - 009 clojure.data/diff"

;; ===== find differences in arbitrarily nested data structures =====

(require '[clojure.data :as d])

(d/diff {'a \1 :b "2"} {:b "2" :c "3"})

(d/diff {'a \1 :b [1N 2N {:k "v1"}]}
        {:c \3 :b [1N 2N {:k "v2"}]})

;; ({:b [nil nil {:k "v1"}] a \1}     // ONLY A
;;  {:b [nil nil {:k "v2"}] :c \3}    // ONLY B
;;  {:b [1N 2N]})                     // COMMON

;; ===== What can be compared? How are they compared? =====

(d/diff (hash-set 1 2 3) (sorted-set 2 3 4))
(d/diff (array-map :a 2 :b 4) (hash-map :a 2 :c 5))
(import 'java.util.ArrayList)
(d/diff (doto (ArrayList.) (.add 1) (.add 2)) (vector-of :int 1 3))

(d/diff [1 2 nil 4] [1 2 nil 5]) ;; easy to get confused by nils

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
(defn to-keys [d] (into #{} (keys (apply hash-map (remove nil? d)))))

(to-keys d1)

(defn added [d1 d2] (s/difference (to-keys d2) (to-keys d1)))
(defn removed [d1 d2] (s/difference (to-keys d1) (to-keys d2)))
(defn changed [d1 d2] (s/difference (s/union (to-keys d1) (to-keys d2)) (added d1 d2)))

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
