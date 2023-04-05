(ns clojure-tasks.task1)

(defn filtered-addition [word alphabet]
  (map #(str word %) (remove #(= (str (last word)) %) alphabet))
)

(defn concatenator [word alphabet]
  (reduce concat (map #(filtered-addition % alphabet) word))
)
;(println (concatenator ["a","b","c"], ["a","b","c"]))

(defn word-maker [n alphabet]
  (reduce concatenator [""] (repeat n alphabet))
)

(defn -main []
  (print (word-maker 3, ["a","b","c","d"]))
)