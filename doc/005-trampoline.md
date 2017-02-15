## 005 trampoline

Screencast link: https://youtu.be/-Ul-XbIUiqE

See also chapter 2.4.3 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

### At the REPL

```clojure
(source trampoline)
(fn? +)
([1 2 3] 2)
(fn? [])
(ifn? [])

(defn caller [f n]
  (let [ret (f)]
    (if (zero? n)
      ret
      (caller f (dec n)))))

(caller #(println ".") 10)
(caller + 10)
(+)
(caller + 10)
(caller + 10000)

(defn caller [f n]
  (let [ret (f)]
    (if (zero? n)
      ret
      #(caller f (dec n)))))

(caller + 2)
(((caller + 2)))
(trampoline caller + 10)
(trampoline caller + 2)

(declare is-odd?)
(defn is-even? [n] (or (zero? n) (is-odd? (dec n))))
(defn is-odd? [n] (and (not (zero? n)) (is-even? (dec n))))
(is-even? 120)
(is-even? 121)
(is-odd? 121)
(is-odd? 12000)
(defn is-even? [n] (or (zero? n) #(is-odd? (dec n))))
(defn is-odd? [n] (and (not (zero? n)) #(is-even? (dec n))))
(trampoline is-odd? 12000)
```
