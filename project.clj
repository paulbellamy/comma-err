(defproject comma-err "0.1.0-SNAPSHOT"

  :description "An experiment in Go-style error handling in Clojure."

  :url "http://github.com/paulbellamy/clj-comma-err"

  :license {:name "MIT License"
            :distribution :repo
            :url "https://raw.github.com/paulbellamy/comma-err/master/LICENSE"}

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.5.1"]
                                  [perforate "0.3.3"]]
                   :source-paths ["dev"]
                   :plugins [[perforate "0.3.3"]
                             [codox "0.6.4"]]}

             :1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}

             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}

             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}

             :1.6 {:dependencies [[org.clojure/clojure "1.6.0-master-SNAPSHOT"]]}}

  :aliases {"all" ["with-profile" "dev:1.3,dev:1.4,dev:1.5,dev:1.6,dev"]}

  :codox {:exclude [comma-err.benchmarks.core]
          :src-dir-uri "http://github.com/paulbellamy/comma-err/blob/0.1.0-SNAPSHOT"
          :src-linenum-anchor-prefix "L"}

  :perforate {:environments [{:name :dev
                              :profiles [:dev]
                              :namespaces [comma-err.benchmarks.core]}]})
