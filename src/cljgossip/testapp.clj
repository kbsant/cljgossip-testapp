;; This app is a test repl session, for now.
;; To use it, load this name space in the repl and evaluate each expression as needed.
(ns cljgossip.testapp
  (:require 
            [cljgossip.core :as gossip]
            [cljgossip.wsclient :as client]
            [cljgossip.handlers :as handlers]))

(def app
  (atom
   {:game {:players #{}}
    :env {}}))

(defn list-players-fn [app]
  (fn []
    (get-in @app [:game :players])))

(defn gossip-handlers [app]
  {:cljgossip/on-heartbeat
   (partial handlers/default-on-heartbeat (list-players-fn app))})

(defn add-player! [app conn player-name]
  (gossip/sign-in conn player-name)
  (swap! app update-in [:game :players] conj player-name))

(defn remove-player! [app conn player-name]
  (gossip/sign-out conn player-name)
  (swap! app update-in [:game :players] disj player-name))

;; TODO: swap! config into env before logging in
(def conn (gossip/login (:env @app) client/ws-connect (gossip-handlers app)))

(add-player! app conn "Frida")

(gossip/send-all conn "gossip" "Frida" "hi")

(add-player! app conn "Thor")

(gossip/send-all conn "gossip" "Thor" "hello")

(gossip/send-to conn "Frida" "GameName" "Thor" "hi, Thor")

(gossip/status conn nil)

(remove-player! app conn "Frida")

(remove-player! app conn "Thor")

(gossip/close conn)


