# comma-err
[![Build Status](https://travis-ci.org/paulbellamy/comma-err.png)](https://travis-ci.org/paulbellamy/comma-err)

An experiment in Go-style explicit, local error handling in Clojure.

## Why

Exceptions are often overused for things which are not truly exceptional. For example, in an HTTP server returning a 404 to the user is not really an exceptional circumstance. Developers should be encouraged to explicitly handle errors, instead of just letting them fail-by-default. Furthermore, exception handling which is far from the source of an error can make debugging difficult. Instead, it is better to handle errors as close to the source as is possible. The "comma-err style" encourages developers to think explicitly about error handling, and to handle errors close to the source. In addition it can sometimes be much faster than using exceptions because it does not need to include a stacktrace.

A general rule of thumb on when to use comma-err is: if you don't care about the stacktrace of an exception it should probably not be an exception.

## Usage

comma-err style functions are ones which have a return like ```[result err]```. Any non-nil value of err is considered an error.

There are helper functions to convert comma-err style code to/from exception-style.

To convert an exception-style function to comma-err style we can do either:

```Clojure
(comma-err (slurp "some-file.txt")) ;=> [nil #<FileNotFoundException...>]

(def safe-slurp (comma-err-fn slurp))
(safe-slurp "some-file.txt") ;=> [nil #<FileNotFoundException...>]
```

To convert a comma-err style error into an exception we can do either:

```Clojure
(must (safe-slurp "some-file.txt")) ;=> Throws a FileNotFoundException

(def dangerous-slurp (must-fn safe-slurp))
(dangerous-slurp "some-file.txt") ;=> Throws a FileNotFoundException
```

We can catch and handle comma-err style errors with several included helpers:

```Clojure
(when-err (identity [nil "error"])
  (println "something bad happened")
  (println (str "Error: " err))
  "alternative result")
;=> returns "alternative result"

(case-err (identity [nil (Exception. "unhandled error 1")])
  FileNotFoundException (slurp "some/other/file.log")
  TimeoutException      :timeout
  :unhandled_error)
;=> returns :unhandled_error

(condp-err instance? (identity [nil (TimeoutException.)])
  FileNotFoundException (slurp "some/other/file.log")
  TimeoutException      :timeout
  :unhandled_error)
;=> returns :timeout"

(has-err? (identity [nil "Something's wrong"]))
;=> returns true"
```

Please see [the docs](http://paulbellamy.github.com/comma-err) for more detailed documentation.

## Examples

See [examples](http://github.com/paulbellamy/comma-err/tree/master/examples) for more detailed examples.

## License

Copyright © 2013 Paul Bellamy

Distributed under the MIT License.
