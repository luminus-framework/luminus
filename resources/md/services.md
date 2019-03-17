The recommended way to write REST services in Luminus is by using the [Reitit Swagger support](https://metosin.github.io/reitit/ring/swagger.html).

The easiest way to add Swagger support is by using the `+swagger` profile:

```
lein new luminus swag +swagger
```

The resulting project will contain an `<app>.routes.services` namespace with a few example routes defined.

## Working with Swagger

We can see that routes are declared in this namespace. The routes look similar to the default route definitions,
except they declare additional metadata regarding the type of parameters they accept and the responses they returned.
this metadata is used to generate the Swagger UI test page for the services, validate requests and responses, and
handle coercion.

```clojure
["/plus"
     {:get {:summary "plus with spec query parameters"
            :parameters {:query {:x int?, :y int?}}
            :responses {200 {:body {:total pos-int?}}}
            :handler (fn [{{{:keys [x y]} :query} :parameters}]
                       {:status 200
                        :body {:total (+ x y)}})}
      :post {:summary "plus with spec body parameters"
             :parameters {:body {:x int?, :y int?}}
             :responses {200 {:body {:total pos-int?}}}
             :handler (fn [{{{:keys [x y]} :body} :parameters}]
                        {:status 200
                         :body {:total (+ x y)}})}}]]
```

The above service operation can be called from ClojureScript as follows:

```clojure
(ns swag.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]))

(defn add [params result]
  (GET "/api/plus"
       {:headers {"Accept" "application/transit+json"}
        :params @params
        :handler #(reset! result %)}))

(defn int-value [v]
  (-> v .-target .-value int))

(defn home-page []
  (let [params (atom {})
        result (atom nil)]
    (fn []
      [:div
       [:form
        [:div.form-group
         [:label "x"]
         [:input
          {:type :text
           :on-change #(swap! params assoc :x (int-value %))}]]
        [:div.form-group
         [:label "y"]
         [:input
          {:type :text
           :on-change #(swap! params assoc :y (int-value %))}]]]
       [:button.btn.btn-primary {:on-click #(add params result)} "Add"]
       (when @result
         [:p "result: " @result])])))

(reagent/render-component [home-page] (.getElementById js/document "app"))
```

The project is also setup to generate a documentation page for the service. The API documentation is available at the
`swagger.json` and `/swagger-ui` URIs declared at the top of the `service-routes` route group:

```clojure
["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

["/api-docs/*"
 {:get (swagger-ui/create-swagger-ui-handler
         {:url "/api/swagger.json"
          :config {:validator-url nil}})}]]
```

## Authentication

Services can have their own authentication rules. This is useful if you wish to return different kinds of
errors than you would when serving HTML pages.

In order to provide authentication, we'll first need to implement `wrap-restricted` middleware:


```clojure
(def wrap-restricted
  {:name :wrap-restricted
   :wrap (fn wrap-restricted [handler]
           (fn [req]
             (if (boolean (:identity req))
               (handler req)
               (unauthorized
{:error "You are not authorized to perform that action."}))))})
```

Next, we can create a restricted context:

```clojure
(defn service-routes []
  ["/api"
   {:coercion   schema-coercion/coercion
    :muuntaja   formats/instance
    :swagger    {:id ::api}
    :middleware [...]}
  ...
  ["/restricted"
      {:swagger    {:tags ["restricted"]}
       :middleware [wrap-restricted]}
   ...])
```

Any routes with `/restricted` prefix will now require authentication.
We can now define services as follows:

```clojure
(defn admin? [req]
  (and (authenticated? req)
       (#{:admin} (:role (:identity req)))))

(defn service-routes []
  ["/api"
   {:coercion   schema-coercion/coercion
    :muuntaja   formats/instance
    :swagger    {:id ::api}
    :middleware [...]}
  ["/login"
   {:post
    {:summary    "handles user login"
     :parameters {:body {:email s/Str
                         :pass  s/Str}}
     :responses  {200 {:body auth/LoginResponse}}
     :handler    (fn [{{{:keys [email pass]} :body} :parameters :as req}]
                   (assoc-in (ok {:userid userid}) [:session :identity] {:userid userid}))}}]

  ["/logout"
   {:post
    {:summary   "remove the user from the session"
     :responses {200 {:body auth/LogoutResponse}}
     :handler (fn [_] (assoc (ok "ok") :session nil))}}]
  ["/restricted"
      {:swagger    {:tags ["restricted"]}
       :middleware [wrap-restricted]}
   ["/user" {:get (fn [request] (ok (-> request :session :identity)))}]]])
```

In the above example, the `/login` route does not require authentication. Meanwhile, the routes defined within the `/api`
context will only be accessible when a user is present in the session.
