(ns oshpd-scraper.t-core
  (:require [midje.sweet :refer [fact facts =>] :as m]
            
            [oshpd-scraper.core :refer :all]))

(facts "Testing utility functions"
  (fact "Create an URL from a string"
    (type (url "http://www.something.com")) => java.net.URL)
  (fact "Create an URL from a resource path"
    (type (resource "test/OSHPD - HID - Chargemasters.html")) => java.net.URL)
  (fact "Split and return hospital name and URL"
    (get-filename-and-url "http://www/something/hospitalname/file") => {:name "hospitalname/file"
                                                                    :url "http://www/something/hospitalname/file"}))

(facts "Testing enlive/html items"
  (let [html (fetch-url (resource "test/OSHPD - HID - Chargemasters.html"))
        links (get-links html)]
    (fact "there should be 1315 links in the sample HTML file"
      (count links) => 1315
      (count (filter-regex links #".*")) => 1315)
    (fact "there should be 423 links for CDM excel files"
      (count (filter-regex links excel-cdm-regex))))
  (let [html (fetch-url (resource "test/OSHPD - HID - Chargemasters - presearch.html"))]
    (fact "checking viewstate"
      (get-hidden-input html view-state-str) => "/wEPDwUJOTcxNDA2ODY0ZGRcB3ajZbOlwtAZ0vntVwrYuyJzzA==")
    (fact "checking eventvalidation"
      (get-hidden-input html event-validation-str) => "/wEWKgLYj6SDBwKfn8TZAwK68o+HAgLTgvOTCwLTgs/IBALTgtulDALTgvfMCgLTgsOpAgK4u/GwBQK4u83tDgK4u9nKBwK4u7WnDwK/2sr/DwKjhJ3JAQLDp8LRCwLpw+qZBgKln/PuCgKs+5bqDwKCyYzLDALdpqYlAtympiUC36amJQLepqYlAtmmpiUC2KamJQLbpqYlAuqmpiUC5aamJQLkpqYlAuempiUC5qamJQLhpqYlAuCmpiUC46amJQLypqYlAu2mpiUC7KamJQLvpqYlAu6mpiUC6aamJQLopqYlAuumpiUfUQJClcVJm2joJSf6CTQm3dZmfQ==")))
