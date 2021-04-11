(ns server
  (:require [clojure.java.io :as io])
  (:import [java.net ServerSocket]))

(defn client-send [socket msg]
  (let [client (io/writer socket)]
    (.write client msg)
    (.flush client)))

(defn client-receive [socket]
  (let [client (io/reader socket)]
    (.readLine client)))

(defn main []
  (with-open [server-socket (ServerSocket. 9999 1)
              socket (.accept server-socket)]
    (client-send socket "Welcome!\n")
    (let [msg (client-receive socket)]
      (client-send socket msg))))

(main)