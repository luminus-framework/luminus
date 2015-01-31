The validation helpers are found in the `noir.validation` namespace, and
as such we'll first have to include it in our namespace declaration to use them.

```clojure
(ns myapp.routes
  (:require ... [noir.validation :as v]))
```

The `rule` helper is used to set errors on fields when a given condition is not satisfied. It accepts
a condition function and a vector containing the key for the field being tested followed by the error to be set.
The condition should return `true` when the rule is satisfied and `false` otherwise.

The following helpers are already available for validation:

* `has-value?` args: `[v]` - returns true if v is truthy and not an empty string.
* `has-values?` args: `[coll]` - returns true if all members of the collection has-value? This works on maps as well.
* `not-nil?` args: `[v]` - returns true if v is not nil
* `min-length?` args: `[v len]` - returns true if v is greater than or equal to the given len
* `max-length?` args: `[v len` - returns true if v is less than or equal to the given len
* `matches-regex?` args: `[v regex]` - returns true if the string matches the given regular expression
* `is-email?` args: `[v]` - returns true if v is an email address
* `valid-file?` args: `[m]` - returns true if a valid file was supplied
* `valid-number?` args: `[v]` - returns true if the string can be parsed to a Long
* `greater-than?` args: `[v n]` - returns true if the string represents a number > given
* `less-than?` args: `[v n]` - returns true if the string represents a number < given
* `equal-to?` args: `[v n]` - returns true if the string represents a number = given


<p>
For example, if we wanted to check whether the field `id` and `pass` fields have values then we could create
the following rules.
</p>

```clojure
(v/rule (has-value? id)
        [:id "screen name is required"])

(v/rule (has-value? pass)
        [:pass "password is required"])
```

The errors are kept in the `noir.validation/*errors*` atom that's bound to the request. Each error is keyed
using the key supplied to the rule and points to a vector of errors. The `rule` function can be called repeatedly on the
same field to set multiple errors.



Once the validation rules have executed, we can check if any errors have been set by calling the `errors?` function.
When called with no arguments it will check that `noir.validation/*errors*` is not empty, otherwise it will check the
errors for the specific key that was passed in.

```clojure
;; returns true if any errors have been set
(v/errors?)
;; returns true if any errors have been set for the key :id
(v/errors? :id)
```

Similarly, we can also get the current errors by calling `get-errors`.
When no key is supplied then all the errors are returned.

```clojure
;; returns a sequence of all errors that have been set
(v/get-errors)
;; returns all the errors set for the key :id
(v/get-errors :id)

```

We can also provide a custom error handling function using the `on-error` helper. The function
should accept the list of errors associated with the field as its parameter. The result of
calling the function is returned by the helper.

```clojure
(v/on-error :id (fn [errors] (clojure.string/join ", " errors)))
```

Finally, errors can be cleared by calling `clear-errors!` to reset the `noir.validation/*errors*` atom.

```clojure
(v/clear-errors!)
```

Below is a complete example for validating login parameters:

```clojure
(defn set-errors [id pass]
  (v/rule (has-value? id)
        [:id "screen name is required"])

  (v/rule (has-value? pass)
        [:pass "password is required"])

  (if (v/errors? :id :pass)
    {:body {:status "error" :errors (get-errors)}}
    (do
    (session/put! :user id)
     {:body {:status "ok"}})))
```
