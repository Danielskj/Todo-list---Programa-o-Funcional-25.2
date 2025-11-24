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

(defn -main []
  (start-server))
