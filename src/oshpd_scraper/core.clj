(ns oshpd-scraper.core
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html])
  (:gen-class))

(defn url
  [s]
  (java.net.URL. s))

(defn resource
  [resource-path]
  (io/resource resource-path))

(defn fetch-url
  [url]
  (html/html-resource url))

(defn get-links
  [html]
  (html/select html [:a]))

(def excel-cdm-regex #".*CDM.*xls[x]?")

(defn filter-regex
  [html regex]
  (filter #(re-matches regex (get-in % [:attrs :href])) html))

(defn get-urls
  [html]
  (map #(get-in % [:attrs :href]) html))

(defn get-hidden-input
  [html input-name]
  (get-in (first (html/select html [[:input (html/attr= :name input-name)]])) [:attrs :value]))

(def base-url "http://www.oshpd.ca.gov")
(def form-url "/Chargemaster/default.aspx")
(def view-state-str "__VIEWSTATE")
(def event-validation-str "__EVENTVALIDATION")

(defn search-chargemaster
  [& {:keys [hospital-name year]
      :or {hospital-name ""
           year "All Years"}}]
  (let [html (fetch-url (url (str base-url form-url)))
        view-state (get-hidden-input html view-state-str)
        event-validation (get-hidden-input html event-validation-str)
    
        response (client/post (str base-url form-url)
                              {:form-params {event-validation-str event-validation
                                             view-state-str view-state
                                             :txtHospName hospital-name
                                             :txtID ""
                                             :btnSearch "Search"
                                             :DDLalpha ""
                                             :DDLYear year
                                             :DDLSort ""
                                             }}
                              )
        body (:body response)
        ]
    ;body
    (html/html-snippet body)
    ))

(defn get-filename-and-url
  [url]
  (let [splits (clojure.string/split url #"/")
        num-splits (count splits)
        hosp-name (nth splits (- num-splits 2))
        file-name (nth splits (- num-splits 1))]
    {:name (str hosp-name "/" file-name) :url url}))

(defn -main
  [& {:keys [hospital-name year]
      :or {hospital-name ""
           year "All Years"}}]
  (prn (str "Searching for Hospital Name: " (if (> (count hospital-name) 0) hospital-name "All")))
  (prn (str "Searching for Year:          " year))
  (let [urls (get-urls (filter-regex (get-links (search-chargemaster :year 2013)) excel-cdm-regex))]
    (doseq [file-url urls]
      (let [url (str base-url file-url)
            m (get-filename-and-url url)
            output-file (str "output/" (:name m))
            ]
        (prn (str "Fetching URL: " (:name m)))
        (clojure.java.io/make-parents output-file)
        (spit output-file (client/get (:url m)))
        )
      )
    ))
