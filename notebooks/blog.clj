^{:nextjournal.clerk/visibility {:code :hide}}
(ns tech.thomas-sojka.css-grid.core
  (:require [clojure.string :as str]
            [hiccup2.core :as h]
            [nextjournal.clerk :as clerk]
            [clojure.java.io :as io]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn box
  ([content] (box {} content))
  ([{:keys [style class]} content]
   [:div.bg-gray-400.text-center.text-white.text-2xl.sans-serif.font-bold.flex.items-center.justify-center
    {:style style :class class :contenteditable "true"} content]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn container2 [{:keys [style class]} & children]
  (into
   [:div.border
    {:class (str "gap-[2px] w-[300px] h-[195px] " class)
     :style style}]
   children))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def aside
  [:aside.bg-gray-400.py-4.px-2.text-gray-100 "Sidebar"])

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def main-content2
  [:main.p-4
   [:div.mb-1.text-gray-800 "Page Title"]
   [:section.text-sm "Lorem ipsum dolor sit amet, consectetuer adipiscing elit."] ])

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn footer [{:keys [style class]}]
  [:footer.text-center.bg-gray-400.px-4.text-gray-100
   {:style style :class class}
   "Footer"])

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn branch? [[_ & forms]]
     (or (vector? (first forms))
         (vector? (second forms))))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn children [[_ & forms]]
  (if (vector? (first forms))
    forms
    (rest forms)))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
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

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
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

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn remove-class-styling [hiccup]
  (walk-hiccup
   remove-class-styling-from-node
   hiccup))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def html-string
  (clerk/update-val #(clerk/code (str (h/html (remove-class-styling %))))))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def prettier-viewer
  {:transform-fn html-string
   :render-fn '(fn [value]
                 (defn loadJS [file-url]
                   (let [scriptEle (.createElement js/document "script")]
                     (.setAttribute scriptEle "src" file-url)
                     (.setAttribute scriptEle "type" "text/javascript")
                     (.appendChild (.-body js/document) scriptEle)
                     (.addEventListener
                      scriptEle
                      "load"
                      (fn []  (.highlightAll js/window.hljs)))
                     (.addEventListener
                      scriptEle
                      "error"
                      (fn [ev] (.log js/console "Error on loading file" ev)))))
                 (when value
                   [nextjournal.clerk.render/with-d3-require {:package ["prettier@2.8.8/parser-babel.js"

                                                                        "prettier@2.8.8/standalone.js"]}
                    (fn [prettier]
                      (loadJS "https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.8.0/build/highlight.min.js" )
                      [:<>
                       [:link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/default.min.css"}]
                       [:pre.grid
                        [:code
                         (str
                          (.format prettier value
                                   (clj->js {:parser "babel"
                                             :plugins [prettier]})))]]])]))})

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def css-viewer
  {:transform-fn clerk/mark-presented
   :render-fn '(fn [value]
                 (defn loadJS [file-url]
                   (let [scriptEle (.createElement js/document "script")]
                     (.setAttribute scriptEle "src" file-url)
                     (.setAttribute scriptEle "type" "text/javascript")
                     (.appendChild (.-body js/document) scriptEle)
                     (.addEventListener
                      scriptEle
                      "load"
                      (fn [] (.highlightAll js/window.hljs)))
                     (.addEventListener
                      scriptEle
                      "error"
                      (fn [ev] (.log js/console "Error on loading file" ev)))))

                 (when value
                   [nextjournal.clerk.render/with-d3-require {:package ["prettier@2.8.8/parser-postcss.js"
                                                                        "prettier@2.8.8/standalone.js"]}
                    (fn [prettier]

                      (loadJS "https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.8.0/build/highlight.min.js" )
                      [:<>
                       [:link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/default.min.css"}]
                       [:pre.not-prose
                        [:code
                         (.format prettier value
                                  (clj->js {:parser "css"
                                            :plugins [prettier]}))]]])]))})

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def line-based-styles
  ".container {
     display: grid;
     grid-template-columns: auto 1fr;
     grid-template-rows: 1fr auto;
   }

   .footer {
     grid-column-start: 1;
     grid-column-end: 3;
   }")

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:style line-based-styles])

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def line-based-example
  (container2
   {:class "container"}
   aside
   main-content2
   (footer {:class "footer"})))

; # Exploring the Power of CSS Grid

; In the past, my approach to building CSS layouts was using [Normal Flow](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Normal_Flow) and [Flexbox](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Flexbox). However, I secretly hoped that one-day [Grid](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Grids) would revolutionize that and make much of my CSS knowledge obsolete.

; I took time to dive into Grid and realized it is not the universal solution I hoped for. Nonetheless, it can be a valuable addition to your CSS Toolbox. Now, I'd like to share where I think it fits in.

