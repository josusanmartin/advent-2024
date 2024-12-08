(ns advent-2024.07.core
  (:require [clojure.string :as str]))

(defn parse-line [line]
  (let [[target-part nums-part] (str/split line #":")
        target (Long/parseLong (str/trim target-part))
        trimmed-nums (str/trim nums-part)
        split-nums (str/split trimmed-nums #"\s")
        parsed-nums (map #(Long/parseLong %) split-nums)
        numbers (vec parsed-nums)]
    [target numbers]))

(defn can-form-target? [target nums]
  (if (= (count nums) 1)
    (= (first nums) target)
    (let [a (first nums)
          b (second nums)
          rest (subvec nums 2)]
      (or (can-form-target? target (vec (cons (+ a b) rest)))
          (can-form-target? target (vec (cons (* a b) rest)))))))

(defn can-form-target-con? [target nums]
  (cond
    (nil? nums) false
    (empty? nums) false
    (= (count nums) 1)
    (= (first nums) target)

    :else
    (let [a (first nums)
          b (second nums)
          rest (subvec nums 2)
          plus-result (vec (cons (+ a b) rest))
          mul-result (vec (cons (* a b) rest))
          ;; Concatenate a and b as strings, then parse
          concat-str (str a b)
          concat-val (try
                       (Long/parseLong concat-str)
                       (catch Exception e
                         ;; If parsing fails, consider this path invalid
                         nil))
          concat-result (when concat-val
                          (vec (cons concat-val rest)))]
      (or (can-form-target-con? target plus-result)
          (can-form-target-con? target mul-result)
          (and concat-result
               (can-form-target-con? target concat-result))))))

(defn -main []
  (let [lines (-> "src/advent_2024/07/07.txt"
                  slurp
                  str/split-lines)
        total (reduce (fn [acc line]
                        (let [[target nums] (parse-line line)]
                          (if (can-form-target? target nums)
                            (+ acc target)
                            acc)))
                      0 lines) 
        total-con (reduce (fn [acc line]
                        (let [[target nums] (parse-line line)]
                          (if (can-form-target-con? target nums)
                            (+ acc target)
                            acc)))
                      0 lines)]
    (println "Sum of targets that return true:" total)
    (println "Sum of targets that return true with concatenation:" total-con)))