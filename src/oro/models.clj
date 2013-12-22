(ns oro.models
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [clojurewerkz.scrypt.core :as scrypt]))


;; TK FACTOR OUT
(defn encrypt
  [pw]
  (scrypt/encrypt pw (Math/pow 2 16) 8 1))

(defn verify
  [plain-pw encrypted-pw]
  (scrypt/verify plain-pw encrypted-pw))



(def +db+ (postgres {:db "orodb"
                     :username "oro"
                     :password "oro"}))

(defdb orodb +db+)


(declare users transactions customers)

(defentity users
  (pk :uuid)
  (has-many transactions {:fk :user_uuid})
  (has-many customers    {:fk :user_uuid})
  (prepare (fn [{password :password :as v}]
             (assoc v :password (encrypt password)))))

(defentity transactions
  (pk :uuid)
  (belongs-to users {:fk :user_uuid}))

(defentity customers
  (pk :uuid)
  (belongs-to users {:fk :user_uuid}))

(defentity cards
  (pk :uuid)
  (belongs-to users {:fk :user_uuid})
  (belongs-to customers {:fk :customer_uuid}))

(defentity plans
  (pk :uuid)
  (belongs-to users {:fk :user_uuid}))

(defentity subscriptions
  (pk :uuid)
  (belongs-to users     {:fk :user_uuid})
  (belongs-to plans     {:fk :plan_uuid})
  (belongs-to customers {:fk :customer_uuid}))

(defentity tokens
  (pk :uuid)
  (belongs-to users {:fk :user_uuid})
  (belongs-to cards {:fk :card_uuid}))


;; TK TODO should be factored out to elsewhere...
(defn user-by-secret
  [api-secret]
  (select users (where {:api_secret api-secret})))
