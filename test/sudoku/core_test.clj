(ns sudoku.core-test
  (:require
    [clojure.pprint]
    [clojure.test :refer :all]
    [sudoku.core :refer :all]))

(deftest test-take-slice
  (let [matrix [[0 1 2]
                [3 4 5]
                [6 7 8]]]
    (are [idx value] (= (take-slice matrix idx) value)
      [[0 1] [0 1]] [[0]]
      [[0 3] [0 1]] [[0] [3] [6]]
      [[0 1] [0 3]] [[0 1 2]])))

(deftest test-get-shape
  (are [matrix _ shape] (= (get-shape matrix) shape)
    [[1 2] [3 4] [5 6]] -> [3 2]
    [[0] [0]] -> [2 1]
    [[0]] -> [1 1]
    [] -> [0 0]))

(deftest test-valid-section?
  (are [section] (valid-section? section)
    [[1 2 3]
     [4 5 6]
     [7 8 9]]
    [[0 0 0]
     [0 0 0]
     [0 0 0]]
    [[1 0 3 4 5 6 7 8 9]]
    [[1] [0] [3] [4] [5] [6] [7] [8] [9]])
  (are [section] (not (valid-section? section))
    [[1 2 3]
     [3 5 6]
     [7 8 9]]
    [[1 0 3 4 5 6 7 8 1]]
    ;; wrong shape
    [[1] [0] [3] [4] [5] [6] [7] [8]]))

(def ^:private sample-puzzle
  [[0 0 3 0 2 0 6 0 0]
   [9 0 0 3 0 5 0 0 1]
   [0 0 1 8 0 6 4 0 0]
   [0 0 8 1 0 2 9 0 0]
   [7 0 0 0 0 0 0 0 8]
   [0 0 6 7 0 8 2 0 0]
   [0 0 2 6 0 9 5 0 0]
   [8 0 0 2 0 3 0 0 9]
   [0 0 5 0 1 0 3 0 0]])

(deftest test-get-row
  (are [idx row] (= (get-row sample-puzzle idx) row)
    [0 0] [[0 0 3 0 2 0 6 0 0]]
    [0 5] [[0 0 3 0 2 0 6 0 0]]
    [3 7] [[0 0 8 1 0 2 9 0 0]]
    [8 1] [[0 0 5 0 1 0 3 0 0]]
    [9 0] []))

(deftest test-get-col
  (are [idx col] (= (apply concat (get-col sample-puzzle idx)) col)
    [0 0] [0 9 0 0 7 0 0 8 0]
    [8 0] [0 9 0 0 7 0 0 8 0]
    [5 3] [0 3 8 1 0 7 6 2 0]))

(deftest test-get-square-section
  (are [idx square] (= (get-square-section sample-puzzle idx) square)
    [0 0] [[0 0 3]
           [9 0 0]
           [0 0 1]]
    [2 2] [[0 0 3]
           [9 0 0]
           [0 0 1]]
    [3 2] [[0 0 8]
           [7 0 0]
           [0 0 6]]
    [8 8] [[5 0 0]
           [0 0 9]
           [3 0 0]]
    [9 9] []))

(deftest test-get-valid-numbers
  (are [idx xs] (= (get-valid-numbers sample-puzzle idx) xs)
    [0 0] [4 5]
    [3 0] [3 4 5]
    [8 8] [2 4 6 7]))

(deftest test-sudoku
  (is
    (=
      (sudoku sample-puzzle)
      [[4 8 3 9 2 1 6 5 7]
       [9 6 7 3 4 5 8 2 1]
       [2 5 1 8 7 6 4 9 3]
       [5 4 8 1 3 2 9 7 6]
       [7 2 9 5 6 4 1 3 8]
       [1 3 6 7 9 8 2 4 5]
       [3 7 2 6 8 9 5 1 4]
       [8 1 4 2 5 3 7 6 9]
       [6 9 5 4 1 7 3 8 2]])))

(defn- string->int-vec
  [s]
  (mapv #(Character/digit % 10) s))

(defn- read-puzzles-file
  "Input file is of the form:
    Grid 01
    003020600
    900305001
    001806400
    008102900
    700000008
    006708200
    002609500
    800203009
    005010300
    Grid 02
    ..."
  [file-name]
  (let [contents (slurp file-name)
        lines (clojure.string/split-lines contents)
        grouped-lines (partition 10 lines)]
    (map
      (fn [[_ & lines]]
        (mapv string->int-vec lines))
      grouped-lines)))

;; Input file borrowed from Project Euler problem 92
(def ^:private puzzles-file
  "test/resources/test.txt")

(deftest ^:performance test-sudoku-performance
  (doseq [puzzle (read-puzzles-file puzzles-file)]
    (println "*******************************\n")
    (clojure.pprint/pprint puzzle)
    (let [solution (time (sudoku puzzle))]
      solution)
    (println "*******************************\n")))
