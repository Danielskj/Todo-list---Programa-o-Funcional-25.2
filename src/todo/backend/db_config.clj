(ns todo.backend.db-config)

;; Esta é a "especificação" do nosso banco de dados.
;; É um mapa que o next.jdbc usa para saber como se conectar.
(def db-spec
  {:dbtype "sqlite"       ;; O tipo de banco
   :dbname "prod.db"})     ;; O nome do arquivo onde tudo será salvo