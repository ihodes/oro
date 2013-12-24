(ns oro.middleware
  (require [plumbing.core :refer :all]

           [cheshire.core :as smile]
           [ring.util.response :refer [charset header]]
           [clojure.data.codec.base64 :as b64]
           [clojure.string :refer [split]]
           [clojure.set :refer [rename-keys]]))


(defn str->base64-str [s]
  (String. (b64/encode (.getBytes s)) "UTF-8"))

(defn base64-str->str [s]
  (String. (b64/decode (.getBytes s)) "UTF-8"))

(defn auth
  [resp]
  (header resp "WWW-Authenticate" "Basic realm=\"oro\""))

(defn wrap-basic-auth
  [handler]
  (fn [request]
    (let [headers     (:headers request)
          auth-header (get headers "authorization")
          auth-text   (when auth-header (base64-str->str (nth (split auth-header #" ") 1)))
          auth-map    (when auth-text (into {} (map vector [:user :pass]
                                                    (split auth-text #":"))))]
      (handler (assoc request :auth auth-map)))))

(defn wrap-user
  [handler lookup]
  (fn [request]
    (let [user (get-in request [:auth :user])
          pass (get-in request [:auth :pass])]
      (handler (assoc request :user (lookup user pass))))))

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

(defn wrap-utf-8
  [handler]
  (fn [request]
    (let [response (handler request)]
      (charset response "utf-8"))))

(defn wrap-append-newline
  [handler]
  (fn [request]
    (let [response (handler request)
          body (:body response)]
      (if (instance? String body)
        (update-in response [:body] #(str % \newline))
        response))))
