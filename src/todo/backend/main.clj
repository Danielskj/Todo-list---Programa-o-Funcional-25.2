(ns todo.backend.main
  (:require
    [ring.adapter.jetty :refer [run-jetty]]
    [reitit.ring :as ring]
    [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
    [ring.util.response :refer [response]]
    [ring.middleware.cors :refer [wrap-cors]]))

;; ----- Estado em memória -----
(defonce todos
  (atom
    [{:id 1 :titulo "Estudar Clojure"}
     {:id 2 :titulo "Ler documentação do Reagent"}]))

;; ----- Rotas -----
(def router
  (ring/router
    [["/api"
      ["/todos"
       {:get (fn [_]
               (response {:todos @todos}))}]]]))

(def app
  (-> (ring/ring-handler router)
      wrap-json-response
      (wrap-cors
        :access-control-allow-origin [#".*"]
        :access-control-allow-methods [:get :post :put :delete])))

;; ----- Servidor -----
(defn -main []
  (println "Servidor iniciado na porta 3000")
  (run-jetty app {:port 3000 :join? false}))
