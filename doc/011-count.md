## 011 count

Screencast link: https://youtu.be/5xCEZAuPnT0

Also see chapter 7 "Collections" as soon as available on the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### Summary

* Different counts for different types
* Corner cases and surprising results
* Parsing arguments example
* Counted?
* Constant time to linear time.

### At the REPL

```clojure

"Clojure Pills - 011 count"
(clojure-version)

;; each type count different things
(count [1 2 3 4 5])
(count {:a 1 :b 2})
(defrecord CanICount [a b c])
(count (CanICount. 1 2 3))

;; corner cases
(count nil)
(count (range (inc Integer/MAX_VALUE)))
(type (count [1]))

;; example
(require '[clojure.java.io :as io])

(defn- print-usage [] (println "Usage: copy 'file-name' 'to-location' ['work-dir']"))

(defn- copy
  ([in out]
    (copy in out "./"))
  ([in out dir]
    (io/copy (io/file (str dir in)) (io/file out))))

(defn -main [& args]
  (cond
    (< (count args) 2) (print-usage)
    (= 2 (count args)) (copy (first args) (second args))
    (> (count args) 2) (copy (first args) (second args) (nth args 2))))

(-main "project.clj" "/tmp/copy1.clj")
(-main "copy1.clj" "/tmp/copy2.clj" "/tmp/")

;; see also counted?

(counted? [])
(counted? '())
(counted? (for [i (range 10)] i))
(type (for [i (range 10)] i))
(counted? "asdf")
(counted? (int-array [1 2 3]))

;; perf: see summary on the slide
```
