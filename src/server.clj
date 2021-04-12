(ns server
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.net ServerSocket]))

(defn handle-get
  ([] [:ERROR "expected 2 arguments to GET"])
  ([_] (handle-get))
  ([env word & _] (if (contains? @env word)
                    [:ANSWER (get @env word)]
                    [:ERROR (str "can't find " word)])))

(defn handle-set
  ([] [:ERROR "expected at least 3 arguments"])
  ([_] (handle-set))
  ([_ _] (handle-set))
  ([env word & definition] [:OK (let [word-def (str/join " " definition)]
                                  (swap! env assoc word word-def))]))

(defn handle [msg env]
  (let [[command & args] (str/split msg #" ")]
    (case command
      "GET" (apply handle-get env args)
      "SET" (apply handle-set env args)
      "ALL" [:ANSWER @env]
      "CLEAR" [:OK (reset! env {})]
      "Unknown command")))

(defn client-send [socket msg]
  (let [client (io/writer socket)]
    (.write client msg)
    (.flush client)))

(defn client-receive [socket]
  (let [client (io/reader socket)]
    (.readLine client)))

(defn main [port]
  (let [env (atom {"lisp" "LISt Processor"})]
    (with-open [server-socket (ServerSocket. port 1)
                socket (.accept server-socket)]
      (client-send socket "Welcome!\n")
      (loop []
        (let [msg (client-receive socket)
              reply (handle msg env)]
          (client-send socket (str reply "\n")))
        (recur)))))

(main 9999)