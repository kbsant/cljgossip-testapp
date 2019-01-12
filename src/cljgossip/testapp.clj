;; This app is a test repl session, for now.
;; To use it, load this name space in the repl and evaluate each expression as needed.
(ns cljgossip.testapp
  (:require 
            [cljgossip.core :as gossip]
            [cljgossip.wsclient :as client]
            [cljgossip.handlers :as handlers]))

;; The test application state contains a set of players.
(def app
  (atom
   {:game {:players #{}}
    :env {}}))

;; Callback function to return the set of players logged in.
(defn list-players-fn [app]
  (fn []
    (get-in @app [:game :players])))

;; Given a reference to the application state, return
;; a map of handlers for gossip events.
(defn gossip-handlers [app]
  {:cljgossip/on-heartbeat
   (partial handlers/default-on-heartbeat (list-players-fn app))})

;; Add a player and sign into gossip.
(defn add-player! [app conn player-name]
  (gossip/sign-in conn player-name)
  (swap! app update-in [:game :players] conj player-name))

;; Remove a player from the app and sign out of gossip.
(defn remove-player! [app conn player-name]
  (gossip/sign-out conn player-name)
  (swap! app update-in [:game :players] disj player-name))

;; The comment block below contains a sample repl session.
;; After logging in at the application layer, players sign in
;; and send messages.
;; Note: gossip is half-duplex, meaning messages from players
;; are not broadcasted to the sending game (except for tells,
;; where the target player is explicitly set). An application needs
;; to broadcast messages locally by itself -- for simplicity,
;; the logic to broadcast messages locally is not included
;; as it is not part of the gossip protocol.
(comment
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

  )


