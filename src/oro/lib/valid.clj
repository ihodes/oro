(ns oro.lib.valid
  "Validations for use all o'er"
  (:require [plumbing.core :refer :all]
            [oro.lib.utils :refer :all]
            [clojure.string :refer (join)]
            [clojure.core.match :refer [match emit-pattern to-source groupable?]]
            [clojure.core.match.regex :refer :all]
            [cheshire.core :as smile]))



(defn rmap [& args] (remove nil? (apply map args)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Validator Core
(declare validate)

(defn validate-value
  "Returns an error message, or nil, for a given value and its validator."
  [value validator]
  (match [validator]
    [(s :guard set?)]          (if-not (contains? s value)
                                 (str "Must be one of " s))
    [(validator :guard fn?)]   (let [err (validator value)]
                                 (cond (= true err)  nil        ; no error
                                       (= false err) "Invalid." ; default error
                                       :else         err))))    ; custom error
(defn validate-entry*
  [jsono [key spec]]
  (match [spec]
    ;; Go deeper.
    [(m :guard map?)]         (cond (not (contains? jsono key))  nil ; not required by default
                                    (not (map? (get jsono key))) [key "Must be an object."]
                                    :else (let [errs (rmap (partial validate-entry* (get jsono key)) m)]
                                            (if-not (empty? errs)
                                              [key errs])))
    [[true  (m :guard map?)]] (cond (not (contains? jsono key))  [key "Is required."]
                                    (not (map? (get jsono key))) [key "Must be an object."]
                                    :else (let [errs (rmap (partial validate-entry* (get jsono key)) m)]
                                            (if-not (empty? errs)
                                              [key errs])))
    [[false (m :guard map?)]] (cond (not (contains? jsono key))  nil ; not required
                                    (not (map? (get jsono key))) [key "Must be an object."]
                                    :else (let [errs (rmap (partial validate-entry* (get jsono key)) m)]
                                            (if-not (empty? errs)
                                              [key errs])))
    
    ;; Validator arrays.
    [[true & validators]]     (if (contains? jsono key)
                                (let [val  (get jsono key)
                                      errs (rmap #(validate-value val %) validators)]
                                  (if-not (empty? errs) [key (set errs)]))
                                [key "Is required."])
    [[false & validators]]     (if (contains? jsono key)
                                 (let [val  (get jsono key)
                                       errs (rmap #(validate-value val %) validators)]
                                   (if-not (empty? errs) [key (set errs)])))
    [[& validators]]           (if (contains? jsono key)
                                 (let [val  (get jsono key)
                                       errs (rmap #(validate-value val %) validators)]
                                   (if-not (empty? errs) [key (set errs)])))
    
    ;; Simple presence checks.
    [true]                     (if-not (contains? jsono key)
                                 [key "Is required."])
    [false]                    nil
    
    ;; Validate it.
    [spec]                     (if-let [value (get jsono key)]
                                 (if-let [err (try (validate-value value spec)
                                                    (catch Exception e (.getMessage e)))]
                                   [key err]))
    [_]                        nil))

(defn validate-nonexistence*
  "Used to make sure no extraneous keys are within the spec."
  [spec [key val]]
  (if-not (contains? spec key) [key "Key is not permitted."]
          (let [spec (get spec key)
                vne' (partial validate-nonexistence* spec)
                err  (match [spec val]
                       [(m :guard map?) (val :guard map?)]                        (map vne' val)
                       [[(_ :guard boolean?) (m :guard map?)] (val :guard map?)]  (map vne' val)
                       [_ _] '())
                err  (remove nil? err)]
            (if-not (empty? err)
              [key err]))))

(defn validate
  "Given a spec map and a JSON object, returns an empty map if there are no errors, else
  returns a nested map with keys for each key-value error within, with value a string
  reflecting the error, or a set of errors."
  [spec jsono]
  (to-map
   (concat (rmap (partial validate-entry* jsono) spec)
           (rmap (partial validate-nonexistence* spec) jsono))))


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

