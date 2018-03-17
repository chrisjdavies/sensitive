(ns sensitive.core
  "Provides the `sensitive` function for protecting data that should
  not leak into logs."
  (:require [clojure.pprint]))

(def ^:dynamic *redacted-string* "***REDACTED***")

(defrecord Secret [s]
  Object
  (toString [_] *redacted-string*)
  clojure.lang.IDeref
  (deref [_] s))

(defmethod print-method Secret
  [_ ^java.io.Writer w]
  (print-method *redacted-string* w))

(defmethod clojure.pprint/simple-dispatch Secret
  [secret]
  (.write *out* (pr-str secret)))

(defn sensitive
  "Wrap a sensitive value in an IDeref.  The secret value will only be
  disclosed via an explicit `deref`, thus preventing accidental leaks
  to logging, etc."
  [s]
  (->Secret s))
