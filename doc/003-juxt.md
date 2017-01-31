## 003 juxt

Screencast link: https://youtu.be/-qW6H0761JQ

See also chapter 2.2.6 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### At the REPL

```clojure
((juxt first second last) (range 10))

(defn up [[x y]] [x (dec y)])
(defn down [[x y]] [x (inc y)])
(defn left [[x y]] [(dec x) y])
(defn right [[x y]] [(inc x) y])
(up [2 1])
(down [2 1])
((juxt up down left right) [2 1])
(def neighbors (juxt up down left right))
(neighbors [2 1])

(def words ["this" "book" "is" "awesome"])
(map count words)
(map (juxt count identity) words)
(map (juxt count str) words)
(sort words)
(sort-by identity words)
(sort-by (juxt count str) words)
(map (juxt count str) words)
(sort (map (juxt count str) words))
(map last (sort (map (juxt count str) words)))
(sort-by (juxt count str) words)

(doc comp)
(source juxt)
```
