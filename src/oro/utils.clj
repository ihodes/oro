(ns oro.utils)

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
