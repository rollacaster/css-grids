^{:nextjournal.clerk/visibility {:code :hide}}
(ns tech.thomas-sojka.css-grid.core
  (:require [nextjournal.clerk :as clerk]
            [nextjournal.clerk.viewer :as v]
            [clojure.string :as str]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn box
  ([content] (box {} content))
  ([{:keys [style class]} content]
   [:div.bg-gray-400.text-center.text-white.text-2xl.sans-serif.font-bold.flex.items-center.justify-center
    {:style style :class class :contenteditable "true"} content]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn container [{:keys [class style]} & children]
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
(def page2-content
  [:<>
   [:DIV.mb-1.text-gray-800 "Page 2 of my Great side Project"]
   [:div.flex.gap-1.flex-wrap
    (repeat 20
            [:div.w-4.h-4.bg-gray-400])]])

; # Exploring CSS Grids

; I've been ignoring CSS Grids for a long time and programmed layouts happily in the past using CSS flexbox and and the nomal CSS flow layout. But CSS Grids always felt like a that could solve some pain paints I have with those layout methods easier. I never found the time so far to explore that. I've got some time now, so let's dive in.


; ## Motivation
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


^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md
  "But it doesn't take too long until the project expands and needs a second page. Quit often the second page has less content and the does not stick to the edge of the screen.")

 ^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/with-viewer {:transform-fn clerk/mark-presented
                     :render-fn '(fn [{:keys [main-content
                                             page2-content]}]
                                   (reagent.core/with-let [x (reagent.core/atom :page-2)]
                                     [:div.border
                                      {:style {:width 300 :height 195}}
                                      [:header.bg-gray-400.px-4.text-gray-100.flex.justify-between
                                       [:div "Header"]
                                       [:nav
                                        [:ul.flex.gap-1
                                         [:li [:button
                                               {:on-click #(reset! x :page-1)
                                                :class [(when (= @x :page-1) "underline")]}
                                               "Page 1"]]
                                         [:li [:button
                                               {:on-click #(reset! x :page-2)
                                                :class [(when (= @x :page-2) "underline")]}
                                               "Page 2"]]]]]
                                      [:main.p-4
                                       (condp = @x
                                         :page-1 main-content
                                         :page-2 page2-content)]
                                      [:footer.bg-gray-400.px-4.text-gray-100 "Footer"]]))}
   {:page2-content page2-content
    :main-content main-content})))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md
  "It's possible to solve that with flexbox but the solution is a bit eddgy")

 ^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/with-viewer {:transform-fn clerk/mark-presented
                     :render-fn '(fn [{:keys [main-content
                                             page2-content]}]
                                   (reagent.core/with-let [x (reagent.core/atom :page-2)]
                                     [:div.border.flex.flex-col.overflow-scroll
                                      {:style {:width 300 :height 195}}
                                      [:header.bg-gray-400.px-4.text-gray-100.flex.justify-between
                                       [:div "Header"]
                                       [:nav
                                        [:ul.flex.gap-1
                                         [:li [:button
                                               {:on-click #(reset! x :page-1)
                                                :class [(when (= @x :page-1) "underline")]}
                                               "Page 1"]]
                                         [:li [:button
                                               {:on-click #(reset! x :page-2)
                                                :class [(when (= @x :page-2) "underline")]}
                                               "Page 2"]]
                                         [:li [:button
                                               {:on-click #(reset! x :page-3)
                                                :class [(when (= @x :page-3) "underline")]}
                                               "Page 3"]]]]]
                                      [:main.p-4.flex-1
                                       (condp = @x
                                         :page-1 main-content
                                         :page-2 page2-content
                                         :page-3 [:<>
                                                  [:div.mb-1.text-gray-800 "Page 3 of my Great side Project"]
                                                  [:div.flex.gap-1.flex-wrap
                                                   (repeat 80
                                                           [:div.w-4.h-4.bg-gray-400])]])]
                                      [:footer.bg-gray-400.px-4.text-gray-100 "Footer"]]))}
   {:page2-content page2-content
    :main-content main-content})))


;; I loose some time googling how to spin up my initial layout. So I thought it's time to dig deeper into this topic to increase my understanding instead of copying some snippets until it worked.

; I wrote my first CSS at the time when Bootstrap became popular. I had no idea what float layouts where but I was able to create any layout using bootstrap which made me happy because CSS did not get in my way.

;; It didn't take much time to notice that centering text in divs is more complicated than one would expect. So the next step in my CSS journey was learning about Flexbox. Firstly I used it only to center things, later I learned enough to build any layouts using Flexbox.

; I purposely decided not to spend a lot of time with layout since Flexbox served my needs quite well and was well supported so even after CSS Grid became more popular I did not took the time to look into it especially since I worked with a project that needed to support IE back then.

; In hindsight I still think that was a good idea. I spend my past years mostly learning more about JavaScript and React and explored other languages and found the one that suits me best which is Clojure.

; But with every new side project I setup there's this itch that tells getting a new tool could help me to speed up my work a lot. I don't work on a project that needs to support Internet Explorer anymore, so I thought it's a good time to learn everything about CSS Grid and hope to find a new default.

; ## Who's using grid?

;; My secret hope was that grid will make a lot of my old layout knowledge obsolete. I spimply use grid all the time after learning about it thoroughly.

;; To check if that might be true I checked what most well-known product companies use to structure their layout and unfortunately the result was evenly distributed.

;; Here's a breakdown of the main layout used for a couple of highly popular product companies.

;; | Flex layout    | Normal Flow layout | Grid layout    |
;; |-------------- |------------------ |-------------- |
;; | Linear Client  | AirBnB Client      | Dropbox Client |
;; | Linear Landing | Apple Landing      | Spotify Client |
;; | Pitch Client   | Shopify Landing    | Slack Client   |
;; | Google Landing | Figma Landing      |                |
;; |                | Canva Landing      |                |


;; I did this analysis by checking how the main parts of a design (mostly header, content, footer) are laid out.

; ## How to build a layout with grid?

; ## Reflections on CSS Grid

;; What I like the most about grid is that a lot of the resulting layout can be deduced by checking the properties of the grid container. After reading how the rows and cols of your grid are distributed you can already form a mental model of the final layout result. That's much harder with flexbox you need to check each child if you want to deduce how the page is layed out. Since there are less places to check there are also less places to change if you want to update the layout.

;; Some things I did not like that much about grid. Is that there are too many ways to do the same thing. You can specify your grid by naming areas, using track numbers or naming your track numbers and all of these options result in additional properties you have to know and new additional syntax to name parts of your grid. The shear amount of docs on the mdn page gibes a clear hint that there is a lot of complexity. I'd rather have only one option to use Grid.

;; It might seem nice that you can choose your favorite approach to using Grid and stick to that while forgettign about all the other possible ways. But it might still happen you have to switch to a code base where a different set of Grid properties are commonly used so you'd have to relearn the properties again.

;; Since I am a big fan of TailwindCSS I was curios to learn which part of the Grid they implemented in their framework. The subset they choose is creating a grid and placing items in it by using numbered rows and columns. I'd guess it's a reasonable choice and think it's good that they stick to one possible approach.

;; Conclusion, so which layout technique to use now?!

;; Instead of an easy answer I guess I have to stick

; ## When to use flexbox?

; > If you are using flexbox and find yourself disabling some of the flexibility,
; you probably need to use CSS Grid Layout.



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
