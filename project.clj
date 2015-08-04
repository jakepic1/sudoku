(defproject sudoku "0.1.0-SNAPSHOT"
  :description "Clojure sudoku solver."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :test-selectors {:default (complement :performance)
                   :performance :performance})
