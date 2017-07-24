## 004 range

Screencast link: https://youtu.be/-Ul-XbIUiqE

See also chapter 3.4.2 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### At the REPL

```clojure
"Clojure Pills - 004 range"

;; longs, pos and neg
(range 10)
(range 5 10)
(range 0 10 2)
(range 20 10 -2)

;; doubles and ratios
(range 0 5 0.5)
(range 0 1 1/10)
(take 5 (range))

;; auto-promotion
(range (-' Long/MAX_VALUE 3) (+' Long/MAX_VALUE 3))
(map type (range (-' Long/MAX_VALUE 3) (+' Long/MAX_VALUE 3)))

;; checking for palindrome sequences

(def s "was it a car or a cat i saw")

(defn palindrome? [xs]
  (let [cnt (count xs) idx (range (quot cnt 2) 0 -1)]
    (every? #(= (nth xs (dec %) ) (nth xs (- cnt %))) idx)))

(palindrome? (remove #(= \space %) s))
;; true

(def s-2 "was it e car or a cat i saw")
(palindrome? (remove #(= \space %) s-2))
;; false

;; see also iterate
(source range)
(take 10 (iterate not true))

;; range laziness
(def s (range 1e20))
(def xs (map #(do (print ".") %) (range 100)))
(take 5 xs)
(last (take 33 xs))

;; at the profiler (see below)
(last (range 1000000000))

```

Visualizing `range` laziness at the profiler. In general you need to pay attention at not retaining the head of the sequence, which sometimes happens in subtle ways.

![range at the profiler](https://github.com/reborg/clojure-pills/blob/master/pics/004-range.png)
