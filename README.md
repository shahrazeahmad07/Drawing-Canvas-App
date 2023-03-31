# Drawing-Canvas-App

This application has custom drawing widget. I have made the drawing widget with canvas.

The user can draw color, change brush size, change color, erase your drawing, clear screen, undo last action, redo last action, add background from the gallery and save your drawing in the gallery!!!

to access media from the gallery, we need read access for devices upto API 28 i.e., Android 9
to save image in gallery, we need write access for devices upto API 28 i.e., Android 9

for devices running Android 10 and plus, API 29 and plus, to deal with scoped-storage in android, a new concept, we use content resolver to save image in gallery!

![ezgif-4-New](https://user-images.githubusercontent.com/68849516/228350325-155f9c4b-b82e-4919-b921-04b49ab92834.gif)

![Screenshot_2023-03-31-04-23-57-90_f0899220bf1c3753e8998d1cc01182d2](https://user-images.githubusercontent.com/68849516/228986633-f30209ca-ce5b-4d8f-ba33-7a285a59dcc6.jpg)

![Screenshot_2023-03-31-04-24-01-11_f0899220bf1c3753e8998d1cc01182d2](https://user-images.githubusercontent.com/68849516/228986651-9260cd80-681d-4638-a855-401a97f698ea.jpg)


if you have any question feel free to ask.

# Approach:
We just save all paths drawn by the user in an array and to undo, just pop the last saved path from originalPaths list to undoPathlist and call the method invalidate()
