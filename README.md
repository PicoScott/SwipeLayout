# SwipeLayout


# Installation

# Usage

## Attribute for the SwipeLayout

* __swipeOrientation__
    * SwipeOrientation control swipe direction. Possible values are:
        * horizaontal (default)
        * vertical

* __swipeHandler__
    * SwipeHandler is full name of class instance of [EndSwipeHandler](./swipe/src/main/java/com/scott/swipe/EndSwipeHandler.java). For example [SwipeSnapHandler](./swipe/src/main/java/com/scott/swipe/SwipeSnapHandler.java) is sticky to edge, when finger away from screen.

* __swipeStatusPosition__
    * The attribute is create to swipeHandler. 
        * float
        * dimen

## Attributes for the children of a SwipeLayout

* __layout_itemType__
    * These indicate the type of child view
        * none (default)
        * swipe_view
        * start_menu
        * end_menu

# License

Please see [LICENSE](./LICENSE.md)