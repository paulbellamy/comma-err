(ns comma-err.benchmarks.core
  (:use perforate.core
        comma-err.core))

(defgoal comma-err-vs-exception-bench "Comparing a basic comma-err vs a try-catch block.")

(defcase comma-err-vs-exception-bench :comma-err
  []
  (when-err (identity '(nil "an error"))
    err))

(defcase comma-err-vs-exception-bench :try-catch
  []
  (try
    (throw (Exception. "an error"))
    (catch Exception err
      (.getMessage err))))

(defgoal comma-err-wrapping-exception-bench "Wrapping an exception-throw with comma-err vs try-catch")

(defn- comma-err-n-calls
  [n]
  (if (pos? n)
    (comma-err-n-calls (dec n))
    (when-err (comma-err (/ 1 0))
      '(nil (.getMessage ^Exception err)))))

(defn- try-catch-n-calls
  [n]
  (if (pos? n)
    (try-catch-n-calls (dec n))
    (/ 1 0)))

(defcase comma-err-wrapping-exception-bench :comma-err
  []
  (comma-err-n-calls 10))

(defcase comma-err-wrapping-exception-bench :try-catch
  []
  (try
    (try-catch-n-calls 10)
    (catch Exception err
      (.getMessage ^Exception err))))
