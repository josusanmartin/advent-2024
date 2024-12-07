(ns advent-2024.05.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn parse-rules [lines]
  (map (fn [line]
         (let [[a b] (str/split line #"\|")]
           [(Integer/parseInt (str/trim a)) (Integer/parseInt (str/trim b))]))
       lines))

(defn parse-array-line [line]
  (->> (str/split (str/trim line) #",")
       (map str/trim)
       (remove str/blank?)
       (mapv #(Integer/parseInt %))))

(defn respects-rules? [arr rules]
  (let [positions (zipmap arr (range (count arr)))]
    (every? (fn [[A B]]
              (let [posA (positions A)
                    posB (positions B)]
                (if (and posA posB)
                  (< posA posB)
                  true)))
            rules)))

(defn middle-element [arr]
  (arr (quot (count arr) 2)))

;; Topological sort to reorder NOT OK arrays according to the rules
(defn topo-sort [nodes edges] 
  ;; nodes: a set or seq of unique elements
  ;; edges: a sequence of [A B] meaning A must come before B
  (let [incoming-count (reduce (fn [m [a b]]
                                 (update m b (fnil inc 0)))
                               (zipmap nodes (repeat 0)) edges)
        adjacency-list (reduce (fn [m [a b]]
                                 (update m a conj b))
                               (zipmap nodes (repeat [])) edges)
        ;; queue with all nodes that have no incoming edges
        initial (filter #(zero? (incoming-count %)) nodes)]
    (loop [result []
           queue (into clojure.lang.PersistentQueue/EMPTY initial)
           in-count incoming-count]
      (if (empty? queue)
        ;; If result doesn't contain all nodes, there's a cycle or unreachable node
        (if (= (count result) (count nodes))
          result
          (throw (ex-info "Cycle detected or incomplete topological sort" {})))
        (let [n (peek queue)
              q (pop queue)
              ;; For each neighbor, decrement incoming edge count
              [in-count' new-nodes]
              (reduce (fn [[ic ns] nbr]
                        (let [new-count (dec (ic nbr))]
                          [(assoc ic nbr new-count)
                           (if (zero? new-count) (conj ns nbr) ns)]))
                      [in-count []]
                      (adjacency-list n))]
          (recur (conj result n) (reduce conj q new-nodes) in-count'))))))

(defn reorder-array [arr rules]
  ;; Filter rules to only those involving elements in arr
  (let [arr-set (set arr)
        relevant-rules (filter (fn [[A B]]
                                 (and (arr-set A) (arr-set B)))
                               rules)
        unique-elems (vec arr-set)] ; All unique elements from arr
    (topo-sort unique-elems relevant-rules)))

(defn -main []
  (let [lines (-> "src/advent_2024/05/05.txt"
                  slurp
                  str/split-lines)
        [rule-lines raw-array-lines] (split-with #(re-find #"\|" %) lines)
        array-lines (remove str/blank? raw-array-lines)
        rules (parse-rules rule-lines)
        arrays (map parse-array-line array-lines)]

    ;; First part: Check each array, print OK/NOT OK, sum middles of OK arrays
    (println "=== First Part (Original Arrays) ===")
    (let [results (for [arr arrays]
                    (let [ok? (respects-rules? arr rules)]
                      #_(println (str (str/join "," arr) " -> " (if ok? "OK" "NOT OK")))
                      {:arr arr :ok? ok? :middle (when ok? (middle-element arr))}))]
      (let [sum-ok (reduce + 0 (mapcat #(when (:ok? %) [(:middle %)]) results))]
        (println "Sum of all middle elements from OK arrays:" sum-ok))

      ;; Second part: For NOT OK arrays, reorder and sum middles
      (println "\n=== Second Part (Reordered NOT OK Arrays) ===")
      (let [not-ok-results (for [r results :when (not (:ok? r))]
                             (let [reordered (reorder-array (:arr r) rules)]
                               #_(println (str (str/join "," (:arr r))
                                             " reordered to "
                                             (str/join "," reordered)))
                               (middle-element reordered)))]
        (println "Sum of all middle elements from reordered NOT OK arrays:"
                 (reduce + 0 not-ok-results))))))