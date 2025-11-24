(ns todo.backend.db
  "Este namespace gerencia os dados dos 'todos'
   usando um banco de dados persistente SQLite.")

(require '[next.jdbc :as jdbc]
         '[clojure.string :as str])

;; --- 1. A Configuração (Especificação do Banco) ---
;; Definimos como nos conectar ao nosso banco de dados.
;; Ele será salvo em um arquivo chamado "prod.db".
(def db-spec {:dbtype "sqlite"
              :dbname "prod.db"})

;; --- 2. Função de Inicialização ---
;; Esta função cria nossa tabela "todos" se ela ainda não existir.
;; Vamos chamá-la em nosso 'core.clj' quando o servidor iniciar.
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

;; --- 3. As Novas Funções (Substituindo os 'atoms') ---

(defn get-all-todos
  "Retorna uma lista com todos os 'todos' no banco."
  []
  ;; O 'execute!' roda o SQL e já retorna os mapas Clojure!
  (jdbc/execute! db-spec ["SELECT * FROM todos ORDER BY created_at DESC"]))

(defn create-todo
  "Cria um novo 'todo', salva no banco e o retorna."
  [todo-data] ;; ex: {:title "Minha tarefa", :description "..."}
  (let [;; Convertemos o booleano para 0 (false)
        todo-map (assoc todo-data
                        :completed 0
                        :created-at (str (java.time.Instant/now)))
        ;; 'execute-one!' insere e já retorna o item criado
        result (jdbc/execute-one! db-spec ["
          INSERT INTO todos (title, description, completed, created_at)
          VALUES (?, ?, ?, ?)"
          (:title todo-map)
          (:description todo-map)
          (:completed todo-map)
          (:created-at todo-map)
          ;; :returning "*" faz o SQLite retornar o item inserido
          ] {:returning "*"})]
    result))