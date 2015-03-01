Luminus uses [Bouncer](https://github.com/leonardoborges/bouncer) as the default validation library.
Bouncer is a Clojure/Script library and allows us to share validation logic between the client and
the server.

Bouncer provides `bouncer.core/validate` and `bouncer.core/valid?` functions for handling validation.
These functions each accept a map containing the parameters followed by the validators.

Bouncer provides the following validators out of the box:

* `required` args: `[v]` - validates that the value is present
* `number` args: `[v]` - validates that the value is numeric
* `positive` args: `[v]` - validates that the value is a positive number
* `member` args: `[v coll]` - validates whether the value is a member of a collection
* `custom` args: `[v pred]` - validates the value using a custom validator
* `every` args: `[coll pred]` - checks that every member of the collection matches the predicate
* `matches` args: `[v regex]` - checks that the value matches the suppied regex
* `email` args: `[v]` - checks that the value is an email address
* `datetime` args: `[v & [format]]` - checks that the value is a date(time) with optional format string
* `max-count` args: `[coll n]` - validates that the collection has at most n elements
* `min-count` args: `[coll n]` - validates that the collection has at least n elements

Before we see how validation works, let's include `bouncer.core` along with the `bouncer.validators` in our namespace.

```clojure
(ns myapp.home
  (:require
    ...
    [bouncer.core :as b]
    [bouncer.validators :as v]))

```

Next, we'll define our map of parameter:

```clojure
(def user {:id nil :pass "secret"})
```

We can now validate the data using some of the built in validators as follows:


```clojure
(b/valid? user
  :id v/required
  :pass v/required)
```

The `valid?` function will return a boolean value indicating whether the data is valid or not.
When we want to see the errors then we must use the `validate` function instead.

```clojure
(b/validate user
  :id v/required
  :pass v/required)
```

The function returns a vector where the first element is the map of errors. The second element will be  the original map with a `:bouncer.core/errors` key appended in case of errors as seen below.


```clojure
[{:id ("id must be present")}
 {:bouncer.core/errors {:id ("id must be present")}
  :id nil
  :pass "secret"}]
```

We can apply multiple validators to the value by putting them into a vector:

```clojure
(b/validate user
  :id v/required
  :pass [v/required [v/min-count 8]])
```

Note how validators that take additional parameters such as `min-count` are placed in a vector, the value and will be passed in implicitly as the first parameter.

We can also validate nested maps:

```clojure
(def person
  {:address
   {:unit 10
    :street nil
    :country "Canada"}})

(b/validate person
    [:address :street] v/required
    [:address :unit]   v/number
    [:address :phone]  [[v/matches #"^\d+$"]])

```

Finally, we can easily define custom validators using `bouncer.validators/defvalidator`:

```clojure
(v/defvalidator valid-password
  {:default-message-format "%s must be at least 7 characters long"}
  [p]
  (and p (> (count p) 7)))

(b/validate user
  :id v/required
  :pass valid-password)
```

For further examples, please refer to the [official project page](https://github.com/leonardoborges/bouncer).
