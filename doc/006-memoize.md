## 006 memoize

Screencast link: https://youtu.be/rgAyFY1oF0s

See also chapter 2.4.2 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### Summary

* Memoize contract and core goals, simple example.
* Memoizing the powerset algorithm.
* Problems with the key-space.
* Different solution and improvement.

### At the REPL

```clojure

(defn busy [a b]
  (println "busy" a b)
  (+ a b))

(busy 1 2)
(busy 1 2)

(def busymemo (memoize busy))

(busymemo 3 2)
(busymemo 3 2)

(defn powerset [[head & remaining]]
  (if head
    (let [subsets (powerset remaining)]
      (concat subsets (map (partial cons head) subsets)))
    '(())))

(powerset "abcd")
(map set (powerset "abcd"))
(count (powerset "abcd"))
(Math/pow 2 4)

(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 21))))

(def powerset
  (memoize
    (fn [[head & remaining]]
      (if head
        (let [subsets (powerset remaining)]
          (concat subsets (map (partial cons head) subsets)))
        '(())))))

(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 21))))
(time (dorun (powerset (range 21))))

(defn powerset [[head & remaining]]
  (println "Called with:" (cons head remaining))
  (if head
    (let [subsets (powerset remaining)]
      (concat subsets (map (partial cons head) subsets)))
    '(())))

(dorun (powerset "abc"))
(dorun (powerset "abcd"))
(dorun (powerset "abcde"))

(defn powerset [xs]
  (println "Called with:" xs)
  (let [remaining (butlast xs)
        head (last xs)]
    (if head
      (let [subsets (powerset remaining)]
        (concat subsets (map (partial cons head) subsets)))
      '(()))))

(dorun (powerset "abc"))
(dorun (powerset "abcd"))
(dorun (powerset "abcde"))
(powerset "abcd")

(def powerset
  (memoize
    (fn [xs]
      (let [remaining (butlast xs)
            head (last xs)]
        (if head
          (let [subsets (powerset remaining)]
            (concat subsets (map (partial cons head) subsets)))
          '(()))))))

(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 21))))
(time (dorun (powerset (range 21))))

```
