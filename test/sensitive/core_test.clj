(ns sensitive.core-test
  (:require [clojure.test :refer :all]
            [sensitive.core :refer :all]))

(deftest sensitive-tests
  (let [secret (sensitive "hello")]
    (testing "toString should not leak the secret"
      (is (not= "hello" (str secret))))
    (testing "pr-str (or equivalent) should not leak the secret"
      (is (not= "hello" (pr-str secret))))
    (testing "deref provides access to the secret"
      (is (= "hello" @secret)))
    (testing "redacted placeholder can be customised"
      (binding [*redacted-string* "XXX"]
        (is (= "XXX" (str secret)))))))

(deftest use-with-edn
  (testing "the sensitive function can be used directly as an EDN reader"
    (let [config-str "{:username \"foo\", :password #sensitive \"bar\"}"
          config (clojure.edn/read-string {:readers {'sensitive sensitive}}
                                          config-str)]
      (is (= "foo" (:username config)))
      (is (not= "bar" (:password config)))
      (is (= "bar" @(:password config))))))
