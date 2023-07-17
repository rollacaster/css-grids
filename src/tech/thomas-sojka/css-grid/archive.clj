(ns tech.thomas-sojka.css-grid.archive
  (:require [nextjournal.clerk :as clerk]
            [tech.thomas-sojka.css-grid.util :as util]
            [clojure.string :as str]
            [hiccup2.core :as h]))

; # Archive

; ## The plan

; I decided to take a more structured approach to explore the topic. Firstly, I learned the basics from the MDN (Mozilla Developer Network). Then, I built several layouts to apply my newfound knowledge in practice. Finally, I examined how other websites were using the concept to gain insights about its usage.

;; It might seem nice that you can choose your favorite approach to using Grid and stick to that while forgettign about all the other possible ways. But it might still happen you have to switch to a code base where a different set of Grid properties are commonly used so you'd have to relearn the properties again.

;; Since I am a big fan of TailwindCSS I was curios to learn which part of the Grid they implemented in their framework. The subset they choose is creating a grid and placing items in it by using numbered rows and columns. I'd guess it's a reasonable choice and think it's good that they stick to one possible approach.



; ## When to use flexbox?

; > If you are using flexbox and find yourself disabling some of the flexibility,
; you probably need to use CSS Grid Layout.

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn container [{:keys [class]} & children]
  [:div.overflow-auto.gap-1.border
   {:class (str/join " " [class "min-h-[100px]"])}
   children])

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def main-content
  [:<>
   [:div.mb-1.text-gray-800 "My Great side Project"]
   [:div.flex.gap-1.flex-wrap
    (repeat 40
            [:div.w-4.h-4.bg-gray-400])]])

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn box
  ([content] (box {} content))
  ([{:keys [style class]} content]
   [:div.bg-gray-400.text-center.text-white.text-2xl.sans-serif.font-bold.flex.items-center.justify-center
    {:style style :class class :contenteditable "true"} content]))

(clerk/html
 (container {:class "grid
                     grid-cols-[min-content_1fr_min-content]
                     grid-rows-[min-content_2fr_min-content]"}
  (box {:class "col-span-3"} "header")
  (box "side1")
  (box "content")
  (box "side2")
  (box {:class "col-span-3"} "footer")))

(clerk/html
 (container {:class "grid"}
  (box "header")
  (box "side1")
  (box "content")
  (box "side2")
  (box "footer")))

(clerk/html
 (container {:style {:display "grid"
                     :grid-template-areas "
                     \"header header header\"
                      \"side1 content side2\"
                      \"footer footer footer"
                     :grid-template-columns "min-content 1fr min-content"
                     :grid-template-rows "min-content 1fr min-content"}}
            (box {:style {:grid-area "header"}} "header")
            (box {:style {:grid-area "side1"}} "side1")
            (box {:style {:grid-area "content"}} "content")
            (box {:style {:grid-area "side2"}} "side2")
            (box {:style {:grid-area "footer"}} "footer")))

(clerk/html
 (container {:style {:display "grid"
                     :grid-template-areas "
                     \"header\"
                     \"side1 \"
                     \"content\"
                     \"side2\"
                     \"footer"}}
            (box {:style {:grid-area "header"}} "header")
            (box {:style {:grid-area "side1"}} "side1")
            (box {:style {:grid-area "content"}} "content")
            (box {:style {:grid-area "side2"}} "side2")
            (box {:style {:grid-area "footer"}} "footer")))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md
  "Whenever I start a new side project I trip over the same layout pain point. I start with my a prototype of the functionality I want to build.")

 (clerk/html
  [:div.border
   {:style {:width 300 :height 195}}
   [:main.p-4
    main-content]]))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md
  "After some time I a header and footer is added to provide structure")

 (clerk/html
  [:div.border
   {:style {:width 300 :height 195}}
   [:header.bg-gray-400.px-4.text-gray-100 "Header"]
   [:main.p-4 main-content]
   [:footer.bg-gray-400.px-4.text-gray-100 "Footer"]]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn box2 [content]
  [:div.border.bg-gray-400.text-center.text-white.flex.items-center.justify-center
   {:class "w-[70px] h-[70px]"}
   content])

(clerk/html
 [:div.flex
  [:div.grid.grid-cols-2.grid-rows-2
   (box2 "Box1")
   (box2 "Box2")
   [:div.border.bg-gray-400.text-center.text-white.flex.items-center.justify-center.col-span-2
    {:class "h-[70px]"}
    "Box3"]]])
