(ns advent-2024.04.core
  (:require [clojure.string :as str]))

(defn parse-file [file-path]
  "Reads the file and parses it into a 2D grid of characters."
  (->> (slurp file-path)
       (str/split-lines)
       (mapv vec)))

(defn find-xmas [grid word]
  "Finds all occurrences of the word XMAS in all directions."
  (let [word-length (count word)
        reversed-word (apply str (reverse word))
        rows (count grid)
        cols (count (first grid))]
    (letfn [(horizontal []
              (for [row-idx (range rows)
                    col-idx (range (- cols (dec word-length)))
                    :let [substring (subs (apply str (grid row-idx)) col-idx (+ col-idx word-length))]
                    :when (or (= substring word) (= substring reversed-word))]
                true))
            (vertical []
              (for [col-idx (range cols)
                    row-idx (range (- rows (dec word-length)))
                    :let [substring (apply str (map #(get-in grid [% col-idx]) (range row-idx (+ row-idx word-length))))]
                    :when (or (= substring word) (= substring reversed-word))]
                true))
            (diagonal []
              (concat
                ;; Top-left to bottom-right
               (for [row-idx (range (- rows (dec word-length)))
                     col-idx (range (- cols (dec word-length)))
                     :let [substring (apply str (map #(get-in grid [(+ row-idx %) (+ col-idx %)]) (range word-length)))]
                     :when (or (= substring word) (= substring reversed-word))]
                 true)
                ;; Top-right to bottom-left
               (for [row-idx (range (- rows (dec word-length)))
                     col-idx (range (dec word-length) cols)
                     :let [substring (apply str (map #(get-in grid [(+ row-idx %) (- col-idx %)]) (range word-length)))]
                     :when (or (= substring word) (= substring reversed-word))]
                 true)))]
      (count (concat (horizontal) (vertical) (diagonal))))))

(defn find-x-mas [grid]
  "Finds all occurrences of X-MAS patterns in the grid."
  (let [rows (count grid)
        cols (count (first grid))
        valid-mas ["MAS" "SAM"] ;; Valid combinations for each arm
        results (for [row-idx (range 1 (dec rows))
                      col-idx (range 1 (dec cols))
                      :let [center (get-in grid [row-idx col-idx])
                            top-left (get-in grid [(dec row-idx) (dec col-idx)])
                            top-right (get-in grid [(dec row-idx) (inc col-idx)])
                            bottom-left (get-in grid [(inc row-idx) (dec col-idx)])
                            bottom-right (get-in grid [(inc row-idx) (inc col-idx)])
                            diag1 (str top-left center bottom-right)
                            diag2 (str top-right center bottom-left)]
                      :when (and (= center \A)
                                 (some #{diag1} valid-mas)
                                 (some #{diag2} valid-mas))]
                  {:center [row-idx col-idx]
                   :top-left top-left
                   :top-right top-right
                   :bottom-left bottom-left
                   :bottom-right bottom-right})]
    results))

(defn -main []
  (let [file-path "src/advent_2024/04/04.txt"
        grid (parse-file file-path)
        part1-total (find-xmas grid "XMAS")
        x-mas-occurrences (find-x-mas grid)
        part2-total (count x-mas-occurrences)]
    ;; Print results
    (println "Total XMAS occurrences:" part1-total)
    (println "Total X-MAS occurrences:" part2-total)))