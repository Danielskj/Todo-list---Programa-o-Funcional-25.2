(ns todo.backend.db
  "Este namespace gerencia os dados dos 'todos' em memória.")

;; (def) cria uma 'var' global.
;; (atom {}) cria nossa "caixa" (atom) e coloca
;; um mapa imutável vazio {} dentro dela.
(def todos-db (atom {}))
;; Nosso banco terá a forma: {1 {:id 1, :title "..."}, 2 {:id 2, ...}}

;; Criamos uma "caixa" separada para o contador de IDs.
(def next-id (atom 1))


;; --- Nossas Funções de Acesso ao Banco ---

(defn get-all-todos
  "Retorna uma lista com todos os 'todos' no banco."
  []
  ;; @todos-db: "Olha dentro da caixa 'todos-db' (lê o valor).
  ;; (vals): Pega apenas os valores do mapa (ignora as chaves/IDs).
  (vec (vals @todos-db)))

(defn create-todo
  "Cria um novo 'todo', salva no banco e o retorna."
  [todo-data] ;; ex: {:title "Minha tarefa"}
  (let [;; 1. Lê o ID atual (ex: 1) usando '@'
        id @next-id
        
        ;; 2. Cria um NOVO mapa imutável com os dados completos
        new-todo (assoc todo-data
                        :id id
                        :completed false
                        :created-at (str (java.time.Instant/now)))]
    
    ;; 3. (swap!): "Troca" o conteúdo da caixa 'todos-db'.
    ;;    Ele aplica a função 'assoc' ao valor *antigo* (o mapa)
    ;;    para criar um *novo* mapa, que agora é guardado na caixa.
    (swap! todos-db assoc id new-todo)
    
    ;; 4. (swap! ... inc): Incrementa o contador na caixa 'next-id'.
    (swap! next-id inc)
    
    ;; 5. Retorna o 'todo' recém-criado.
    new-todo))