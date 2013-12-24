(ns oro.utils
  (:require [clojurewerkz.scrypt.core :as scrypt]))

(defmacro r<-
  "Useful for listing ring middleware/wrappers in logical order. 

  e.g. in lieu of (-> myroutes do-something-with-user add-user) you
       would write (r<- myroutes add-user do-something-with-user)"
  ([x] x) 
  ([x & forms]
     `(-> ~x ~@(reverse forms))))

(defmacro r<<-
  "Like r<- but with ->>."
  ([x] x) 
  ([x & forms]
     `(->> ~x ~@(reverse forms))))

(defn encrypt
  [pw]
  (scrypt/encrypt pw (Math/pow 2 16) 8 1))

(defn verify
  [plain-pw encrypted-pw]
  (scrypt/verify plain-pw encrypted-pw))
