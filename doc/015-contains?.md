## 015 contains?

Screencast link: https://youtu.be/Ln95283Anww

Also see chapter 7.2.3 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

#### summary

* common use
* Quiz: so given a record (map-like) what is the result of the following?
* less common, with numerically indexed colls
* it must be a number
* it must be below 2^32
* can be used to check for nils
* extended example
* see also
* implementation details and perfs

### At the REPL

```clojure

"Clojure Pills - 015 contains?"

;; common use

(contains? {:a "a" :b "b"} :b)
(contains? {:a "a" :b "b"} "b")
(contains? #{:x :y :z} :z)

;; Quiz: so given a record (map-like) what is the result of the following?

(defrecord A [x y z])
(def myA (A. 1 2 3))
(contains? myA 1)

;; less common, with numerically indexed colls

(contains? [:a :b :c] 1)
(contains? "1234" 4)

;; it must be a number

(contains? [:a :b :c] :a)

;; it must be below 2^32

(def power-2-32 (long (Math/pow 2 32)))
(contains? [1 2 3] power-2-32)

;; can be used to check for nils

(contains? #{1 2 nil 3 4} nil)
(#{1 2 nil 3 4} nil)

;; extended example

(def sensor-read
  [{:id "AR2" :location 2 :status "ok"}
   {:id "EF8" :location 2 :status "ok"}
   nil
   {:id "RR2" :location 1 :status "ok"}
   nil
   {:id "GT4" :location 1 :status "ok"}
   {:id "YR3" :location 4 :status "ok"}])

(defn problems? [sensors]
  (contains? (into #{} sensors) nil))

(defn raise-on-error [sensors]
  (if (problems? sensors)
    (throw (RuntimeException.
      "At least one sensor is malfunctioning"))
    :ok))

(raise-on-error sensor-read)

;; see also

(some #{:a} [:a :b :c])
(.contains [:a :b :c] :c)
(.contains "verylongstring" "long")

;; implementation details and perfs

contains?

```

![perf](https://github.com/reborg/clojure-pills/blob/master/doc/contains-by-type.png)
