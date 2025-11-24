(ns todo.backend.handler
  (:require
    [todo.backend.db :as db]
    [reitit.ring :as ring]
    [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(def app
  (-> (ring/ring-handler
        (ring/router
          [["/api"
            ["/todos"
             {:get  (fn [_]
                      {:status 200
                       :body (db/get-all-todos)})

              :post (fn [req]
                      (let [todo (:body req)]
                        {:status 201
                         :body (db/create-todo todo)}))}]]]))
      wrap-json-response
      (wrap-json-body {:keywords? true})))
