(ns advent_2024.01.core
  (:require [clojure.string :as str]))

(defn parse-file [file]
  "Parses the input file into a vector of integer pairs."
  (->> (slurp file)
       (str/split-lines)
       (map (fn [line]
              (map (fn [x] (Integer. x))
                   (str/split line #"\s+"))))))

(defn sum-abs-diffs [data]
  "Sums the absolute differences between sorted columns."
  (->> (apply map vector data)
       (map sort)
       (apply map vector)
       (map (fn [[a b]] (Math/abs (- b a))))
       (reduce +)))

(defn calculate-weighted-sum [data]
  "Calculates the weighted sum based on how many times numbers in the left column appear in the right column."
  (let [left-col (map first data)
        right-col (map second data)
        freq (frequencies right-col)]
    (->> left-col
         (map (fn [n]
                (let [count (get freq n 0)]
                  (* n count))))
         (reduce +))))

(defn -main []
  (let [data (parse-file "src/advent_2024/01/01.txt")
        total (sum-abs-diffs data)
        weighted-sum (calculate-weighted-sum data)]
    (println "Total Absolute Difference:" total)
    (println "Weighted Sum:" weighted-sum)))