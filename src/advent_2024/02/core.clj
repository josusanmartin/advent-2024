(ns advent-2024.02.core
  (:require [clojure.string :as str]))

(defn parse-file [file-path]
  "Reads the file and parses each line into a sequence of integers."
  (->> (slurp file-path)
       (str/split-lines)
       (map (fn [line]
              (mapv #(Integer/parseInt %) (str/split line #"\s+"))))))

(defn valid-difference? [nums]
  "Checks if all adjacent numbers differ by at least one and at most three."
  (every? #(<= 1 % 3) (map #(Math/abs (- %1 %2)) nums (rest nums))))

(defn is-increasing? [nums]
  "Checks if the sequence is strictly increasing."
  (apply <= nums))

(defn is-decreasing? [nums]
  "Checks if the sequence is strictly decreasing."
  (apply >= nums))

(defn is-safe? [nums]
  "Checks if a sequence is safe (either increasing or decreasing) and meets the difference condition."
  (and (or (is-increasing? nums) (is-decreasing? nums))
       (valid-difference? nums)))

(defn can-become-safe? [nums]
  "Checks if a sequence can be made safe by removing one number."
  (if (is-safe? nums)
    true
    (some is-safe? (map #(vec (concat (subvec nums 0 %) (subvec nums (inc %))))
                        (range (count nums))))))

(defn count-safe-lines [file-path]
  "Counts the number of lines that are safe."
  (let [lines (parse-file file-path)]
    {:already-safe (count (filter is-safe? lines))
     :fixable-safe (count (filter can-become-safe? lines))}))

(defn -main []
  (let [file-path "src/advent_2024/02/02.txt"
        counts (count-safe-lines file-path)]
    (println "Number of lines already safe:" (:already-safe counts))
    (println "Number of lines safe or fixable:" (:fixable-safe counts))))