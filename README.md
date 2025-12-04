Infilect – Android Assignment

This project was made for the Android Engineer Intern assignment at Infilect.
It’s a small demo app that uses the device camera to detect objects in real-time and shows a tick mark on each detected item. I’ve used CameraX for the camera preview and ML Kit’s object detection API for the detections.

What the app does:

Opens the camera as soon as the app starts
Detects objects on the screen in real time
Calculates the center point of each detected object
Draws a green tick on that position
Uses tracking IDs so the same object is not detected again when the camera comes back to it

Tech used

Kotlin
CameraX
ML Kit Object Detection (stream mode)
Custom OverlayView for drawing ticks

How it works (short explanation):

ML Kit gives bounding boxes for visible objects
I take the center point of each box
The points are mapped to the overlay’s size
Ticks are drawn through a custom Canvas view
A map of tracking IDs is used to avoid duplicates

How to run:
Just open the project in Android Studio and run it on a physical device (camera permission is needed).
