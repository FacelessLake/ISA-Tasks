(ns clojure-tasks.task2)

(def inf-numbers
  (lazy-seq (cons 2 (map inc inf-numbers)))
)

(defn cross-out [numbers]
  (remove #(= 0 (mod % (first numbers))) (rest numbers))
)

(def find-primes
  (map first (iterate cross-out inf-numbers))
)

(defn -main []
  (println (nth find-primes 10000))
)