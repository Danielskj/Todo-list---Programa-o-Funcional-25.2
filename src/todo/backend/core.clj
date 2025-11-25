(ns todo.backend.core
  (:require
    [ring.middleware.params :refer [wrap-params]]
    [ring.adapter.jetty :as jetty]
    [reitit.ring :as ring]
    [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]
    [ring.middleware.cors :refer [wrap-cors]]
    [todo.backend.handler :as handler]
    [todo.backend.db :as db])
  (:gen-class))


(def app-routes
  (ring/router
    ["/api"
     ["/hello" {:get {:handler handler/hello-handler}}]

     ["/todos"
      {:get {:handler handler/list-todos-handler}
       :post {:handler handler/create-todo-handler}}]

     ["/todos/:id/toggle"
      {:post {:handler handler/toggle-todo-handler}}]]))


(def app
  (ring/ring-handler
    app-routes
    (ring/create-default-handler)
    {:middleware [
                  ;; ---- CORS (primeiro sempre) ----
                  [wrap-cors
                   :access-control-allow-origin [#"http://localhost:8000"]
                   :access-control-allow-methods [:get :post :put :delete]
                   :access-control-allow-headers ["Content-Type"]]

                  ;; ---- Outros middlewares ----
                  wrap-json-response
                  [wrap-json-body {:keywords? true}]
                  wrap-keyword-params
                  wrap-params
                  ]}))


;; Servidor

(defn start-server [port]
  (println (str "Servidor iniciado na porta " port))
  (jetty/run-jetty #'app {:port port :join? false}))

(defn -main [& args]
  (let [port (Integer/parseInt (or (first args) "3000"))]
    (db/initialize-database!) ;; Garante que a tabela exista
    (start-server port)))


;;att 1 backend