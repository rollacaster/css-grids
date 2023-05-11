^{:nextjournal.clerk/visibility {:code :hide}}
(ns tech.thomas-sojka.css-grid.core
  (:require [nextjournal.clerk :as clerk]))

; ## When to use flexbox?

; > If you are using flexbox and find yourself disabling some of the flexibility,
; you probably need to use CSS Grid Layout.

(defn box
  ([content] (box {} content))
  ([{:keys [style]} content]
   [:div.bg-gray-400.text-center.text-white.text-2xl.sans-serif.font-bold.border
    {:style style} content]))



(clerk/html [:div.flex.flex-wrap {:style {:width 300}}
             (box {:style {:flex "1 1 110px"}} "1")
             (box {:style {:flex "1 1 110px"}} "2")
             (box {:style {:flex "1 1 110px"}} "3")])

(clerk/html
 [:<>
  [:input {:type "range" :min 300 :max 500
           :value 350}]

  [:div.grid.grid-cols-3 {:style {:width 300}}
   (box "1")
   (box "2")
   (box "3")]])
