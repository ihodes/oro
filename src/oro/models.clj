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
p
