(ns clj-comma-err.core-test
  (:use clojure.test
        clj-comma-err.core)
  (:import [java.util.concurrent TimeoutException]
           [java.io FileNotFoundException]))

(deftest comma-err-test
  (testing "it passes through the result on success"
    (is (= '(5 nil) (comma-err (identity 5)))))

  (testing "it catches exceptions on error"
    (let [[result err] (comma-err (throw (Exception. "something bad")))]
      (is (nil? result))
      (is (= Exception (class err)))
      (is (= "something bad" (.getMessage err))))))

(deftest must-test
  (testing "it passes through the result on success"
    (is (= 5 (must (identity '(5 nil))))))

  (testing "it throws when an error is returned"
    (is (thrown-with-msg? Exception #"something bad"
          (must (identity '(nil "something bad")))))))

(deftest when-err-test
  (testing "it passes through the result on success"
    (is (= 5 (when-err (identity '(5 nil))
               6))))

  (testing "it calls the block (with error as err) and returns the last when an
           error happens"
    (let [calledArgs (atom [])]
      (is (= 6 (when-err (identity '(nil "something bad"))
                 (swap! calledArgs conj err)
                 6)))
      (is (= ["something bad"] @calledArgs)))))

(deftest case-err-test
  (testing "it passes through the result on success"
    (is (= 5 (case-err (identity '(5 nil))
               "something bad" 7))))

  (testing "it calls the matching block when an error occurs"
    (is (= 7 (case-err (identity '(nil "something bad"))
               "file not found" 4
               "something bad" 7))))

  (testing "it can return a default from the block")
    (is (= :unhandled (case-err (identity '(nil "something bad"))
               "file not found" 4
               :unhandled)))

  (testing "it can match different exceptions"
    (is (= :timeout (case-err (identity '(nil :b))
                      :a :file_not_found
                      :b :timeout
                      :unhandled)))))

(deftest condp-err-test
  (testing "it passes through the result on success"
    (is (= 5 (condp-err = (identity '(5 nil))
               "something bad" 7))))

  (testing "it calls the matching block when an error occurs"
    (is (= 7 (condp-err = (identity '(nil "something bad"))
               "file not found" 4
               "something bad" 7))))

  (testing "it can return a default from the block")
    (is (= :unhandled (condp-err = (identity '(nil "something bad"))
               "file not found" 4
               :unhandled)))

  (testing "it can match different exceptions"
    (is (= :timeout (condp-err instance? (identity '(nil (TimeoutException.)))
                      FileNotFoundException :file_not_found
                      TimeoutException :timeout
                      :unhandled)))))
