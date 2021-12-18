## Meus bagulhos app
This is my first app using Kotlin. My motivation to create it was simply training and using what I've learned.
In this app it's possible to create tasks, edit and rearrange them.

![](https://i.imgur.com/XWlcWfs.gif)

#### Resources I used:
- ViewPager2
- Tabs
- RecyclerView
- SQLite
- Slider
- a custom Edit Text that limits the number of characters and lines
- Dialog and Alert Dialog
- Drag and drop feature
- Double click, long click and items selection

What took most of my time was the drag and drop feature. The reason for this is that the position of the task (and all the tasks
between it and the final position) had to be saved. At the same time, the "done list" doesn't get into account the position the task had before. This list organizes
the tasks just by the order they come. 

It looks simple now, but working with all this positioning was a bit confusing.
