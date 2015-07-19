Luminus sets up a default test harness found in the `test` directory of the project.

The database tests run using the `:test` profile. This profile is the composite of the `:project/test` and
the `:profiles/test` profiles from `project.clj` and `profiles.clj` respectively.

A default test will be created for the application handler:

```clojure
(ns myapp.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [<app>.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= 404 (:status response))))))
```

In the tests, a mock request generated using the `ring.mock.request/request` function is generated and passed to the
`<app>.handler/app` function. The response is tested against the expected response for the route. The example tests
simply test that a valid route returns a response with a status `200`, while an invalid one results in a `404` response.
 
When a relational database profile is used, such as `+postgres`, then a set of tests for the database are added to
the project.

```clojure
(ns myapp.test.db.core
  (:require [clojure.test :refer :all]
            [myapp.db.core :refer :all]
            [myapp.db.migrations :as migrations]))

(deftest test-users
  ;; Make sure the user with id 1 doesn't exist.
  ;; You can also use transactions around tests to ensure that.
  (delete-user! {:id "1"})  
  (is (= 1 (create-user! {:id         "1"
                          :first_name "Sam"
                          :last_name  "Smith"
                          :email      "sam.smith@example.com"
                          :pass       "pass"})))
  (is (= (get-user {:id "1"})
         [{:id         "1"
           :first_name "Sam"
           :last_name  "Smith"
           :email      "sam.smith@example.com"
           :pass       "pass"
           :admin      nil
           :last_login nil
           :is_active  nil}])))

(use-fixtures :once (fn [f] (migrations/migrate ["migrate"]) (f)))
```

The database connection for the test database should be defined inside the `:profiles/test` profile found in the `profiles.clj`. 