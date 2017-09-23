(require '[clojure.core.reducers :as r])

;; Fold looks a lot like reduce
(r/fold + (range 1000))
(r/fold + (map inc (filter odd? (range 1000))))

;; But carries a secret wapon (for vectors and maps)
(let [coll (doall (range 1e7))] (time (r/fold + coll)))
(let [coll (vec (range 1e7))] (time (r/fold + coll)))

;; pic

;; fork-join fits nicely to process at the leaf,
;; but this is not happening if we go "plain sequential":
(->> (vec (range 1000))
     (map inc)
     (filter odd?)
     (map #(do (println (str (Thread/currentThread))) %))
     (r/fold +))

;; reducers allow deferred processing to happen on each chunk
(->> (vec (range 1000))
     (r/map inc)
     (r/filter odd?)
     (r/map #(do (println (str (Thread/currentThread))) %))
     (r/fold +))

;; Additional knobs.
;; combinef can be independent from reducef
(r/fold * / (vec (range 1 600)))

;; desired chunk size can change (512 default)
(r/fold 400 * / (vec (range 1 10000)))

;; either reducef (but combinef takes precedence) zero-arity is used to define init
(r/fold
  (fn combinef
    ([] (println "init -500") -500)
    ([a b] (+ a b)))
  (fn reducef
    ([] (println "init 0") 0)
    ([a b] (+ a b)))
  (r/map inc (r/filter odd? (vec (range 1000)))))

;; monoid is an helper for that
(r/fold (r/monoid + (constantly -500)) + (r/map inc (r/filter odd? (vec (range 1000)))))

;; when working on maps, reducef has 3 args (like reduce-kv)
(r/fold merge (fn [m k v] (assoc m k (str v))) (zipmap (range 2000) (range 2000)))

;; Extended example: calculating word-frequencies
(require '[clojure.string :refer [split]])

(defn words [coll]
  (let [combinef (r/monoid #(merge-with + %1 %2) (constantly {}))
        reducef (fn [m [k cnt]] (assoc m k (+ cnt (get m k 0))))]
    (r/fold
      combinef
      reducef
      (r/map #(vector % 1) coll))))

(def freqs
  (-> "http://www.gutenberg.org/files/2600/2600-0.txt"
      slurp
      (split #"\s+")
      words))

(take 10 (sort-by second > freqs))

;; Careful, some reducers are not foldable (r/drop, r/take, r/take-while)
;; this is going to be sequential even if I'm dropping nothing
(->> (range 1000)
     (into [])
     (r/map range)
     (r/mapcat conj)
     (r/drop 0)
     (r/map #(do (println (str (Thread/currentThread))) %))
     (r/filter odd?)
     (r/fold +))

;; With transducers, just invoke xform on reducef to go parallel:
(r/fold ((comp (map inc) (filter odd?)) +) (vec (range 1000)))

;; Careful! Compared to reducers, stateful transducers
;; are in concurrent access danger:
(distinct
  (for [i (range 5000)]
    (r/fold ((comp (map inc) (drop 10)) +) (vec (range 1000)))))

;; A trick to get them right is explained here:
;; https://gist.github.com/reborg/6cef0d83a5035363bd242510d50dfd2a

;; How it compares to pmap? Completely different models.
;; * pmap on vectors runs concurrently on a number of threads which is multiple of 32 (the default size of a lazy sequence chunk).
;; * pmap on a lazy-seq will run num.core + 2 thread concurrently.
;; * fold run on num-core concurrent threads. But if a thread is free, it goes stealing work from a busy one.
;; The choice bois down to: task predictability, laziness, concurrency model.
;; Here's the point about predictability:

(def clock (atom nil))
(def assorted-tasks (map vector (range) (shuffle (concat (range 0 90) (range 3500 3510)))))
(defn pi [n] (->> (range) (filter odd?) (take n) (map / (cycle [1 -1])) (reduce +) (* 4.0)))

(let [start (reset! clock (System/currentTimeMillis))]
  (dorun
    (pmap (fn [[idx t]]
            (println "exec" idx t)
            (if (= 99 idx)
              (swap! clock #(- (System/currentTimeMillis) %))
              (pi t)))
          assorted-tasks))
  @clock)

(time (r/fold 1 (constantly nil) (fn [_ [idx t]] (pi t)) (vec assorted-tasks)))

;; more experiements.
;; Artificial number of cores up to 32. This is goind to execute 64 concurrent threads (and possibly freeze your system!)
(let [pi (fn [n] (->> (range) (filter odd?) (take n) (map / (cycle [1 -1])) (reduce +) (* 4.0)))
      coll (vec (range 2000 2250))
      n (+ 2 32)
      rets (map #(future (pi %)) coll)
      step (fn step [[x & xs :as vs] fs]
             (lazy-seq
               (if-let [s (seq fs)]
                 (cons (deref x) (step xs (rest s)))
                 (map deref vs))))]
  (step rets (drop n rets)))

;; by Rich: https://groups.google.com/d/msg/clojure/7pXvjjBhzp8/lvBydUXcaN0J
;; pmap for vectors very similar to fork-join but with agents. No work stealing.
(defn vector-pmap [f v]
  (let [n (+ 2 (.. Runtime getRuntime availableProcessors))
        sectn (quot (count v) n)
        agents (map #(agent (subvec v (* sectn %) (min (count v) (+ (* sectn %) sectn)))) (range n))]
    (doseq [a agents] (send a #(doall (map f %))))
    (apply await agents)
    (into [] (apply concat (map deref agents)))))

(time (dorun
  (vector-pmap
    (fn [[idx t]]
      (println "exec" idx t)
      (if (= 99 idx)
        (swap! clock #(- (System/currentTimeMillis) %))
        (pi t)))
    (vec assorted-tasks))))
