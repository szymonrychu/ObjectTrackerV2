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

## Linux
### Download and installation 
We need:
* Java JDK 1.6.
Intallation:
```sh
sudo su
add-apt-repository ppa:webupd8team/java
apt-get update
apt-get install oracle-java8-installer
```

* Latest Eclipse downloaded from official [Eclipse Download Site](https://www.eclipse.org/downloads/) -download Eclipse IDE for C/C++ Developers

* Android ADT Bundle from official [download site](https://developer.android.com/sdk/installing/index.html). Get Stand-alone SDK Tools.

* Android NDK from [it's site](https://developer.android.com/tools/sdk/ndk/index.html)

* OpenCV4Android -go to [this site](http://sourceforge.net/projects/opencvlibrary/files/opencv-android/).

1. Create dir, that will hold your entire env:
```sh
mkdir ~/android-sdk
```
2. Unzip Eclipse, ADT and NDK to that dir:
```sh
cd ~/android-sdk
tar -xzvf ~/Downloads/eclipse-standard-luna-R-linux-gtk-x86_64.tar.gz
tar -xzvf ~/Downloads/android-sdk_r23.0.2-linux.tgz
unzip ~/Downloads/OpenCV-2.4.9-android-sdk.zip
tar -xjvf ~/Downloads/android-ndk32-r10-linux-x86_64.tar.bz2
```

3. Prepare default workspace:
```sh
mkdir ~/android-sdk/android-workspace
```

4. Run eclipse in newly created workspace to install the rest of the plugins:
```sh
~/android-sdk/eclipse/eclipse -data ~/android-sdk/android-workspace
```

5. Install rest from the required packages. As far as I know, the best tutorial to do this is published [here](http://developer.android.com/sdk/installing/installing-adt.html).

6. Prepare simple launcher script:
```sh
touch ~/android-sdk/launcher.sh
chmod +x ~/android-sdk/launcher.sh
gedit ~/android-sdk/launcher.sh
```
and paste to the file following code:
```sh
#!/bin/bash
# user can set this path:
# 
export ANDROID_HOME=`pwd`
# rest of the script:
#
echo "paths"
export WORKSPACE=$ANDROID_HOME/android-workspace
export SDK_HOME=$ANDROID_HOME/android-sdk-linux
export NDKROOT=$ANDROID_HOME/android-ndk-r9d
export OPENCV_HOME=$ANDROID_HOME/OpenCV-2.4.9-android
export PATH=$PATH:$ANDROID_HOME/eclipse:$SDK_HOME/platform-tools

# reconfiguring eclipse to use our workspace
#
echo "android sdk"
ADT_CONF_DIR=$WORKSPACE/.metadata/.plugins/org.eclipse.core.runtime/.settings/com.android.ide.eclipse.adt.prefs
ADT_CONFIG=`cat $ADT_CONF_DIR`
echo "" > $ADT_CONF_DIR
while read LINE; do
	if [[ $LINE == com.android.ide.eclipse.adt.sdk=* ]]; then
		echo "com.android.ide.eclipse.adt.sdk=$SDK_HOME" >> $ADT_CONF_DIR
	else
		echo $LINE >> $ADT_CONF_DIR
	fi
done <<< "$ADT_CONFIG"
# run eclipse in background
#
echo "workspace"
$ANDROID_HOME/eclipse/eclipse -data $WORKSPACE > $ANDROID_HOME/log.txt 2>&1 &
# run our shell
#
```
7. Configure project's internal paths.
Run previously created script:
```sh
~/android-sdk/launcher.sh
```
Point eclipse to previously unpacked Android NDK- go to the:
 Window -> Preferences -> Android -> NDK
and browse to unpacked package (~/android-sdk/android-ndk-r9d/).


And that should be it. I wrote it from my memory, so I could made some bugs- I will be happy to answer to any issues submitted on github, or by emails.

### Import project to build env:

1. Download data from github:
```sh
git clone https://github.com/szymonrychu/ObjectTrackerV2.git ~/android-sdk/git/ObjectTrackerV2/
```

2. Import the project ot Eclipse:
run ~/android-sdk/launcher.sh, then go to the:
 File -> Import -> Existing Projects into Workspace

and browse to ~/android-sdk/git/ObjectTrackerV2/
also import everything from ~/android-sdk/OpenCV-2.4.9-android/


 
# Known Bugs

* Application don't seem to work right with latest CyanogenMod nightlies on Nexus5. App crashes when tries to reload camera config. 