; ## Motivation
; I wrote my first CSS when [Bootstrap](https://getbootstrap.com/) gained popularity. Back then,  Bootstrap used float layouts and I had no clue about them. Still, I could create any layout using Bootstrap, which made me happy. CSS did not get in my way of buildings websites.

; Later, I realized that centering text in divs was more complex than expected. So, the next step in my CSS journey was to learn about Flexbox. Initially, solely for centering elements, but later I learned how to build any layout using Flexbox.

; Then, Grid gained popularity. I deliberately chose not to invest much time in it. Flexbox served my needs well and supports Internet Explorer, which is thankfully less crucial nowadays.

; However, I was always curious about Grid. I wanted to explore it as my new go-to technique for layout design. Now, I took the time to dive into Grid. I read all the guides and tutorials I could get and added the Grid to my CSS Toolbox.

; This blog post is not a thorough introduction to all the aspects of Grid. Instead, it will provide you with the basics of Grid and help you understand when to use it. You can go deeper into the rabbit hole of Grid if it feels worthwhile to you afterward.

; ## The basics of Grid

; If you haven't used Grid yet, let me introduce it to you. We're building a simple layout that looks like that:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/image (io/resource "images/basic-layout.png"))

;; Before we write the CSS rules for the layout, let's build up a mental model of how CSS Grid works:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md "When you specify an HTML element as a **grid container**, all its children become interleaved with **grid lines**. However, this does not make any visible difference in the rendered result. For instance, if we render three boxes in a **grid container**, they will be placed vertically. The same happens if no **grid container** is present.")
 (clerk/image (io/resource "images/boxes.png")))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md "But under the hood, Grid adds four horizontal and two vertical **grid lines** between each box.")
 (clerk/image (io/resource "images/boxes-with-grid-lines.png")))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md " Grid allows to specify the formation of these **grid lines** and determine the placement of each box within them. If we specify a 2x2 grid, each of the three boxes occupies a **grid cell**. The last **grid cell** remains empty.")
 (clerk/image (io/resource "images/boxes-in-grid.png")))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md "We can specify the number of **grid cells** each can element can span within a grid. This way we can expand Box3 to span both of the remaining **grid cells**.")
 (clerk/image (io/resource "images/boxes-in-grid-with-stretch.png")))

;; To summarize, when constructing a new layout using Grid, you begin by specifying a grid that accommodates your design. From there, you define the position and size of each element within this grid.

; ### Using the Grid with line-based placement
;; Now that we have the mental model. Let's put it into practice. Building upon our recent knowledge, we will specify the following CSS rules:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/with-viewer css-viewer
  line-based-styles)

;; We include a CSS ruleset called `container` with a display property set to `grid` to activate the Grid for our HTML-Element. By using `grid-template-columns` and `grid-template-rows`, you define a 2x2 layout. Here, the first column is set to `auto` so it is as wide as its content. The second column takes up the remaining space by using `1fr` ([fractional unit](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout/Basic_concepts_of_grid_layout#the_fr_unit)). The rows follow the approach but in reverse. The first row expands to fill all the remaining space. The second row adjusts its height based on its content.

;; > **Note**
;; > Besides `auto` and the fractional unit, there are further keywords to create your desired grid. You can find all options in the reference pages of [`grid-template-rows`](https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template-rows#syntax) and [`grid-template-columns`](https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template-columns#syntax).

;; The footer should span across both columns at the bottom. We add a second rule set `footer` for that. It defines `grid-column-start` as `1` and `grid-column-end` as `3`. Those numbers specify the start and end grid line of the footer. We add this class to the footer element. Now, it will occupy two cells of the grid.

;; Here's the HTML structure to use the CSS classes described below:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/with-viewer prettier-viewer
   line-based-example)

;; And here are the final rendered results (with some additional classes added for minimal styling). Every box is where it should be, thanks to Grid.

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html
 line-based-example)

; ## Is CSS Grid the new default?

;; I hoped that Grid would make much of my previous layout knowledge obsolete. To check if my hope was justified, I researched the layout techniques used by well-known product companies. Not all pages are created equal, so I decided to distinguish between landing pages and client apps developed.

; Here's a breakdown of some of the primary layouts. I checked the layout technique used for the main design components. Focusing on the header, content, and footer sections. I checked if they use **Normal flow**, **Flexbox** or **Grid**.


 ^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/table
 [["Normal Flow layout" "Flex layout" "Grid layout"]
  [(clerk/caption "AirBnB Client"
                  (clerk/image (io/resource "images/airbnb-app.png")))
   (clerk/caption "Linear Client"
                  (clerk/image (io/resource "images/linear-app.png")))
   (clerk/caption "Dropbox Client"
                  (clerk/image (io/resource "images/dropbox-app.png")))]
  [(clerk/caption "Figma Landing"
                  (clerk/image (io/resource "images/figma-landing.png")))
   (clerk/caption "Linear Landing"
                  (clerk/image (io/resource "images/linear-landing.png")))
   (clerk/caption "Spotify Client"
                  (clerk/image (io/resource "images/Spotify-Client.png")))]
  [(clerk/caption "Canva Landing"
                  (clerk/image (io/resource "images/canva-landing.png")))
   (clerk/caption "Pitch Client"
                  (clerk/image (io/resource "images/pitch-client.png")))
   (clerk/caption "Slack Client"
                  (clerk/image (io/resource "images/slack-client.png")))]
  [(clerk/caption "Pitch Landing"
                  (clerk/image (io/resource "images/pitch-landing.png")))
   (clerk/caption "Google Landing"
                  (clerk/image (io/resource "images/google-landing.png")))
   (clerk/caption "Canva Client"
                  (clerk/image (io/resource "images/canva-client.png")))]])

