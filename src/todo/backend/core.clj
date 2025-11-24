(ns todo.backend.core
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [todo.backend.handler :as handler]
            ;; --- ADICIONE ESTA LINHA ---
            [todo.backend.db :as db]))
  (:gen-class))

(ns todo.backend.core
  (:require
    [todo.backend.handler :refer [app]]
    [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defonce server (atom nil))

(defn start-server []
  (reset! server
          (run-jetty app {:port 3000 :join? false}))
  (println "Servidor iniciado em http://localhost:3000"))

(defn stop-server []
  (when @server
    (.stop @server)
    (reset! server nil)
    (println "Servidor parado.")))

;; CÓDIGO CORRIGIDO
(defn -main [& args]
  (let [port (Integer/parseInt (or (first args) "3000"))]
    ;; --- ADICIONE ESTA LINHA ---
    (db/initialize-database!) ;; Garante que a tabela exista
    ;; --------------------------
    (start-server port)))