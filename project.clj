(defproject cljgossip/testapp "0.1.0-SNAPSHOT"
  :description "A sample application using cljgossip and cljgossip-wsclient"
  :url "https://github.com/kbsant/cljgossip-testapp"
  :dependencies
  [[org.clojure/clojure "1.11.1"]
   [cljgossip "0.1.0-SNAPSHOT"]
   [cljgossip/wsclient "0.1.0-SNAPSHOT"]]
  :repl-options {:init-ns cljgossip.testapp})
