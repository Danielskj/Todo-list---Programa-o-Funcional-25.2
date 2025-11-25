(ns todo.backend.handler
  "Handlers das rotas da API"
  (:require
    [todo.backend.db :as db]
    [clojure.string :as str]))

;; ------------------------------
;; HELLO HANDLER
;; ------------------------------
(defn hello-handler
  [_request]
  {:status 200
   :body "Hello, World!"})

;; ------------------------------
;; LISTAR TODOS
;; ------------------------------
(defn list-todos-handler
  "Handler para GET /api/todos."
  [_request]
  {:status 200
   :body {:todos (db/get-all-todos)}})

;; ------------------------------
;; CRIAR TODO
;; ------------------------------
(defn create-todo-handler
  "Handler para POST /api/todos."
  [{:keys [body]}]
  (let [title (:title body)]
    (if (and title (not (str/blank? title)))
      (let [new-todo (db/create-todo body)]
        {:status 201
         :body new-todo})
      {:status 400
       :body {:error "O campo 'title' é obrigatório."}})))
(defn toggle-todo-handler
  "Handler para 'alternar' o status de um todo."
  [request]
  (let [id (-> request :path-params :id Integer/parseInt)]
    (if-let [updated-todo (db/toggle-todo! id)]
      {:status 200 :body updated-todo}
      {:status 404 :body {:error "Todo não encontrado"}})))