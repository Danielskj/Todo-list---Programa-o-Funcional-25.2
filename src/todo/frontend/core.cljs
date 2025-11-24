(ns todo.frontend.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdom]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(defonce app-state
  (r/atom {:todos []
           :loading false
           :error nil}))

(def api-url "http://localhost:3000/api")

(defn fetch-json [url options]
  (-> (js/fetch url (clj->js options))
      (.then (fn [response]
               (when-not (.-ok response)
                 (throw (js/Error. (str "HTTP error: " (.-status response)))))
               (.json response)))
      (.then #(js->clj % :keywordize-keys true))))

(defn get-todos []
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (let [response (<p! (fetch-json (str api-url "/todos") {:method "GET"}))]
        (swap! app-state assoc
               :todos response   ;; recebe lista de TODOS
               :loading false))
      (catch js/Error e
        (swap! app-state assoc
               :error (.-message e)
               :loading false)))))


(defn todos-list []
  (let [{:keys [todos loading error]} @app-state]
    [:div
     (when loading
       [:p "Carregando..."])

     (when error
       [:p {:style {:color "red"}}
        "Erro ao buscar dados: " error])

     (when (seq todos)
       [:ul
        (for [t todos]
          ^{:key (:id t)}            ;; <-- CORRIGIDO
          [:li (:title t)])])]))     ;; <-- CORRIGIDO


(defn app []
  [:div
   [:h1 "Todo App (Frontend → Backend)"]
   [todos-list]])

(defn ^:export init []
  (println "Frontend inicializado...")
  (let [root (rdom/create-root (js/document.getElementById "app"))]
    (.render root (r/as-element [app])))
  (get-todos))
