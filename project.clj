(defproject oshpd-scraper "0.1.0-SNAPSHOT"
  :description "Quick tool for scraping the OSHPD website for data files"
  :url "http://www.github.com/andrew-nguyen/oshpd-scraper"
  :license {:name "Affero General Public License"
            :url "http://www.gnu.org/licenses/agpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                
                 [clj-http "0.9.0"] 
                 [enlive "1.1.5"]
                 [midje "1.6.2"]
                 ]
  :main oshpd-scraper.core)
