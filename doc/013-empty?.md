## 013 empty? and not-empty

Screencast link: https://youtu.be/tbsGaiyqxHk

Also see chapter 7.1.6 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### Summary

* Quite easy functions to use.
* Don't confuse with (empty).
* Question mark or not to question mark?
* Small examples. Things to be aware of.
* Calls with more than one arguments.
* Two idiomatic uses.
* What else to look at?
* Can we go faster?
* Lazy sequences stay lazy, almost.

### At the REPL

```clojure

;; Some empty

(empty? [])
(empty? "")
(empty? nil)
(empty? (int-array []))

;; Some full

(empty? [1 2 3])
(empty? (clojure.lang.PersistentQueue/EMPTY))
(empty? " ")
(empty? "nil")
(empty? [nil])
(empty? [[]])

;; Some not working

(empty? \space)
(empty? false)
(empty? (transient []))

;; And what about not-empty

(not-empty [])
(not-empty [1])
(not-empty nil)

;; Idioms

(remove empty? [nil "a" {} "" (range 10)])

(defn is-digit [s] (every? #(Character/isDigit %) s))
(defn to-num [s] (and (not-empty s) (is-digit s) (Long/valueOf s)))

(to-num nil)
(to-num "")
(to-num "a")
(when-let [n (to-num "12")] (* 2 n))
(when-let [n (to-num "12A")] (* 2 n))

;; Sources

(source empty?)

(let [coll [1 2 3]] (seq coll))

;; Perf

(require '[criterium.core :refer [quick-bench]])

(let [v (vec (range 1000))] (quick-bench (empty? v)))

(let [v (vec (range 1000))] (quick-bench (zero? (count v))))

(let [v (vec (range 1000))] (quick-bench (.isEmpty ^java.util.Collection v)))

;; Lazy

(empty? (map #(do (println "realizing" %) %) (range 100)))
(zero? (count (map #(do (println "realizing" %) %) (range 100))))

```
