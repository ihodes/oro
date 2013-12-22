(defproject oro "0.1.0-ALPHA"
  :description "Move money."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [org.clojure/core.logic "0.8.5"]
                 [clojurewerkz/scrypt "1.0.0"]
                 [org.clojure/data.codec "0.1.0"]
                 
                 [prismatic/schema "0.1.9"]
                 [prismatic/plumbing "0.1.1"]

                 [ring "1.2.1"]
                 [http-kit "2.1.13"]
                 [compojure "1.1.6"]
                 [liberator "0.10.0"]

                 [environ "0.4.0"]

                 [io.isaachodes/reticulum "0.0.1-SNAPSHOT"]

                 [hiccup "1.0.4"]
                 [cheshire "5.2.0"]
                 
                 [org.clojure/java.jdbc "0.2.3"]
                 [postgresql "9.1-901.jdbc4"]
                 [korma "0.3.0-RC6"]])
