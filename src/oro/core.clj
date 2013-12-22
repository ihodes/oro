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
;;;;;;;;;; Predeclarations (...the todo list)
;; Need to be implemented
(declare get-transaction create-transaction update-transaction
         refund-transaction capture-transaction)
(declare get-customer create-customer update-customer delete-customer)

(declare get-cards get-card create-card update-card delete-card)
(declare get-plans get-plan create-plan update-plan delete-plan)
(declare get-subscriptions get-subscription create-subscription 
         update-subscription delete-subscription)
(declare get-tokens get-token create-token)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; Routes
(defroutes routes
  (GET "/" [] landing-page)
  (context "/api/v1.0" []
    (GET "/" [] "You are connected to the API.")

    (context "/account" []
      (GET "/"          []     get-account-information))
    
    (context "/users"      []
      (GET "/"             []           get-users)
      (GET "/:user-id"     [user-id]    get-user)
      (POST "/"            []           create-user)
      (POST "/:user-id"    [user-id]    update-user)
      (DELETE "/:user-id"  [user-id]    delete-user))

    ;; Calling this transaction, as it allows us to be more flexible with what sorts
    ;; of payment methods we can handle. E.g. cash equiv cards sold in supermarkets,
    ;; or mobile payments... etc. 
    (context "/transactions"   []
      (GET "/"                 []                       get-transactions)
      (GET "/:transaction-id"  [transaction-id]         get-transaction)
      (POST "/"                 []                       create-transaction)
      (POST "/:transaction-id" [transaction-id]         update-transaction)
      (POST "/:transaction-id/refund"  [transaction-id] refund-transaction)
      (POST "/:transaction-id/capture" [transaction-id] capture-transaction))

    (context "/customers"      []
      (GET "/"                 []               get-customers)
      (GET "/:customer-id"     [customer-id]    get-customer)
      (POST "/"                []               create-customer)
      (POST "/:customer-id"    [customer-id]    update-customer)
      (DELETE "/:customer-id"  [customer-id]    delete-customer))

    (context "/cards"      []
      (GET "/"             []           get-cards)
      (GET "/:card-id"     [card-id]    get-card)
      (POST "/"            []           create-card)
      (POST "/:card-id"    [card-id]    update-card)
      (DELETE "/:card-id"  [card-id]    delete-card))

    (context "/plans"      []
      (GET "/"             []           get-plans)
      (GET "/:plan-id"     [plan-id]    get-plan)
      (POST "/"            []           create-plan)
      (POST "/:plan-id"    [plan-id]    update-plan)
      (DELETE "/:plan-id"  [plan-id]    delete-plan))

    (context "/subscriptions"      []
      (GET "/"                     []                 get-subscriptions)
      (GET "/:subscription-id"     [subscription-id]  get-subscription)
      (POST "/"                    []                 create-subscription)
      (POST "/:subscription-id"    [subscription-id]  update-subscription)
      (DELETE "/:subscription-id"  [subscription-id]  delete-subscription))

    (context "/tokens"     []
      (GET  "/"            []          get-tokens)
      (GET  "/:token-id"   [token-id]  get-token)
      (POST "/"            []          create-token)))
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
