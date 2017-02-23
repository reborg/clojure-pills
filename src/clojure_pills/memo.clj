(ns clojure-pills.memo)

"Clojure Pills - 006 Memoize"

(defn busy [a b]
  (println "busy" a b)
  (+ a b))

(busy 1 2)
(busy 1 2)

(def busymemo (memoize busy))

(busymemo 3 2)
(busymemo 3 2)

(defn powerset [[head & remaining]]
  (if head
    (let [subsets (powerset remaining)]
      (concat subsets (map (partial cons head) subsets)))
    '(())))

(powerset "abcd")
(map set (powerset "abcd"))
(count (powerset "abcd"))
(Math/pow 2 4)

(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 21))))

(def powerset
  (memoize
    (fn [[head & remaining]]
      (if head
        (let [subsets (powerset remaining)]
          (concat subsets (map (partial cons head) subsets)))
        '(())))))

(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 21))))
(time (dorun (powerset (range 21))))

(defn powerset [[head & remaining]]
  (println "Called with:" (cons head remaining))
  (if head
    (let [subsets (powerset remaining)]
      (concat subsets (map (partial cons head) subsets)))
    '(())))

(dorun (powerset "abc"))
(dorun (powerset "abcd"))
(dorun (powerset "abcde"))

(defn powerset [xs]
  (println "Called with:" xs)
  (let [remaining (butlast xs)
        head (last xs)]
    (if head
      (let [subsets (powerset remaining)]
        (concat subsets (map (partial cons head) subsets)))
      '(()))))

(dorun (powerset "abc"))
(dorun (powerset "abcd"))
(dorun (powerset "abcde"))
(powerset "abcd")

(def powerset
  (memoize
    (fn [xs]
      (let [remaining (butlast xs)
            head (last xs)]
        (if head
          (let [subsets (powerset remaining)]
            (concat subsets (map (partial cons head) subsets)))
          '(()))))))

(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 10))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 20))))
(time (dorun (powerset (range 21))))
(time (dorun (powerset (range 21))))

(defn memoize2 [f]
  (let [mem (atom {})
        hits (atom 0)
        miss (atom 0)
        calls (atom 0)]
    (fn [& args]
      (if (identical? :stats (first args))
        {:calls @calls
         :hits @hits
         :misses @miss
         :keys (count @mem)}
        (do
          (swap! calls inc)
          (if-let [e (find @mem args)]
            (do (swap! hits inc) (val e))
            (let [ret (apply f args)]
              (swap! miss inc)
              (swap! mem assoc args ret)
              ret)))))))

(def powerset
  (memoize2
    (fn [xs]
      (let [remaining (butlast xs)
            head (last xs)]
        (if head
          (let [subsets (powerset remaining)]
            (concat subsets (map (partial cons head) subsets)))
          '(()))))))

(time (dorun (powerset (range 20)))) (powerset :stats nil)
(time (dorun (powerset (range 20)))) (powerset :stats nil)
(time (dorun (powerset (range 21)))) (powerset :stats nil)
(time (dorun (powerset (range 22)))) (powerset :stats nil)
