(ns tech.thomas-sojka.css-grid.util
  (:require [clojure.string :as str]))

(defn branch? [[_ & forms]]
     (or (vector? (first forms))
         (vector? (second forms))))

(defn children [[_ & forms]]
  (if (vector? (first forms))
    forms
    (rest forms)))

(defn walk-hiccup [f root]
  (if (branch? root)
    (let [[symbol first-form] root]
      (f
       (if (map? first-form)
         (into [symbol first-form]
               (for [child (children root)]
                 (walk-hiccup f child)))
         (into [symbol]
               (for [child (children root)]
                 (walk-hiccup f child))))))
    (f root)))

(defn remove-class-styling-from-node [[symbol first-from & rest-forms]]
  (into [(keyword (first (str/split (name symbol) #"\.")))]
        (cons (if (map? first-from)
                (cond
                  (str/includes? (:class first-from) "container")
                  (assoc first-from :class "container")

                  (str/includes? (:class first-from) "footer")
                  (assoc first-from :class "footer")

                  :else
                  (dissoc first-from :class))
                first-from)
              rest-forms)))

(defn remove-class-styling [hiccup]
  (walk-hiccup
   remove-class-styling-from-node
   hiccup))
