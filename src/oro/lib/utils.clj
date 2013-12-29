(ns oro.lib.utils
  (:require [plumbing.core :refer :all]
            [clojurewerkz.scrypt.core :as scrypt]))



(defmacro r<-
  "Useful for listing ring middleware/wrappers in logical order. 

  e.g. in lieu of (-> myroutes do-something-with-user add-user) you
       would write (r<- myroutes add-user do-something-with-user)"
  ([x] x) 
  ([x & forms]
     `(-> ~x ~@(reverse forms))))

(defn call [f & args] (apply f args))
(defn flip [f] (fn [a b] (f b a)))

(defn encrypt
  [pw]
  (scrypt/encrypt pw (Math/pow 2 16) 8 1))

(defn verify
  [plain-pw encrypted-pw]
  (scrypt/verify plain-pw encrypted-pw))


(defn boolean?
  [v]
  (not (nil? (#{true false} v))))


(defn to-map
  [a]
  (reduce (fn [m [k v]]
            (if (or (seq? v) (vector? v))
              (assoc m k (to-map v))
              (assoc m k v)))
          {} a))
