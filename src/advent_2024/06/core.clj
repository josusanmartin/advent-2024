(ns advent-2024.06.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

;; Toggle visualization
(def ^:dynamic *visualize* false)

;; Directions and movement setup
(def directions [:up :right :down :left])
(def direction-deltas {:up [0 -1], :right [1 0], :down [0 1], :left [-1 0]})
(def direction-char {:up \^ :right \> :down \v :left \<})

(defn turn-right [dir]
  (directions (mod (inc (.indexOf directions dir)) 4)))

(defn parse-map [lines]
  (let [grid (mapv vec lines)
        height (count grid)
        width (count (first grid))
        guard-pos (first (for [y (range height)
                               x (range width)
                               :let [ch (get-in grid [y x])]
                               :when (= ch \^)]
                           [x y]))]
    {:grid grid
     :start-x (first guard-pos)
     :start-y (second guard-pos)
     :start-dir :up
     :width width
     :height height}))

(defn in-bounds? [x y width height]
  (and (>= x 0) (< x width)
       (>= y 0) (< y height)))

(defn cell [grid x y]
  (get-in grid [y x]))

(defn print-viewport
  "Print a portion of the grid around (x,y) if visualization is enabled."
  [grid x y dir visited viewport-width viewport-height]
  (let [height (count grid)
        width  (count (first grid))
        half-w (quot viewport-width 2)
        half-h (quot viewport-height 2)
        min-x (max 0 (- x half-w))
        max-x (min (dec width) (+ x half-w))
        min-y (max 0 (- y half-h))
        max-y (min (dec height) (+ y half-h))]

    (doseq [row-idx (range min-y (inc max-y))]
      (doseq [col-idx (range min-x (inc max-x))]
        (cond
          (and (= col-idx x) (= row-idx y))
          (print (direction-char dir))

          (= (cell grid col-idx row-idx) \#)
          (print "#")

          (visited [col-idx row-idx])
          (print "*")

          :else
          (print ".")))
      (println))
    (println "---------------------------")))

(defn simulate
  "Simulates until the guard leaves, counting steps and unique visited cells.
   Optionally prints viewport if *visualize* is true.
   Returns {:steps <int> :visited-count <int>}."
  [{:keys [grid start-x start-y start-dir width height]}]
  (let [viewport-width  50  ; adjust as needed
        viewport-height 15]
    (loop [x start-x
           y start-y
           dir start-dir
           steps 0
           visited #{[x y]}]
      (when *visualize*
        (print-viewport grid x y dir visited viewport-width viewport-height)
        (Thread/sleep 5)) ;; pause for visualization

      (let [[dx dy] (direction-deltas dir)
            nx (+ x dx)
            ny (+ y dy)]
        (cond
          (not (in-bounds? nx ny width height))
          {:steps steps :visited-count (count visited)}

          (= (cell grid nx ny) \#)
          ;; turn right and try again
          (recur x y (turn-right dir) steps visited)

          :else
          ;; move forward
          (recur nx ny dir (inc steps) (conj visited [nx ny])))))))

(defn set-cell [grid x y c]
  ;; Returns a new grid with cell (x,y) replaced by c
  (assoc grid y (assoc (grid y) x c)))

(defn simulate-with-loop-detection
  "Simulate until guard leaves or we detect a loop.
   Returns {:status :loop or :left, :steps <int if left>}"
  [grid start-x start-y start-dir width height]
  (loop [x start-x
         y start-y
         dir start-dir
         steps 0
         visited-states #{[x y dir]}]
    (let [[dx dy] (direction-deltas dir)
          nx (+ x dx)
          ny (+ y dy)]
      (cond
        ;; Out of bounds => guard leaves
        (not (in-bounds? nx ny width height))
        {:status :left :steps steps}

        ;; Obstacle => turn right, try again without moving forward
        (= (cell grid nx ny) \#)
        (let [new-dir (turn-right dir)]
          (if (visited-states [x y new-dir])
            {:status :loop}
            (recur x y new-dir steps (conj visited-states [x y new-dir]))))

        :else
        ;; Move forward
        (let [new-state [nx ny dir]]
          (if (visited-states new-state)
            {:status :loop}
            (recur nx ny dir (inc steps) (conj visited-states new-state))))))))

(defn count-loop-configurations
  "Counts how many ways to add a single obstacle (#) to empty cells causes a loop."
  [{:keys [grid start-x start-y start-dir width height]}]
  (let [candidates (for [y (range height)
                         x (range width)
                         :let [ch (cell grid x y)]
                         :when (and (= ch \.)
                                    (not (= [x y] [start-x start-y])))]
                     [x y])]
    (reduce (fn [acc [cx cy]]
              (let [new-grid (set-cell grid cx cy \#)
                    result (simulate-with-loop-detection new-grid start-x start-y start-dir width height)]
                (if (= :loop (:status result))
                  (inc acc)
                  acc)))
            0
            candidates)))

(defn -main []
  (let [lines (-> "src/advent_2024/06/06.txt"
                  slurp
                  str/split-lines)
        parsed (parse-map lines)]
    ;; First, run the normal simulation (with optional visualization)
    (let [{:keys [steps visited-count]} (simulate parsed)]
      (println "Steps before leaving:" steps)
      (println "Unique spaces visited:" visited-count))

    ;; Next, count how many single-obstacle placements cause infinite loops
    (let [loop-count (count-loop-configurations parsed)]
      (println "Number of ways to add a single obstacle to cause a loop:" loop-count))))