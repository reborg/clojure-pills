## 001 fnil

Screencast link: https://www.youtube.com/watch?v=uDOvBAcApC4

See also chapter 2.2.1 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.


### At the REPL

```clojure
"Clojure Pills 001: fnil"
(doc fnil)
(source fnil)

(re-find #"hello" "Hey hello you")
(re-find #"hello" nil)
(def re-find+ (fnil re-find #"" ""))
(re-find+ #"hello" nil)
(re-find+ nil nil)

(when (some? s) (re-find #"hello" s))
(def s "hello")
(when (some? s) (re-find #"hello" s))
(def s nil)
(when (some? s) (re-find #"hello" s))
(re-find+ nil nil)

(fnil + 0 0 0 0 0)
(fnil + 0 0 0)
(defn fnil+ [f & opts]
  (fn [& args]
  (apply f (map #(if (nil? %1) %2 %1) args (concat opts (repeat nil))))))

(def ++ (fnil+ + 0 0 0 0 0))
(+ 1 2 nil 4 5)
(++ 1 2 nil 4 5)

(some->> "hello" (re-find #"hello"))
(some->> nil (re-find #"hello"))
(some->> s (re-find #"hello"))

(source fnil)
(def ++ (apply fnil+ (range 1e9)))
(++ 1 2 3)
(def ++ (apply fnil+ + (range 1e9)))
(++ 1 2 3)
(time (++ 1 2 3))
(time (apply ++ 1 2 3 (range 1e7)))
(time (apply + 1 2 3 (range 1e7)))
```
