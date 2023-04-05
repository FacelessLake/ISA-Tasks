(ns clojure-tasks.task3)

(def default-thread-num 40)
(def default-block-size 100)

(def inf-numbers
  (lazy-seq (cons 0 (map inc inf-numbers)))
)

(defn parallel-filter
  ([predicate collection threads-number]
    (mapcat deref
      (doall
        (map
          #(future (doall (filter predicate %))) (partition-all threads-number collection)
        )
      )
    )
  )
)

(defn spliterator
  [collection chunk-size]
  (take-while #(not (empty? %))
    (map
      #(take chunk-size %)
      (iterate
        #(drop chunk-size %) collection
      )
    )
  )
)


(defn my-filter
  ([predicate collection] (my-filter predicate collection default-thread-num))
  ([predicate collection threads-number] (my-filter predicate collection threads-number default-block-size))
  ([predicate collection threads-number block-size]
    (mapcat
      #(parallel-filter predicate % threads-number)
      (spliterator collection
        (* threads-number block-size)
      )
    )
  )
)

(defn heavy_even? [arg]
  (Thread/sleep 1)
  (even? arg)
)

(defn -main []
  (println (my-filter neg? [1 2 3 -4 5 -6 -7]))
  (println (time (nth (my-filter heavy_even? inf-numbers) 1000)))
  (println (time (nth (filter heavy_even? inf-numbers) 1000)))
)