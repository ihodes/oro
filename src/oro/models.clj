(ns oro.models
  (:require [korma.db :refer :all]
            [korma.core :refer :all]))


(def +db+ (postgres {:db "orodb"
                     :username "oro"
                     :password "oro"}))

(defdb orodb +db+)


(declare users transactions customers)

(defentity users
  (pk :uuid)
  (has-many transactions {:fk :user_uuid})
  (has-many customers    {:fk :user_uuid}))

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