;; Upon reviewing the pages, I couldn't see a clear trend for the most popular layout techniques. The distribution of layout techniques seemed evenly spread.

; However, one notable observation was that most landing pages use the Normal flow layout. The reason probably is that landing pages should guide users through a coherent sequence of information. They prioritize a linear flow over interactivity.

; On the other hand, complex client applications often use Grid layouts. They help to display multiple interactive capabilities to the user.

; Flexbox remains a popular choice as a layout technique. In addition, almost every page had sub-sections styled with Flexbox.


;; ## Personal Evaluation of the Grid
;; ### Advantages of the Grid

; The mental model of building layouts with CSS Grid feels intuitive to me. One advantage is that you can create complex layouts using simpler markup, compared to Flexbox. To build the example layout we discussed earlier using Flexbox, I would need to add an additional wrapper. A would need `<div>` to wrap the sidebar and content sections. As the layout becomes more complex, using Flexbox would require an increasing number of boxes.

; A consequence of having fewer boxes is that fewer places in the markup define the layout. You read fewer places in the code to understand how the layout works. In contrast with Flexbox, understanding the page layout requires checking each child element individually.


;; ### Critique of the Grid
; When I began reading the Grid guide on MDN, I immediately noticed its extensive length. Comprehensive documentation is valuable, I couldn't help but wonder if the features of Grid are somewhat bloated. Here's a comparison of the guide lengths with other layout techniques:
 ^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/vl {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
           :embed/opts {:actions false}
           :width 300
           :height 200
           :description "A simple bar chart with embedded data.",
           :config {:view {:stroke "transparent"}}
           :data {:values [{:technique "Grid", :word-count 29154}
                           {:technique "Flex", :word-count 15997}
                           {:technique "Flow", :word-count 4842} ]},
           :encoding {:x {:field "technique"
                          :type "nominal"
                          :axis {:labelFontSize 14}
                          :sort "-y"}
                      :y {:field "word-count"
                          :type "quantitative"}
                      :color {:field "word-count"
                              :title "Word count of MDN Guide"}}
           :layer [{:mark {:type "bar"},
                    :encoding {:x {:field "technique", :type "nominal"
                                   :axis {:labelAngle 0
                                          :titleFontSize 16}

                                   :title "Layout technique"},
                               :y {:field "word-count", :type "quantitative"
                                   :axis nil
                                   :title "Word count of the MDN Guide"}}}
                   {:mark {:type "text"
                           :fontSize 18
                           :dy -10}
                    :encoding {:text {:field "word-count"
                                      :type "quantitative"}
                               }}]})

; Grid allows to specify the same layouts with multiple different notations. I omitted many features in the basic explanation above to keep the introduction short. Here are a few of them:

; - [Name each grid track instead of using the numbers we used](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout/Grid_layout_using_named_grid_lines)
; - [There are several attributes to shorten the CSS](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout/Grid_layout_using_line-based_placement#the_grid-column_and_grid-row_shorthands)
; - [Placing elements with grid areas](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout/Grid_template_areas)
; - [Define the behavior of elements that exceed your specified grid](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout/Auto-placement_in_grid_layout)

; I prefer to have fewer choices. Only one technique to use Grid would be fine for me. Even if I restrict myself to a small subset of Grid I regularly use. I might still get into a codebase that uses a different subset. That forces me to learn all possible flavors of Grid in the long run.

; ## Conclusion
; So, which layout technique should you choose? Unfortunately, there is no simple answer. You have to consider your specific needs:

 ^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/image (io/resource "images/layout-flow.png"))

; > **Note**
; > Those rules will have edge cases and might change the more I use Grid. For instance, Grid can style a dynamic number of elements, but I find Flexbox more intuitive here.

; You made it to the end. Thanks for following my journey to learn about Grid. I hope you found this journey valuable and that you can add CSS Grid to your CSS Toolbox.
