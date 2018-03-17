# sensitive

[![License](https://img.shields.io/github/license/chrisjdavies/sensitive.svg)](LICENSE)
[![Clojars Project](https://img.shields.io/clojars/v/chrisjd/sensitive.svg)](https://clojars.org/chrisjd/sensitive)
[![CircleCI](https://circleci.com/gh/chrisjdavies/sensitive.svg?style=svg)](https://circleci.com/gh/chrisjdavies/sensitive)

Wraps sensitive information in a derefable wrapper to prevent it
accidentally leaking into logs.

Inspired by hearing the story behind [this CircleCI blog
post](https://circleci.com/blog/how-a-simple-logging-problem-turned-into-a-bear-trap-lessons-learned/)
and then discovering that their own solution was almost identical to
what I'd come up with.


## Installation

Add the following to your `project.clj`:

```
[chrisjd/sensitive "0.1.0"]
```


## Usage

Use the `sensitive` function to wrap sensitive values:

``` clojure
user> (require '[sensitive.core :refer [sensitive]])
nil
user> (def password (sensitive "swordfish"))
#'user/password
user> password
"***REDACTED***"
user> (str "the password is: " password)
"the password is: ***REDACTED***"
```

Deref the value to obtain the secret:

``` clojure
user> (str "the password is: " @password)
"the password is: swordfish"
```

If you'd prefer a different redaction string, rebind
`sensitive.core/*redacted-string*`:

``` clojure
user> (binding [sensitive.core/*redacted-string* "XXX"]
        (println (sensitive "foo")))
XXX
nil
```


## Documentation

- [API Docs](https://chrisjdavies.github.io/sensitive/)


## Examples

### From EDN

Use the _custom readers_ feature of `clojure.edn` for convenience in
loading sensitive values from EDN.

We use the `#sensitive` tag in our configuration file:

``` clojure
{:username "foo"
 :password #sensitive "bar"}
```

Then we use the `:readers` option to handle with
`sensitive.core/sensitive`:

``` clojure
user> (let [cfg (clojure.edn/read-string {:readers {'sensitive sensitive}}
                                   (slurp "config.edn"))]
        (println (str "loaded: " cfg))
        (println (str "password: " @(:password cfg))))
loaded: {:username "foo", :password "***REDACTED***"}
password: bar
nil
```


## License

Copyright © 2018 Chris J-D

Distributed under the MIT License: https://opensource.org/licenses/MIT
