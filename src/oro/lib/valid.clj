(ns oro.lib.valid
  "Validations for use all o'er the place."
  (:require [plumbing.core :refer :all]
            [oro.lib.utils :refer :all]
            [clojure.core.match :refer [match]]))



(defn rmap [& args] (remove nil? (apply map args)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Validator Core
(declare validate validate-entry)

(defn validate-with-validator
  "Returns an error message, or nil, for a given value and its validator."
  [value validator]
  (match [validator]
    [(s :guard set?)]        (if-not (contains? s value)
                               (str "Must be one of " s))
    [(validator :guard fn?)] (let [err (try (validator value)
                                         (catch Exception e (.getMessage e)))]
                               (cond (= true err)  nil        ; no error
                                     (= false err) "Invalid." ; default error
                                     :else         err))))    ; custom error

(defn validate-with-validators
  [required? key json validators]
  (if (contains? json key)
    (let [val  (get json key)
          errs (rmap #(validate-with-validator val %) validators)]
      (if-not (empty? errs) [key (set errs)]))
    (if required? [key "Is required."])))

(defn validate-with-subspec
  [required? key json subspec]
  (cond (not (contains? json key))  (if required? [key "Is required."])
        (not (map? (get json key))) [key "Must be an object."]
        :else (let [errs (rmap (partial validate-entry (get json key)) subspec)]
                (if-not (empty? errs)
                  [key errs]))))

(defn validate-entry
  [json [key spec]]
  (match [spec]
    ;; Go deeper.
    [[true  (spec :guard map?)]] (validate-with-subspec true  key json spec)
    [[false (spec :guard map?)]] (validate-with-subspec false key json spec)
    [(spec :guard map?)]         (validate-with-subspec false key json spec)
    
    ;; Simple presence checks.
    [true]   (if-not (contains? json key) [key "Is required."])
    [false]  nil

    ;; Validators
    [[true & vs]]   (validate-with-validators true  key json vs)
    [[false & vs]]  (validate-with-validators false key json vs)
    [[& vs]]        (validate-with-validators false key json vs)
    [validator]     (validate-with-validators false key json [validator])))

(defn validate-nonexistence
  "Used to make sure no extraneous keys are within the spec."
  [spec [key val]]
  (if-not (contains? spec key) [key "Key is not permitted."]
          (let [spec (get spec key)
                vne' (partial validate-nonexistence spec)
                err  (match [spec]
                       [(m :guard map?)] (map vne' val)
                       [[(_ :guard boolean?) (m :guard map?)]] (map vne' val)
                       [_] '())
                err  (remove nil? err)]
            (if-not (empty? err)
              [key err]))))

(defn validate
  "Given a spec map and a JSON object, returns an empty map if there are no 
  errors, else returns a nested map with keys for each key-value error within,
  with value a string reflecting the error, or a set of errors."
  [spec jsono]
  (to-map
   (concat (rmap (partial validate-entry jsono) spec)
           (rmap (partial validate-nonexistence spec) jsono))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Validation Functions
(defn email?
  [c]
  (if (re-find #".+@.+\..+" c) true
      "Must be a valid email."))

(defn password?
  [c]
  (if (> (count c) 8) true
      "Must be 8 characters or longer."))
