ObjectTrackerV2
===============

Repository with newest implementation of old and obsolete ObjectTracker project. 



# Features:
## Consistent with oldest ObjectTracker:
List of features shared between ObjectTracker and ObjectTrackerV2

* Ability to recognize tags, draw outline presenting their position on the phone screen,
* Usage of native libraries running beneath Android application (JNI),
* Simple serial connection alghoritm used to communicate with ArduinoUNO

## Newest -only in ObjectTrackerV2
List of features unique only for ObjectTrackerV2

* A lot of code optimizations- both in Java and C/C++
* Replaced Java implementation of Camera Calibration with native one,
* Changed way of storing Camera Matrixes ,
* Replaced one native library with several smaller ones,
* Added way of storing previous camera resolutions (only for Recognizer Activity for now),
* Added ability to recognize and track tags in portrait and reversed landscape screen modes,
* A lot of code cleanup,

## Under Preparation:
List of features, which are close to be working in newest release

* Activity used for manual driving the platform with multitouch controller enabling user to test communication with platform,
* View designed to present OpenGl ES 2.0 and higher content over camera preview,
* Alghoritm of generating tags and then storing them as pictures in phone,

## Not yet prepared:
List of features which were planned to be implemented in final release

* automatically track and follow tagged objects,
* rendering results of tracking in OpenGL ES 2.0 or higher,
* Communication with mobile platform,

# How to prepare build env

## Placeholder

# Known Bugs

## Placeholder




