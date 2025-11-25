(ns todo.backend.db
  "Este namespace gerencia os dados dos 'todos'
   usando um banco de dados persistente SQLite."
  (:require [next.jdbc :as jdbc]
            [clojure.string :as str]))

;; --- 1. A Configuração do Banco ---
(def db-spec {:dbtype "sqlite"
              :dbname "prod.db"})

;; --- 2. Inicialização ---
(defn initialize-database!
  "Cria a tabela 'todos' no banco de dados se ela não existir."
  []
  (jdbc/execute! db-spec ["
    CREATE TABLE IF NOT EXISTS todos (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      title TEXT,
      description TEXT,
      completed BOOLEAN DEFAULT 0,
      created_at TEXT
    )
  "]))

;; --- 3. Funções do Banco ---

(defn get-all-todos []
  (jdbc/execute! db-spec ["SELECT * FROM todos ORDER BY created_at DESC"]))

(defn create-todo [todo-data]
  (let [todo-map (assoc todo-data
                        :completed 0
                        :created-at (str (java.time.Instant/now)))
        result (jdbc/execute-one!
                 db-spec
                 ["INSERT INTO todos (title, description, completed, created_at)
                   VALUES (?, ?, ?, ?)"
                  (:title todo-map)
                  (:description todo-map)
                  (:completed todo-map)
                  (:created-at todo-map)]
                 {:returning "*"})]
    result))
(defn toggle-todo!
  "Alterna o status 'completed' de um todo no banco."
  [id]
  ;; (1 - completed) é um truque SQL para inverter 0 -> 1 e 1 -> 0.
  (jdbc/execute-one! db-spec ["
    UPDATE todos
    SET completed = (1 - completed)
    WHERE id = ?
    RETURNING *"
    id
  ]))