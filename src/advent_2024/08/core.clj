(ns advent-2024.08.core
  (:require [clojure.string :as str]))

(defn all-pairs [coll]
  (for [i (range (count coll))
        j (range (inc i) (count coll))]
    [(nth coll i) (nth coll j)]))

(defn read-grid [path]
  (let [lines (str/split-lines (slurp path))]
    (mapv vec lines)))

(defn occurrences-map [grid]
  (let [height (count grid)
        width (count (first grid))]
    (loop [y 0 m {}]
      (if (>= y height)
        m
        (let [row (grid y)
              m (reduce (fn [m x]
                          (let [ch (row x)]
                            (if (not= ch \.)
                              (update m ch conj [y x])
                              m)))
                        m
                        (range width))]
          (recur (inc y) m))))))

(defn distance [y1 x1 y2 x2]
  (Math/sqrt (+ (* (- y2 y1) (- y2 y1))
                (* (- x2 x1) (- x2 x1)))))

(defn set-char [grid y x ch]
  (if (or (< y 0) (>= y (count grid))
          (< x 0) (>= x (count (first grid))))
    grid
    (assoc grid y (assoc (grid y) x ch))))

;; PART 1: Two antinodes per pair
(defn place-antinodes [grid ch-occurrences]
  (reduce (fn [g [[y1 x1] [y2 x2]]]
            (let [dist (distance y1 x1 y2 x2)
                  dy (- y2 y1)
                  dx (- x2 x1)
                  uy (/ dy dist)
                  ux (/ dx dist)
                  yA (long (Math/round (- y1 (* uy dist))))
                  xA (long (Math/round (- x1 (* ux dist))))
                  yB (long (Math/round (+ y2 (* uy dist))))
                  xB (long (Math/round (+ x2 (* ux dist))))]
              (-> g
                  (set-char yA xA \#)
                  (set-char yB xB \#))))
          grid
          (all-pairs ch-occurrences)))

;; PART 2: Every point on the line defined by two antennas of the same frequency is an antinode.
(defn gcd [a b]
  (if (zero? b) (Math/abs a) (recur b (mod a b))))

(defn in-bounds? [grid y x]
  (and (>= y 0) (< y (count grid))
       (>= x 0) (< x (count (first grid)))))

(defn place-antinodes-new [grid ch-occurrences]
  (let [height (count grid)
        width (count (first grid))
        antinodes (atom (into #{} (for [y (range height)
                                        x (range width)
                                        :when (not= ((grid y) x) \.)]
                                    [y x])))]
    ;; We start antinodes set with antenna positions themselves because in the new approach,
    ;; any antenna with a pair also is an antinode.
    (doseq [[p1 p2] (all-pairs ch-occurrences)]
      (let [[y1 x1] p1
            [y2 x2] p2
            dy (- y2 y1)
            dx (- x2 x1)
            g (gcd dy dx)
            dyu (/ dy g)
            dxu (/ dx g)]
        ;; Move forward (k >= 0)
        (loop [k 0]
          (let [Y (+ y1 (* k dyu))
                X (+ x1 (* k dxu))]
            (when (in-bounds? grid Y X)
              (swap! antinodes conj [Y X])
              (recur (inc k)))))

        ;; Move backward (k < 0)
        (loop [k -1]
          (let [Y (+ y1 (* k dyu))
                X (+ x1 (* k dxu))]
            (when (in-bounds? grid Y X)
              (swap! antinodes conj [Y X])
              (recur (dec k)))))))

    ;; Place '#' at all antinodes
    (reduce (fn [g [Y X]] (set-char g Y X \#)) grid @antinodes)))

(defn -main []
  (let [grid (read-grid "src/advent_2024/08/08.txt")
        occ (occurrences-map grid)
        chars (filter #(>= (count (occ %)) 2) (keys occ))]

    ;; PART 1
    (let [final-grid-old (reduce (fn [g c] (place-antinodes g (occ c))) grid chars)
          antinode-count-old (count (filter #(= \# %) (apply concat final-grid-old)))]
      (println "=== Part 1 ===")
      (doseq [row final-grid-old]
        (println (apply str row)))
      (println "Number of antinodes (Part 1):" antinode-count-old))

    (println)

    ;; PART 2
    (let [final-grid-new (reduce (fn [g c] (place-antinodes-new g (occ c))) grid chars)
          antinode-count-new (count (filter #(= \# %) (apply concat final-grid-new)))]
      (println "=== Part 2 ===")
      (doseq [row final-grid-new]
        (println (apply str row)))
      (println "Number of antinodes (Part 2):" antinode-count-new))))