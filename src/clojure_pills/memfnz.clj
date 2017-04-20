(ns clojure-pills.memfnz)

"Clojure Pills - 012 memfn"

;; ============== contract

(def toLower (memfn toLowerCase))
(toLower "ABBA")

(map toLowerCase ["ONE" "TWO"]) ; <!>

(map (memfn toLowerCase) ["ONE" "TWO"])

(map (memfn indexOf ch) ["abba" "trailer" "dakar"] ["a" "i" "k"])
(map (memfn indexOf 😱) ["abba" "trailer" "dakar"] ["a" "i" "k"])

;; ============== example

(import '[java.time Instant Duration])

(def instants
  (repeatedly (fn []
    (Thread/sleep 100)
    (Instant/now))))

(take 10 instants)

(defn durations [instants & [t0]]
  (let [start (or t0 (Instant/now))]
    (->> instants
         (map #(Duration/between % start))
         (map (memfn toMillis))
         (map #(/ % 1000.)))))

(let [two (doall (take 2 instants))] (durations two))

(let [t1 (Instant/now)
      times (doall (take 2 instants))]
  (Thread/sleep 200)
  (first (durations times t1)))

;; ============== impl details

(macroexpand '(memfn toUpperCase))
(macroexpand '(memfn indexOf ch))

;; ============== perf

(source memfn)

(set! *warn-on-reflection* true)

(time (dotimes [n 100000]
  (map (memfn toLowerCase) ["A" "B"])))

(time (dotimes [n 100000]
  (map (memfn ^String toLowerCase) ["A" "B"])))
