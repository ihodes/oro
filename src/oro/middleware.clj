(ns oro.middleware
  (require [plumbing.core :refer :all]
           [cheshire.core :as smile]))


(defn wrap-json-response
  [handler]  
  (fn [request]
    (let [response (handler request)
          body (:body response)]
      (if (instance? clojure.lang.PersistentArrayMap body)
        (update-in response [:body] #(smile/generate-string % {:pretty true}))
        response))))

(defn wrap-string-body
  [handler]
  (fn [request]
    (try (handler (update-in request [:body] slurp))
         (catch Exception e (handler request)))))

(defn wrap-json-request
  [handler]
  (fn [request] 
    (try (let [body (:body request)
               json-map (keywordize-map (smile/parse-string body))]
           (handler (merge-with merge request {:json json-map})))
         (catch Exception e (handler request)))))
