(ns comma-err.core)

(defmacro comma-err
  "Takes a function call which would throw, and makes it return [result err].
  The value of err is the thrown exception.

  Example:
  (comma-err (identity 6)                                ;=> [6 nil]
  (comma-err (throw (Exception. \"Something's wrong\"))) ;=> [nil #<Exception java.lang.Exception: Something's wrong>]"
  [f]
  `(try
     (let [result# ~f]
       (list result# nil))
     (catch Exception e#
       (list nil e#))))

(defn comma-err-fn
  "Takes a function which would throw, and makes it return [result err].
  The value of err is the thrown exception.

  Example:
  ((comma-err-fn identity) 6)                               ;=> [6 nil]
  ((comma-err-fn throw) (Exception. \"Something's wrong\")) ;=> [nil #<Exception java.lang.Exception: Something's wrong>]"
  [f]
  (fn [ & args ]
    (comma-err
      (apply f args))))

(defmacro must
  "Takes a function call which would return [result err] and makes it throw an
  exception if err is not nil. The message of the exception is err.

  Example:
  (must (identity [5 nil]))                     ;=> 5
  (must (identity [nil \"Something's wrong\"])) ;=> throws an exception"
  [f]
  `(let [[result# err#] ~f]
     (if (nil? err#)
       result#
       (throw (Exception. err#)))))

(defn must-fn
  "Takes a function which would return [result err] and makes it throw an
  exception if err is not nil. The message of the exception is err.

  Example:
  ((must-fn identity) [5 nil])                     ;=> 5
  ((must-fn identity) [nil \"Something's wrong\"]) ;=> throws an exception"
  [f]
  (fn [ & args ]
    (comma-err
      (apply f args))))

(defmacro when-err
  "Catch errors in the [result err] style, and handle them somehow. The error
  is available as the 'err' var. If error is nil, returns the 'result' value.
  Otherwise, returns the result of the block.

  Example:
  (when-err (identity [5 nil])
    (comment \"handle an error here\"))
  ;=> returns 5

  (when-err (identity [nil \"error\"])
    (println \"something bad happened\")
    (println (str \"Error: \" err))
    \"alternative result\")
  ;=> returns \"alternative result\"
  "

  [f & handler]
  `(let [[result# ~'err] ~f]
     (if (nil? ~'err)
       result#
       (do ~@handler))))

(defmacro case-err
  "Catch errors in the [result err] style, and use a case statement to handle
  them. The error is available in the 'err' var. This is equivalent to a
  when-err with a case inside of it.

  Examples:
  (case-err (identity [5 nil])
    \"error1\" 6)
  ;=> returns 5

  (case-err (identity [nil (Exception. \"unhandled error 1\")])
    FileNotFoundException (slurp \"some/other/file.log\")
    TimeoutException      :timeout
    :unhandled_error)
  ;=> returns :unhandled_error"

  [ f & clauses ]
  `(when-err ~f
    (case ~'err
      ~@clauses)))

(defmacro condp-err
  "Catch errors in the [result err] style, and use a condp statement to handle
  them. The error is available in the 'err' var. This is equivalent to a
  when-err with a condp inside of it.

  Examples:
  (condp-err = (identity [5 nil])
    \"error1\" 6)
  ;=> returns 5

  (condp-err instance? (identity [nil (TimeoutException.)])
    FileNotFoundException (slurp \"some/other/file.log\")
    TimeoutException      :timeout
    :unhandled_error)
  ;=> returns :timeout"

  [ pred f & clauses ]
  `(when-err ~f
    (condp ~pred ~'err
      ~@clauses)))

(defn has-err?
  "Check if a comma-err style return value has an error present.

  Exmples:
  (has-err? (identity [5 nil]))                     ;=> false
  (has-err? (identity [nil \"Something's wrong\"])) ;=> true"

  [[_ err]]
  (not (nil? err)))
