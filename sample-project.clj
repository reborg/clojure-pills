(defproject clojure-pills "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-nodisassemble "0.1.3"]
            [venantius/ultra  "0.5.1"]]
  :jvm-opts ["-Xmx2G"]
  :java-source-paths ["java"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [criterium "0.4.3"]
                 [enlive "1.1.6"]])
