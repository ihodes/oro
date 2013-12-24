(ns oro.valid
  "Validations for use all o'er")


(defn email?
  [c]
  true)

(defn password?
  [c]
  (> (count c) 3)) ;; TK TODO should move to config
