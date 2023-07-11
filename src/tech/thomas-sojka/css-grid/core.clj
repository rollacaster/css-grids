^{:nextjournal.clerk/visibility {:code :hide}}
(ns tech.thomas-sojka.css-grid.core
  (:require [nextjournal.clerk :as clerk]
            [tech.thomas-sojka.css-grid.util :as util]
            [hiccup2.core :as h]))

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
(def html-string
  (clerk/update-val #(clerk/code (str (h/html (util/remove-class-styling %))))))

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

; # Exploring CSS Grids

; My approach to build CSS layouts in the past was using [Normal Flow](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Normal_Flow) and [Flexbox](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Flexbox). However, I secretly hoped that on day [Grids](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Grids) would revolutionize that make much of my CSS knowledge irrelevant.

; I took some time for a deep dive into Grids and realized that it is not the universal solution I was hoping for. Nonetheless, it can be valuable addition to your CSS-Toolbox and I'd like to share where I think it fits in.

; ## Motivation
; I wrote my first CSS when Bootstrap gained popularity. I had no idea what float layouts were but I was able to create any layout using bootstrap which made me happy because CSS did not get in my way of buildings websites.

; It didn't take long for me to realize that centering text in divs was more complex than expected. So, the next step in my CSS journey was to learn about Flexbox. Initially, solely for centering elements, but later I learned how to build any layout using Flexbox.

; I deliberately chose not to invest much time in Grid, even as its popularity grew. Flexbox served my needs quite well and was supported in IE, which is thankfully less important nowadays.

; However, I always had the itch to learn Grid and hope that Grid could become my new go-to technique for layout design.

; ## The basics of grid

; If you haven't used grid yet, let me introduce it to you by building a simple layout that looks like that:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/image "images/basic-layout.png")

;; Before we write the CSS rules for the layout, let's build up a mental model how CSS Grid works:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md "When you specify an HTML element as a **grid container** all its children become interleaved with grid lines. However, this does not make any visible difference in the rendered result. For instance, if we render three boxes in a **grid container**, they will be arranged vertically, as if no **grid container** were present.")
 (clerk/image "images/boxes.png"))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md "But under the hood, the grid adds four horizontal and two vertical grid lines between each box. The key capability provided by Grid is the ability to specify the formation of these grid lines and determine the placement of each box within them.")
 (clerk/image "images/boxes-with-grid-lines.png"))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md "If we specify a 2x2 grid, the three boxes will be arranged within the existing cells, resulting in the last cell being left empty.")
 (clerk/image "images/boxes-in-grid.png"))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/md "It is possible to specify the number of cells an element spans within a grid, allowing for the creation of any desired layout.")
 (clerk/image "images/boxes-in-grid-with-stretch.png"))

;; To summarize, when constructing a new layout using Grid, you begin by specifying a grid that accommodates your design. From there, you define the position and size of each element within this grid.

; ### Using the grid with line-based placement



;; Now that we have an understanding of how it works, let's put it into practice. Building upon our recent knowledge, we will specify the following CSS rules:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/with-viewer css-viewer
  line-based-styles)

;; To use the Grid, we include a CSS ruleset called `container` with a display property set to `grid`. By using `grid-template-columns` and `grid-template-rows`, you define a 2x2 layout. Here, the first column is set to `auto` so it is as wide as its content, while the second column takes up the remaining space by using `1fr` (fractional unit). The rows follow the approach but in reverse. The first row expands to fill all the remaining space, while the second row adjusts its height based on its content.

;; To ensure that the footer spans across both columns at the bottom, we add a second rule set that defines `grid-column-start` as `1` and `grid-column-end` as `3`. Those numbers specify the start and end grid line of the footer. By applying this class to an HTML element, it will occupy a larger portion of the grid.

;; Here's the HTML structure to use the CSS classes described below:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/with-viewer prettier-viewer
   line-based-example)

;; And here the final results of rendering that (with some additional classed added for minimal styling). Every box is positioned where it should be using Grid.

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html
 line-based-example)

; ## Is CSS Grid the new default?

;; I had a secret hope that Grid would render much of my previous layout knowledge obsolete. To check if this might be true, I did a quick research on the layout techniques used by well-known product companies. Not all pages are created equal, so I decided to distinguish between landing pages and client apps developed by each company.

