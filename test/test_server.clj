(ns test-server (:require [clojure.test :refer [is testing deftest]]
                          [server]))

(deftest handle-get
  (testing "handle-get"
    (testing "with incorrect arity"
      (is (= (server/handle-get) 
             [:ERROR "expected 1 argument to GET"]) 
          "zero arguments")
      (is (= (server/handle-get (atom {})) 
             [:ERROR "expected 1 argument to GET"]) 
          "one argument"))
    (testing "with unavailable word"
      (is (= (server/handle-get (atom {"a" "b"}) "word") 
             [:ERROR "can't find word"])))
    (testing "with existing word"
      (is (= (server/handle-get (atom {"a" "b"}) "a") 
             [:ANSWER "b"]) 
          "two arguments")
      (is (= (server/handle-get (atom {"a" "b"}) "a" "b") 
             [:ANSWER "b"]) 
          "> two arguments"))))

(deftest handle-set
  (testing "handle-set"
    (testing "with incorrect arity"
      (is (= (server/handle-set) 
             [:ERROR "expected at least 2 arguments to SET"]) 
          "zero arguments")
      (is (= (server/handle-set (atom {})) 
             [:ERROR "expected at least 2 arguments to SET"]) 
          "one argument")
      (is (= (server/handle-set (atom {}) "word") 
             [:ERROR "expected at least 2 arguments to SET"])
          "two arguments"))
    (testing "with correct arity"
      (is (= (server/handle-set (atom {}) "word" "definition" "123") 
             [:OK {"word" "definition 123"}]) 
          "set new word")
      (is (= (server/handle-set (atom {"word" "definition"}) "word" "definition" "123") 
             [:OK {"word" "definition 123"}]) 
          "replace existing word"))))

(deftest handle
  (testing "handle"
    (testing "uknown command"
      (is (= (server/handle "BEEPBOOP" nil)
             [:ERROR "Unknown Command"])))
    (testing "GET"
      (is (= (server/handle "GET a" (atom {"a" "b"}))
             [:ANSWER "b"])
          "available word definition")
      (is (= (server/handle "GET a" (atom {"b" "a"}))
             [:ERROR "can't find a"])
          "unavailable word definition")
      (is (= (server/handle "GET" nil)
             [:ERROR "expected 1 argument to GET"])
          "no arguments"))
    (testing "SET"
      (is (= (server/handle "SET a b" (atom {}))
             [:OK {"a" "b"}]))
      (is (= (server/handle "SET a" nil)
             [:ERROR "expected at least 2 arguments to SET"])
          "one argument")
      (is (= (server/handle "SET" nil)
             [:ERROR "expected at least 2 arguments to SET"])
          "no arguments"))
    (testing "ALL"
      (is (= (server/handle "ALL" (atom {}))
             [:ANSWER {}])
          "empty collection")
      (is (= (server/handle "ALL" (atom {"a" "b" "c" "d"}))
             [:ANSWER {"a" "b" "c" "d"}])
          "filled collection"))
    (testing "CLEAR"
      (is (= (server/handle "CLEAR" (atom {"a" "b" "c" "d"}))
             [:OK {}])))))
 
(deftest server
  (handle-get)
  (handle-set)
  (handle))

(server)