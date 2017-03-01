## 007 subvec

Screencast link: https://youtu.be/-TqI45IXdCk

Not yet released, but available soon as a chapter of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### Summary

* `subvec` vector operation, context, reasons, relationship with `mapv`, `filterv` and `reduce-kv`.
* `subvec` contract, notable exceptions, relationship with referenced vector.
* `remove-at` example.
* `pmapv` example.
* performance: overall speed.
* performance: GC side effect on retained vector.

### At the REPL

```clojure

;; ============== contract

(subvec [1 2 3 4] 1 3)
(subvec [1 2 3 4] 1)

(def subv (subvec (vector-of :int 1 2 3) 1))
(conj subv \a)
(conj subv nil) ;; !

;; ============== example 1

(defn remove-at [v idx]
	(into (subvec v 0 idx)
				(subvec v (inc idx) (count v))))

(remove-at [0 1 2 3 4 5] 3)

;; ============== example 2

(defn firstv [v] (nth v 0))
(defn restv [v] (subvec v 1))

(defn norm [v]
  (loop [v v
         res 0.]
    (if (= 0 (count v))
      (Math/sqrt res)
      (recur (restv v)
             (+ res (Math/pow (firstv v) 2))))))

(norm [-3 4])

;; ============== example 3

;; Straight from reducers.clj

(def pool (delay (java.util.concurrent.ForkJoinPool.)))
(defn fjtask [^Callable f] (java.util.concurrent.ForkJoinTask/adapt f))

(defn- fjinvoke [f]
  (if (java.util.concurrent.ForkJoinTask/inForkJoinPool)
    (f)
    (.invoke ^java.util.concurrent.ForkJoinPool
             @pool ^java.util.concurrent.ForkJoinTask
             (fjtask f))))

(defn- fjfork [task] (.fork ^java.util.concurrent.ForkJoinTask task))
(defn- fjjoin [task] (.join ^java.util.concurrent.ForkJoinTask task))

(defn pmapv [f v & [n]]
  (let [n (or n (+ 2 (.. Runtime getRuntime availableProcessors)))]
    (cond
      (empty? v) []
      (<= (count v) n) (mapv f v)
      :else
      (let [split (quot (count v) 2)
            v1 (subvec v 0 split)
            v2 (subvec v split (count v))
            fc (fn [child] #(pmapv f child n))]
        (fjinvoke
          #(let [f1 (fc v1)
                 t2 (fjtask (fc v2))]
             (fjfork t2)
             (into (f1) (fjjoin t2))))))))

(defn slow-inc [n] (do (Thread/sleep 10) (inc n)))

(let [v (vec (range 1000))] (dorun (time (mapv slow-inc v))))
(let [v (vec (range 1000))] (dorun (time (pmapv slow-inc v))))

;; ============== perf 1

(require '[criterium.core :refer [quick-bench]])

(defn norm [v]
  (loop [v v
         res 0.
         idx (dec (count v))]
    (if (< idx 0)
      (Math/sqrt res)
      (recur (subvec v 0 idx)
             (+ res (Math/pow (peek v) 2))
             (dec idx)))))

(let [v (vec (range 1000))] (quick-bench (norm v)))

(defn norm-idx [v]
  (loop [idx (dec (count v))
         res 0.]
    (if (< idx 0)
      (Math/sqrt res)
      (recur (dec idx)
             (+ res (Math/pow (nth v idx) 2))))))

(let [v (vec (range 1000))] (quick-bench (norm-idx v)))

;; ============== perf 2

(defn bigv [n] (vec (range n)))

(let [v1 (subvec (bigv 1e7) 0 5)
      v2 (subvec (bigv 1e7) 5 10)]
      (into v1 v2))

(let [v1 (into [] (subvec (bigv 1e7) 0 5))
      v2 (into [] (subvec (bigv 1e7) 5 10))]
      (into v1 v2))

```
