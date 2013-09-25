(ns examples.http
  "An example of one use for comma-err in an http application. Specifically we
  are looking at how we handle page not found, and unauthorized errors. These
  errors would typically be handled with exceptions, but by using comma-err we
  can ensure a higher degree of robustness.

  Obviously the code here has no degree of security and should not be used in
  production."
  (:use comma-err.core))

(def not-found {:status 404, :body "Not Found"})

(defn- forbidden
  [{path :path}]
  {:status 403, :body (str "You do not have sufficient privileges to access " path)})

(defn- server-error
  "Render a server error. Just dump the exception/error to a string for now."
  [req err]
  {:status 500, :body (str err)})

(defn static-file-handler
  "Our http handler for serving static files. We keep the error handling quite
  local, instead of relying on throwing/catching exceptions. The advantage is
  that we know that this function can never throw an exception, so our
  flow-of-control is always assured.

  This should not be used in production as it will just serve up any file on
  the entire filesystem.

  TODO: Differentiate the type of error in a better way. What is the Java
  class? They are both FileNotFoundExceptions"
  [{path :path :as req}]
  (condp-err #(re-find %1 (.getMessage %2)) (comma-err {:status 200 :body (slurp path)})
    #"No such file or directory"  not-found
    #"Permission denied"          (forbidden req)
    ;; else
    (server-error req err)
    ))
