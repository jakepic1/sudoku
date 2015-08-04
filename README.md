# sudoku

Implementation of a sudoku solver in Clojure.

Currently, the only implementation is a naive recursive backtracking solution. This is suboptimal,
and doesn't really make use of any intelligent sudoku strategies the way a human would. As I have time
to improve the algorithm and write new implementations, I'll add them here.

You can see how slow it is for some inputs by running `lein test :performance`

## Usage

FIXME
