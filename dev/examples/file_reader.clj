(ns examples.file-reader
  (:use comma-err.core))

(defn read-first-file
  "Read through a list of files returning the
  contents of the first one which exists and
  is readable. Returns nil if none succeed."
  [ & files]
  (let [safe-slurp (comma-err-fn slurp)]
    (->> (map safe-slurp files)
         (remove has-err?)
         ffirst)))
