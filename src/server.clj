(ns server
  (:require [clojure.java.io :as io])
  (:import [java.net ServerSocket]))


(defn main []
  (with-open [server-socket (ServerSocket. 9999 1)
              socket (.accept server-socket)
              o (io/writer socket)]
    (.write o "Hello World!")))

(main)