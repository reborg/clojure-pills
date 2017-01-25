## 002 case

Screencast link: https://youtu.be/_f3WeoDSN-I

See also chapter 3.3.4 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.


### At the REPL

```clojure
"Clojure Pills 002: case"
(case 1 0 "0" 1 "1" :default)
(case 1 (inc 0) "1" (dec 1) "0" :default)
'dec

(case 'pi 'alpha "a" 'beta "b" 'pi "pi")
'beta
(type 'beta)
''alpha
(case 'pi (quote alpha) "a" (quote "beta") b (quote pi) "pi")
(case 'pi alpha "a" beta "b" pi "pi")

(defn calculator [op]
  (case op
    ("+" "plus") +
    ("-" "minus") -
    ("*" "times") *
    "/" /
    (constantly "uknown operand")))

((calculator "plus") 2 5)
((calculator "*") 2 5)
((calculator ".") 2 5)
(condp = 1 (inc 0) "1")

(require '[criterium.core :refer [quick-bench]])
(let [n 5] (quick-bench (condp = 5 1 1 2 2 3 3 4 4 5 5)))
(let [n 5] (quick-bench (case 5 1 1 2 2 3 3 4 4 5 5)))
