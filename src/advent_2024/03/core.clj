(ns advent-2024.03.core
  (:require [clojure.string :as str]))

(defn parse-multiplications
  "Extracts multiplication values directly from a mul(x,y) string"
  [mul-str]
  (let [[_ x y] (re-matches #"mul\((\d+),(\d+)\)" mul-str)]
    (* (Integer/parseInt x) (Integer/parseInt y))))

(defn process-content
  "Processes the content for Part 1, extracting and summing all multiplications"
  [content]
  (->> (re-seq #"mul\(\d+,\d+\)" content)
       (map parse-multiplications)
       (reduce +)))

(defn process-content-with-control
  "Processes the content for Part 2, respecting do/don't controls"
  [content]
  (loop [tokens (re-seq #"do\(\)|don't\(\)|mul\(\d+,\d+\)" content)
         active true
         sum 0]
    (if (empty? tokens)
      sum
      (let [token (first tokens)]
        (cond
          (= token "don't()") (recur (rest tokens) false sum)
          (= token "do()") (recur (rest tokens) true sum)
          active (recur (rest tokens)
                        active
                        (+ sum (parse-multiplications token)))
          :else (recur (rest tokens) active sum))))))

(defn write-debug-file [content output-path]
  (spit output-path
        (->> (re-seq #"do\(\)|don't\(\)|mul\(\d+,\d+\)" content)
             (str/join "\n")))
  (println (str "Debug content written to: " output-path)))

(defn -main []
  (let [content (slurp "src/advent_2024/03/03.txt")]
    ; Write debug files
    (write-debug-file content "src/advent_2024/03/cleaned_part1.txt")
    (write-debug-file content "src/advent_2024/03/cleaned_part2.txt")

    ; Process and print results
    (println "Part 1 - Total sum of multiplications:"
             (process-content content))
    (println "Part 2 - Total sum of multiplications (with do()/don't()):"
             (process-content-with-control content))))