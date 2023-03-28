# Drawing-Canvas-App

This application has custom drawing widget. I have made the drawing widget with canvas.

The user can draw color, change brush size, change color, undo last action, redo last action, erase your drawing, add background from the gallery and save your drawing in the gallery!!!

to access media from the gallery, we need read access for devices upto API 28 i.e., Android 9
to save image in gallery, we need write access for devices upto API 28 i.e., Android 9

for devices running Android 10 and plus, API 29 and plus, to deal with scoped-storage in android, a new concept, we use content resolver to save image in gallery!

![ezgif-4-New](https://user-images.githubusercontent.com/68849516/228350325-155f9c4b-b82e-4919-b921-04b49ab92834.gif)


if you have any question feel free to ask.


I have created two types of drawing widget/paint widget, both can be downloaded from the release menu on GitHub repository of this project.

1st (later will be refered as App 1) directly draws over the canvas when the user tend to draw, this drawing on canvas is permanent and can't be undone by undoing your action. rather you have to store bitmaps after each action and then if the user press undo, you simply just pop the 2nd last bitmap (last will be deleted na or removed to show the undo response) and then pass it to the new canvas object and then again called invalidate() to show the changes. This version has all features, specially that of eraser and undo redo. (but undo redo has an issue which will be discussed later in the text).

2nd (later will be referred as App 2) stores the paths which the user has moved finger through, and then draw color in those paths. and for undo and redo, simply a path from one list of current action is moved to undo Path List and onDraw method is recalled again using invalidate(). this version doesn't draws directly on the canvas. this one doesn't have eraser feature because of an encountered issue wich I list later.

the reason why I am explaining all this because of the two issue tha I encountered with each type of paint widget.

# Issues:
1. App 1 has an issue in undo redo function. When an action is performed and then undo redo is tested, its working fine but when I draw a line and then undo it and then again draw a new line and then try to undo it, it first doesn't do anything but then when pressed again, removes both the current line and the previous line together.

2. App 2 has a problem in implementing erasor, eraser is working but when I try to erase it starts drawing black color line
