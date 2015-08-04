(ns sudoku.core)

(defn take-slice
  "Get a slice of a 2-d sequence, as a 2-d sequence.

  The matrix is assumed to be row-major.

  Start is inclusive, stop is exclusive."
  [matrix [[row-start row-stop] [col-start col-stop]]]
  (let [rows (->> matrix (take row-stop) (drop row-start))]
    (mapv
      #(->> % (take col-stop) (drop col-start) vec)
      rows)))

(defn get-shape
  "Get [num-rows, num-cols] for a sequence of sequences.

  Matrix is assumed to be row-major. So this returns
  [(count first-dimension) (count second-dimension)]"
  [matrix]
  [(count matrix) (-> matrix first count)])

(def blank-cell? zero?)

(defn valid-section?
  "Determine if the sudoku section is valid according to standard sudoku rules
  (meaning, has no duplicate numbers).

  Input must be 3x3 (square segment), 9x1 (column), or 1x9 (row)."
  [sudoku-section]
  (let [non-empty-squares (->> sudoku-section (apply concat) (remove blank-cell?))
        shape (get-shape sudoku-section)]
    (and
      (some #{shape} [[3 3] [9 1] [1 9]])
      (= (count non-empty-squares) (count (set non-empty-squares))))))

(defn get-row
  "Get the row for a given cell. Returns a 1x9 matrix."
  [puzzle [row-num col-num]]
  (let [[_ number-of-cols] (get-shape puzzle)]
   (take-slice puzzle [[row-num (inc row-num)] [0 number-of-cols]])))

(defn get-col
  "Get the column for a given cell. Returns a 9x1 matrix."
  [puzzle [row-num col-num]]
  (let [[number-of-rows _] (get-shape puzzle)]
    (take-slice puzzle [[0 number-of-rows] [col-num (inc col-num)]])))

(defn get-square-section
  "Get the 3x3 square section for a given cell."
  [puzzle [row-num col-num]]
  (let [get-start #(-> % (/ 3) int (* 3))
        row-start (get-start row-num)
        col-start (get-start col-num)]
    (take-slice puzzle [[row-start (+ 3 row-start)] [col-start (+ 3 col-start)]])))

(defn get-valid-numbers
  "Get a sequence of valid numbers that can be placed in the cell,
  according to standard Sudoku rules."
  [puzzle cell-idx]
  (filter
    #(let [puzzle (assoc-in puzzle cell-idx %)
           row (get-row puzzle cell-idx)
           col (get-col puzzle cell-idx)
           square (get-square-section puzzle cell-idx)]
       (every? valid-section? [row col square]))
    (range 1 10)))

(defn sudoku
  "Solve a sudoku puzzle using recursive backtracking.
  Puzzle is a 2d vector (vector of vectors)."
  [puzzle]
  (let [[num-rows num-cols] (get-shape puzzle)
        empty-cells (for [row (range num-rows)
                          col (range num-cols)
                          :when (blank-cell? (get-in puzzle [row col]))]
                      [row col])
        first-empty-cell (first empty-cells)]
    (if first-empty-cell
      (let [valid-numbers (get-valid-numbers puzzle first-empty-cell)]
        ;; Now do some recursive backtracking
        (some
          #(sudoku (assoc-in puzzle first-empty-cell %))
          valid-numbers))
      ;; Else, there are none left so the puzzle is solved
      puzzle)))
