(ns oro.core
  (:require  [plumbing.core :refer :all]

             [compojure.core     :refer [defroutes GET POST PUT DELETE ANY context]]
             [org.httpkit.server :refer :all] 

             [oro.pages        :refer :all]
             [oro.utils        :refer :all]
             [oro.middleware   :refer :all]
             [oro.controllers  :refer :all]
             [oro.responses    :refer :all]))


(def +api-version+ "v1.0")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Routes
(defroutes routes
  (GET "/" [] landing-page)
  (context "/api/v1.0" []
    (GET "/" [] "You are connected to the API.")

    (context "/account" []
      (GET "/"          []     get-account-information))
    
    (context "/users"        []
      (GET "/"               []           get-users)
      (GET "/:user-uuid"     [user-uuid]    get-user)
      (POST "/"              []           create-user)
      (POST "/:user-uuid"    [user-uuid]    update-user)
      (DELETE "/:user-uuid"  [user-uuid]    delete-user))

    ;; Calling this transaction, as it allows us to be more flexible with what sorts
    ;; of payment methods we can handle. E.g. cash equiv cards sold in supermarkets,
    ;; or mobile payments... etc. 
    (context "/transactions"     []
      (GET "/"                   []                       get-transactions)
      (GET "/:transaction-uuid"  [transaction-uuid]         get-transaction)
      (POST "/"                  []                       create-transaction)
      (POST "/:transaction-uuid" [transaction-uuid]         update-transaction)
      (POST "/:transaction-uuid/refund"  [transaction-uuid] refund-transaction)
      (POST "/:transaction-uuid/capture" [transaction-uuid] capture-transaction))

    (context "/customers"        []
      (GET "/"                   []               get-customers)
      (GET "/:customer-uuid"     [customer-uuid]    get-customer)
      (POST "/"                  []               create-customer)
      (POST "/:customer-uuid"    [customer-uuid]    update-customer)
      (DELETE "/:customer-uuid"  [customer-uuid]    delete-customer))

    (context "/cards"      []
      (GET "/"             []           get-cards)
      (GET "/:card-uuid"   [card-uuid]    get-card)
      (POST "/"            []           create-card)
      (POST "/:card-uuid"  [card-uuid]    update-card)
      (DELETE "/:card-uuid"[card-uuid]    delete-card))

    (context "/plans"        []
      (GET "/"               []           get-plans)
      (GET "/:plan-uuid"     [plan-uuid]    get-plan)
      (POST "/"              []           create-plan)
      (POST "/:plan-uuid"    [plan-uuid]    update-plan)
      (DELETE "/:plan-uuid"  [plan-uuid]    delete-plan))

    (context "/subscriptions"        []
      (GET "/"                       []                 get-subscriptions)
      (GET "/:subscription-uuid"     [subscription-uuid]  get-subscription)
      (POST "/"                      []                 create-subscription)
      (POST "/:subscription-uuid"    [subscription-uuid]  update-subscription)
      (DELETE "/:subscription-uuid"  [subscription-uuid]  delete-subscription))

    (context "/tokens"     []
      (GET  "/"            []            get-tokens)
      (GET  "/:token-uuid" [token-uuid]  get-token)
      (POST "/"            []            create-token)))

  (ANY "/*" [] (respond-json-404)))

;; Stripe also has: 
;; * Coupons
;; * Discounts
;; * Invoices
;; * Invoice Items
;; * Disputes
;; * Transfers
;; * Recipients
;; * Application Fees
;; * Events
;; * Balance 
;; * + Webhooks

;; Also worth looking into...
;; * Fraud -- since we'd be setting up people as their own MOR, this would be useful


(def app
  (r<- #'routes
       wrap-string-body
       wrap-json-request

       wrap-json-response))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; Run the server
(defonce stop-server #())
(stop-server)
(def stop-server (run-server #'app {:port 8080}))
