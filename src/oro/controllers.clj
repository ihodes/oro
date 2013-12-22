(ns oro.controllers
  (:require [plumbing.core :refer :all]

            [oro.models :refer :all]
            [korma.core :refer :all]
            [cheshire.core :as smile]
            [ring.util.response :refer (status response content-type header)])
  (:import [java.security SecureRandom]))


(defn- uuid [str] (java.util.UUID/fromString str))
(defn- random-string [] (.toString (java.math.BigInteger. 130 (SecureRandom.))))



(defn get-account-information
  [req]
  (response {:* "...pending..."}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Users
(defn get-users
  [req]
  (response {:users (select users)}))

(defnk get-user
  [[:route-params user-id] :as req]
  (response (select users
              (where {:uuid (uuid user-id)}))))

(defnk create-user
  [[:json email password] :as req]
  (response (insert users
              (values {:email email :password password
                       :api_secret (random-string) :api_public (random-string)}))))

(defnk update-user
  [[:json password :as updated] [:route-params user-id] :as req]
  (response (update users (set-fields updated)
              (where {:uuid (uuid user-id)}))))

(defnk delete-user
  [[:route-params user-id] :as req]
  (response (delete users
              (where {:uuid (uuid user-id)}))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Users
(defn get-transactions [req]
  (response {:transactions (select transactions)}))

(defnk get-transaction
  [[:route-params transaction-id] :as req]
  (response (select transactions
              (where {:uuid (uuid transaction-id)}))))

(defnk create-transaction
  [[:json user-uuid amount source destination :as fields] :as req]
  (response (insert transactions
              (values (update-in fields [:user-uuid] uuid)))))

#_(defnk update-transaction
  [[:json ... :as updated] [:route-params transaction-id] :as req]
  (response (update transactions (set-fields updated)
              (where {:uuid (uuid transaction-id)}))))

(defnk delete-transaction
  [[:route-params transaction-id] :as req]
  (response (delete transactions
              (where {:uuid (uuid transaction-id)}))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Users
(defn get-customers [req]
  (response {:customers (select customers)}))

(defnk get-customer
  [[:route-params customer-id] :as req]
  (response (select customers
              (where {:uuid (uuid customer-id)}))))

(defnk create-customer
  [[:json name user-uuid] :as req]
  (response (insert customers
              (values {:name name :user_uuid (uuid user-uuid)}))))

(defnk update-customer
  [[:json name :as updated] [:route-params customer-id] :as req]
  (response (update customers (set-fields updated)
              (where {:uuid (uuid customer-id)}))))

(defnk delete-customer
  [[:route-params customer-id] :as req]
  (response (delete customers
              (where {:uuid (uuid customer-id)}))))
