(ns clojure-pills.defmacroz)

"Clojure Pills - 008 defmacro"

;; === a simple debugging tool ===

(/ (* 3 (+ 1 1)) 10.)
;; 0.6
;; But I'd like to see something like:
;; (+ 1 1) is 2
;; 0.6

(defn ? [form]
  (let [res (form)]
    (println form "is" res)
    res))

(/ (* 3 (? #(+ 1 1))) 10.)

(defn ? [form]
  (let [res (apply (first form) (rest form))]
    (println form "is" res)
    res))

(/ (* 3 (? '(+ 1 1))) 10.)

(type (first '(+ 1 1)))

('+ 1 1)

(defn ? [form]
	(let [res (apply (ns-resolve *ns* (first form)) (rest form))]
		(println form "is" res)
		res))

(/ (* 3 (? '(+ 1 1))) 10.)

(/ (? '(* 3 (? '(+ 1 1)))) 10.)

;; === A better approach ===

(defn ? [_ _ form]        ;; 2 additional params
  (let [res (eval form)]  ;; need to eval explicitly
    (println form "is" res)
    res))
(. (var ?) (setMacro))

(* 3 (? (+ 1 1)))
(/ (? (* 3 (? (+ 1 1)))) 10.)

(* 3 (let [res 5] (? (+ 1 res)))) ;; bang!

(* 3
   (let [res 5]
     (let [res (eval '(+ 1 res))]
       (println form "is" res)
       res)))

(eval '(+ 1 res)) ;; !!

;; === The correct approach ===

(defn ? [_ _ form]
  `(let [res# ~form]
     (println '~form '~'is res#)
     res#))
(. (var ?) (setMacro))

(* 3 (let [res 5] (? (+ 1 res)))

(defmacro ? [form]
  `(let [res# ~form]
     (println '~form '~'is res#)
     res#))

(* 3 (let [res 5] (? (+ 1 res)))) ;; finally!

(defmacro ? [form]
  `(let [res# ~form]
     (println '~&form '~'is res#)
     res#))

(* 3 (let [res 5] (? (+ 1 res)))) ;; finally!

;; === how does it work ===

(macroexpand '(defmacro simple []))

;; === contract ===

(defmacro ^:dbg ?
  "Prints the result of the intermediate evaluations."
  ([form]
   {:pre [(some? form)]}
   `(? ~form #'prn))
  ([form f]
   `(let [res# ~form]
      (~f '~form '~'is res#)
      res#)))

(* 3 (? nil)) ;; assertion error
(* 3 (? (+ 1 1) clojure.pprint/write)) ;; different printing

;; === perf ===