; Here's a breakdown of some of the primary layouts. For the analysis, I checked the layout technique used for main design components, which were most commonly the header, content, and footer sections.


 ^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/table
 [["Normal Flow layout" "Flex layout" "Grid layout"]
  [(clerk/caption "AirBnB Client"
    (clerk/image "images/airbnb-app.png"))
   (clerk/caption "Linear Client"
    (clerk/image "images/linear-app.png"))
   (clerk/caption "Dropbox Client"
    (clerk/image "images/dropbox-app.png"))]
  [(clerk/caption "Figma Landing"
    (clerk/image "images/figma-Landing.png"))
   (clerk/caption "Linear Landing"
    (clerk/image "images/linear-landing.png"))
   (clerk/caption "Spotify Client"
    (clerk/image "images/Spotify-Client.png"))]
  [(clerk/caption "Canva Landing"
    (clerk/image "images/canva-Landing.png"))
   (clerk/caption "Pitch Client"
    (clerk/image "images/pitch-client.png"))
   (clerk/caption "Slack Client"
    (clerk/image "images/slack-client.png"))]
  [(clerk/caption "Pitch Landing"
    (clerk/image "images/pitch-landing.png"))
   (clerk/caption "Google Landing"
    (clerk/image "images/google-landing.png"))
   (clerk/caption "Canva Client"
    (clerk/image "images/canva-client.png"))]])

;; Upon reviewing the pages, I couldn't identify a clear trend in the layout techniques used. The distribution of used techniques seemed relatively evenly spread. However, one notable observation was that most landing pages were commonly built using the normal flow layout. This makes a lot of sense because landing pages should guide users through a coherent sequence of information, prioritizing a linear flow over interactivity. On the other hand, the Grid layout was predominantly used in more complex client applications which provide several interactive capabilities to the user. Flexbox remained a viable option that many websites opted for as main layout technique and it's additionally almost always used to layout smaller parts of the layout.

;; ## My evaluation of the Grid
;; ### What's great about the Grid

; To me, the mental model of buildings layouts with CSS Grid feels intuitive. One advantage is that it allows for the creation of complex layouts using simpler markup compared to flexbox. If I were to build the example layout we built earlier using flexbox, I would need to introduce an additional `<div>` to wrap both the sidebar and content sections. As the layout becomes more complex, using flexbox would require an increasing number of additional boxes.

; A consequence of having fewer boxes is that there are also fewer places in the markup that define the layout. This means you have less places in the code to read to understand how the layout is constructed. In contrast, with flexbox, understanding the page layout requires checking each child element individually.


;; ## What's not so great about Grid
; When I began reading the Grid guide on MDN, I immediately noticed its extensive length. While comprehensive documentation is valuable, I couldn't help but wonder if the features of Grid are somewhat bloated compared to other layout techniques

;- Grid: 29154 words
;- Flow: 4842 words
;- Flex: 15997 words

; Grid allows to specify the same layouts with multiple different notations. I omited all the various options in my explanation of the basics to keep the introduction slim. But there are many different ways to specify the grid. Some things I ommitted:

; - Name each grid track instead of using the numbers we used (LINK)
; - Several attributes to write less CSS rules (LINK)
; - Give each element of your layout a name and place it via ASCII Art (link + better explanation)
; - Define the behaviour of elements which exceed your specified grid (link)

; Personally I'd prefer to have less choice, as long as it gets the job done. Even if I decide to restrict myself to a small subset of Grid that I regularly use, I might still get into a codebase which uses a different subset which will force me to learn all possible flavors of Grid in the long run.

; ## Conclusion
;; So which layout technique to use now? Instead of an easy answer I guess I have to pick the tool depending on my needs:

; So, which layout technique should you choose? Unfortunately there is no simple answer, you have to consider your specific needs:

; - If you build up landing page explaining one argument at a time -> use Normal Flow
; - If you know all the elements in advance that you want to position in a complex layout -> use Grid
; - If you layout a dynamic number of elements -> use Flex

; Note: Those rules will have cases and might change a lot the more I use Grid. For instance, although it's possible to use Grid to style a dynamic number of elements, personally, I find Flexbox more intuitive in such cases.

; If you made it that far, thanks for following my journey to learn about Grid, I hope you could it adds one more technique to your CSS-Toolbelt just as it did for me.
