(ns server
  (:import [java.net ServerSocket]))


(defn main []
  (let [socket (ServerSocket. 6789 2)]
    (.accept socket)))

(main)