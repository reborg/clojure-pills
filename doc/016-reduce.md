## 016 reduce

Screencast link: https://youtu.be/pHbossQGcU8

Also see chapter 3.5.4 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

#### summary

Intro: a stack consuming VS iterative recursion
Reduce is the prototypical recursive iterative process:
Contract
Example: counting words (aka frequencies redo)
See also:
Performances
See chart
![perf](https://github.com/reborg/clojure-pills/blob/master/doc/reduce-bench.png)

### At the REPL

```clojure

"Clojure Pills - 016 reduce"

;; ============== intro: a stack consuming VS iterative recursion

(defn fibo [n]
  (condp = n
    0 0
    1 1
    (+ (fibo (- n 1)) (fibo (- n 2)))))

(fibo 10)
(fibo 10000) ;; stackover

(defn fibo [n]
  (loop [a 1 b 0 cnt n]
    (if (zero? cnt)
      b
      (recur (+' a b) a (dec cnt)))))

(fibo 10000)

;; reduce is the prototypical recursive iterative process:

(defn fibo [n]
  (reduce
    (fn [[a b] cnt]
      (if (zero? cnt)
        b
        [(+' a b) a]))
    [1 0]
    (range n -1 -1)))

;; ============== contract

(instance? clojure.lang.Seqable [])
(instance? clojure.lang.Seqable (transient []))
(reduce + (transient (into [] (range 10))))

(defn dbg
  ([] (println "0-arity"))
  ([x] (println "1-arity with" x))
  ([x y] (println "2-arity with" x y))
  ([x y & z] (println "var-arg with" x y z)))

(reduce dbg nil)
(reduce dbg [])
(reduce dbg "val" [])

;; ============= Example: counting words (aka frequencies redo)

(defn count-occurrences [coll]
  (->> coll
       (map #(vector % 1))
       (reduce (fn [m [k cnt]]
         (assoc m k (+ cnt (get m k 0)))) {})))

(defn word-count [s]
  (count-occurrences (.split #"\s+" s)))

(word-count "Counting all words, all the words and sentences.")

;; ============= See also:

(reduce-kv (fn [m k v] (assoc m (keyword v) (name k))) {} {:a "1" :b "2"})
(reductions + 0 (range 10))
(reductions (fn [acc itm] (if (> itm 5) (reduced (+ itm acc)) (+ itm acc))) (range 10))

;; ============= Performances

;; see chart

(let [xs (range 1e8)] (reduce + xs))
(take 10 (reduce merge '() (range 1e8))) ;; ops
(let [xs (range 1e8)] (last xs) (reduce + xs)) ;; ops

```

