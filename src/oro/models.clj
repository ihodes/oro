(ns oro.models
  (:require [plumbing.core :refer :all]
            
            [korma.db :refer :all]
            [korma.core :refer :all]
            [environ.core :refer [env]]
            [oro.utils :refer [encrypt verify]]))


(def +db+ (postgres {:db       (env :db-name)
                     :username (env :db-username)
                     :password (env :db-password)}))
(defdb db +db+)



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


(defn belongs?
  "True if the object belongs to the user; verified by checking that the
   the user_uuid is equal to the uuid of the user."
  [user object]
  (= (:uuid user) (:user_uuid object)))

(defn user-by-secret
  [_ pass]
  (when pass
    (first (select users (where {:api_secret pass})))))
