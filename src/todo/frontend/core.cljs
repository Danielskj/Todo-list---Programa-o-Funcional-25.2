(ns todo.frontend.core
  (:require
    [reagent.core :as r]
    [reagent.dom.client :as rdom]
    [clojure.string :as str]
    [cljs.core.async :refer [go]]
    [cljs.core.async.interop :refer-macros [<p!]]))

;; --- 1. Estado Global Reativo ---
(defonce app-state
  (r/atom {:next-id 1
           :input-text ""
           :todos []
           :loading false
           :error nil}))

(def api-url "http://localhost:3000/api")

;; -------------------------------
;; Helpers para fetch
;; -------------------------------
(defn fetch-json [url options]
  (-> (js/fetch url (clj->js options))
      (.then (fn [response]
               (when-not (.-ok response)
                 (throw (js/Error. (str "HTTP error: " (.-status response)))))
               (.json response)))
      (.then #(js->clj % :keywordize-keys true))))

;; -------------------------------
;; GET /todos
;; -------------------------------
(defn get-todos []
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (let [response (<p! (fetch-json (str api-url "/todos") {:method "GET"}))]
        (swap! app-state assoc
               :todos (:todos response)
               :loading false))
      (catch js/Error e
        (swap! app-state assoc :error (.-message e) :loading false)))))


;; -------------------------------
;; POST /todos
;; -------------------------------
(defn create-todo [todo-data]
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (<p! (fetch-json (str api-url "/todos")
                       {:method "POST"
                        :headers {"Content-Type" "application/json"}
                        :body (js/JSON.stringify (clj->js todo-data))}))
      (get-todos)
      (catch js/Error e
        (swap! app-state assoc :error (.-message e) :loading false)))))


;; -------------------------------
;; POST /todos/:id/toggle  (NOVO)
;; -------------------------------
(defn toggle-todo
  "Chama a API para alternar o status de um todo."
  [id]
  (go
    (try
      (<p! (fetch-json (str api-url "/todos/" id "/toggle")
                       {:method "POST"}))
      ;; Atualiza a lista inteira
      (get-todos)
      (catch js/Error e
        (swap! app-state assoc :error (.-message e) :loading false)))))


;; -------------------------------
;; Formulário
;; -------------------------------
(defn todo-form []
  [:div.todo-input
   [:input
    {:type "text"
     :placeholder "O que precisa ser feito?"
     :value (:input-text @app-state)
     :on-change #(swap! app-state assoc :input-text (-> % .-target .-value))}]

   [:button
    {:on-click (fn []
                 (create-todo {:title (:input-text @app-state)})
                 (swap! app-state assoc :input-text ""))}
    "Adicionar"]])


;; -------------------------------
;; Lista de todos  (ATUALIZADA)
;; -------------------------------
(defn todo-list []
  [:ul.todo-list
   (for [todo (:todos @app-state)]
     ^{:key (:todos/id todo)}

     [:li.todo-item
      {:class (when (= 1 (:todos/completed todo)) "completed")}

      ;; checkbox de completar
      [:input.todo-checkbox
       {:type "checkbox"
        :checked (not= 0 (:todos/completed todo))   ;; CORREÇÃO IMPORTANTE
        :on-change #(toggle-todo (:todos/id todo))}]

      ;; título
      (:todos/title todo)])])


;; -------------------------------
;; App principal
;; -------------------------------
(defn app []
  [:div.todo-app
   [:h1 "Todo App (API Integrada)"]
   [todo-form]
   [todo-list]])


;; -------------------------------
;; Inicialização
;; -------------------------------
(defn ^:export init []
  (println "Frontend inicializado...")
  (let [root (rdom/create-root (js/document.getElementById "app"))]
    (.render root (r/as-element [app])))
  (get-todos))
