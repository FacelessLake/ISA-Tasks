(ns clojure-tasks.task5)

(defn forks
  [fork-counter]
  (map
    (fn [_] [(ref 0) (atom 0)])
    (range fork-counter)
  )
)

(defn print-forks
  [forks]
  (println (concat "" (map (fn [[r a]] [@r @a]) forks)))
  forks
)

(defn fork-pairs
  [forks]
  (cons
    [(last forks) (first forks)]
    (map
      (fn [x y] [(nth forks x) (nth forks y)])
      (range (dec (count forks)))
      (range 1 (count forks))
    )
  )
)

(defn eating-and-thinking
  [l-fork r-fork thinking eating dish-amount]
  (fn task []
    (reduce
      (fn [_ _]
        (Thread/sleep thinking)
        (dosync
          (swap! (last l-fork) inc)                         ;for atomic changing
          (swap! (last r-fork) inc)
          (alter (first l-fork) inc)                        ;for ref changing
          (alter (first r-fork) inc)
          (Thread/sleep eating)
        )
      )
      (range (inc dish-amount))
    )
  )
)

(defn philosophers
  [forks thinking eating dish-amount]
  (doall
    (map
      (fn [[l-fork r-fork]]
        (new Thread
          (eating-and-thinking l-fork r-fork thinking eating dish-amount)
        )
      )
    (fork-pairs forks))
  )
)

(defn dinner
  [fork-count thinking eating dish-amount]
  (let
    [ forks (forks fork-count)
      philosophers (philosophers forks thinking eating dish-amount)
    ]
    (doall (map #(.start %) philosophers))
    (doall (map #(.join %) philosophers))
  forks)
)

(def fork-count 5)
(def thinking 10)
(def eating 10)
(def dish-amount 2)

(defn -main []
  (print-forks (time (dinner fork-count thinking eating dish-amount)))
)