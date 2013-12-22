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
  [[:route-params user-uuid] :as req]
  (response (select users
              (where {:uuid (uuid user-uuid)}))))

(defnk create-user
  [[:json email password] :as req]
  (response (insert users
              (values {:email email :password password
                       :api_secret (random-string) :api_public (random-string)}))))

(defnk update-user
  [[:json password :as updated] [:route-params user-uuid] :as req]
  (response (update users (set-fields updated)
              (where {:uuid (uuid user-uuid)}))))

(defnk delete-user
  [[:route-params user-uuid] :as req]
  (response (delete users
              (where {:uuid (uuid user-uuid)}))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Transactions
(defn get-transactions [req]
  (response {:transactions (select transactions)}))

(defnk get-transaction
  [[:route-params transaction-uuid] :as req]
  (response (select transactions
              (where {:uuid (uuid transaction-uuid)}))))

(defnk create-transaction
  [[:json user_uuid amount source destination :as fields] :as req]
  (response (insert transactions
              (values (update-in fields [:user_uuid] uuid)))))

(defnk update-transaction
  [[:json amount :as updated] [:route-params transaction-uuid] :as req]
  (response (update transactions (set-fields updated)
              (where {:uuid (uuid transaction-uuid)}))))

(defnk delete-transaction
  [[:route-params transaction-uuid] :as req]
  (response (delete transactions
              (where {:uuid (uuid transaction-uuid)}))))

(defnk capture-transaction
  [[:route-params transaction-uuid] :as req]
  (response (update transactions
              (set-fields {:captured_at (sqlfn now)})
              (where {:uuid (uuid transaction-uuid)}))))

(defnk refund-transaction
  [[:route-params transaction-uuid] :as req]
  (response (update transactions
              (set-fields {:refunded_at (sqlfn now)})
              (where {:uuid (uuid transaction-uuid)}))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Customers
(defn get-customers [req]
  (response {:customers (select customers)}))

(defnk get-customer
  [[:route-params customer-uuid] :as req]
  (response (select customers
              (where {:uuid (uuid customer-uuid)}))))

(defnk create-customer
  [[:json name user_uuid :as fields] :as req]
  (response (insert customers
              (values (update-in fields [:user_uuid] uuid)))))

(defnk update-customer
  [[:json name :as updated] [:route-params customer-uuid] :as req]
  (response (update customers (set-fields updated)
              (where {:uuid (uuid customer-uuid)}))))

(defnk delete-customer
  [[:route-params customer-uuid] :as req]
  (response (delete customers
              (where {:uuid (uuid customer-uuid)}))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Cards
(defn get-cards [req]
  (response {:cards (select cards)}))

(defnk get-card
  [[:route-params card-uuid] :as req]
  (response (select cards
              (where {:uuid (uuid card-uuid)}))))

(defnk create-card
  [[:json name number type expiration ccv user_uuid :as fields] :as req]
  (response (insert cards
              (values (update-in fields [:user_uuid] uuid)))))

(defnk update-card
  [[:json name number type expiration ccv :as updated]
   [:route-params card-uuid] :as req]
  (response (update cards (set-fields updated)
              (where {:uuid (uuid card-uuid)}))))

(defnk delete-card
  [[:route-params card-uuid] :as req]
  (response (delete cards
              (where {:uuid (uuid card-uuid)}))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Plans
(defn get-plans [req]
  (response {:plans (select plans)}))

(defnk get-plan
  [[:route-params plan-uuid] :as req]
  (response (select plans
              (where {:uuid (uuid plan-uuid)}))))

(defnk create-plan
  [[:json amount name description interval :as fields] :as req]
  (response (insert plans
              (values fields))))

(defnk update-plan
  [[:json amount name description interval :as updated]
   [:route-params plan-uuid] :as req]
  (response (update plans (set-fields updated)
              (where {:uuid (uuid plan-uuid)}))))

(defnk delete-plan
  [[:route-params plan-uuid] :as req]
  (response (delete plans
              (where {:uuid (uuid plan-uuid)}))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Subscriptions
(defn get-subscriptions [req]
  (response {:subscriptions (select subscriptions)}))

(defnk get-subscription
  [[:route-params subscription-uuid] :as req]
  (response (select subscriptions
              (where {:uuid (uuid subscription-uuid)}))))

(defnk create-subscription
  [[:json plan_uuid customer_uuid :as fields] :as req]
  (response (insert subscriptions
              (values (update-in fields [:user-uuid] uuid)))))

(defnk update-subscription
  [[:json plan_uuid :as updated]
   [:route-params subscription-uuid] :as req]
  (response (update subscriptions (set-fields updated)
              (where {:uuid (uuid subscription-uuid)}))))

(defnk delete-subscription
  [[:route-params subscription-uuid] :as req]
  (response (delete subscriptions
              (where {:uuid (uuid subscription-uuid)}))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Tokens
(defn get-tokens [req]
  (response {:tokens (select tokens)}))

(defnk get-token
  [[:route-params token-uuid] :as req]
  (response (select tokens
              (where {:uuid (uuid token-uuid)}))))

(defnk create-token
  [[:json name number type expiration ccv :as fields] :as req]
  (response (insert tokens
              (values (update-in fields [:user-uuid] uuid)))))

(defnk delete-token
  [[:route-params token-uuid] :as req]
  (response (delete tokens
              (where {:uuid (uuid token-uuid)}))))

