(defproject comma-err "0.1.0-SNAPSHOT"

  :description "An experiment in Go-style error handling in Clojure."

  :url "http://github.com/paulbellamy/clj-comma-err"

  :license {:name "MIT License"
            :distribution :repo}

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.5.1"]
                                  [perforate "0.3.3"]]
                   :plugins [[perforate "0.3.3"]]}

             :1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}

             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}

             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}

             :1.6 {:dependencies [[org.clojure/clojure "1.6.0-master-SNAPSHOT"]]}}

  :aliases {"all" ["with-profile" "dev:1.3,dev:1.4,dev:1.5,dev:1.6,dev"]}

  :perforate {:environments [{:name :dev
                              :profiles [:dev]
                              :namespaces [comma-err.benchmarks.core]}]})
