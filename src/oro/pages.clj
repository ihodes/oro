(ns oro.pages
  (:require  [plumbing.core :refer :all]
             [hiccup.core :refer [html]]))



(defnk html-page
  [title body]
  (html [:html
         [:head [:title title]]
         [:body body]]))

(defn landing-page [req]
  (html-page {:title "Home Page"
              :body [:p "This is the home page"]}))
