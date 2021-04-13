(ns server
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.net ServerSocket]))

(defn handle-get
  ([] [:ERROR "expected 1 argument to GET"])
  ([_] (handle-get))
  ([env word & _] (if (contains? @env word)
                    [:ANSWER (get @env word)]
                    [:ERROR (str "can't find " word)])))

(defn handle-set
  ([] [:ERROR "expected at least 2 arguments to SET"])
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
      [:ERROR "Unknown Command"])))

(defn client-send [socket msg]
  (let [client (io/writer socket)]
    (.write client msg)
    (.flush client)))

(defn client-receive [socket]
  (let [client (io/reader socket)]
    (.readLine client)))

(defn main [port]
  (let [env (atom {"lisp" "LISt Processor"})
        running (atom true)]
    (with-open [server-socket (ServerSocket. port)]
      (while @running
        (let [s (.accept server-socket)]
          (future (let [msg (client-receive s)
                        reply (handle msg env)]
                    (client-send s (str reply "\n"))
                    (.close s))))))
    running))

(def my-server (main 9999))