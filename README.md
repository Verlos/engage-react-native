# react-native-proximipro-engage

 Add beacon technology in your React Native application for both iOS and Android.
# Getting started
---
### If you are using `react-native` >= 0.60.0
## Automatic Way
---
``` 
yarn add react-native-proximipro-engage
```

or if you're using npm
``` 
npm install react-native-proximipro-engage --save
```
---
#### Important:
Linking is not needed anymore. ``react-native@0.60.0+`` supports dependencies auto linking.
For iOS you also need additional step to install auto linked Pods (Cocoapods should be installed), open Podfile and set 

```
platform: ios, '12.0'
```
then 
``` 
cd ios && pod install && cd ../
```
___
### If you are using `react-native` <= 0.59.10 
If you are having any problems with this library
After installing jetifier, runs a ```npx jetify -r``` and test if this works by running a ```react-native run-android```.
## Automatic Way
---
``` 
yarn add react-native-proximipro-engage
react-native link react-native-proximipro-engage
```

or if you're using npm
``` 
npm install react-native-proximipro-engage --save
react-native link react-native-proximipro-engage
```
---
We recommend using the releases from npm, however you can use the master branch if you need any feature that is not available on NPM. By doing this you will be able to use unreleased features, but the module may be less stable. 
**yarn**: 
``` 
npm install --save npm install --save bitbucket:simformteam/proximipro-engage-reactnative-sdk.git
```

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-proximipro-engage` and add `Engage.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libEngage.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

# iOS Install(using Pods)
You just need to add to your Podfile the react-native-proximipro-engage dependency.

```ruby
  # react-native-proximipro-engage pod
  pod 'react-native-proximipro-engage', :path => '../node_modules/react-native-proximipro-engage'
```

After that, just run a `pod install` or `pod udpate` to get up and running with react-native-proximipro-engage. 

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.EngagePackage;` to the imports at the top of the file
  - Add `new EngagePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-proximipro-engage'
  	project(':react-native-proximipro-engage').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-proximipro-engage/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-proximipro-engage')
  	```

## Additional Steps
#### iOS
1. Add one swift file for creating swift bridge for swift to objective-c compiler.
2. Set Deployment target 12.0
3. Click on project in Project navigator, Select target project, select Capabilities, turn on Background Modes and select following options:
	* Location updates
	* Uses Bluetooth LE accesories
	* Background fetch
	* Remote notification
4. Open up `AppDelegate.m` file and import

	```#import <CoreLocation/CoreLocation.h>```

	```#import "EngageLocationManager.h"```

	
	
	 and 

	Add following line into `didFinishLaunchingWithOptions` method

	```[EngageLocationManager config:[[CLLocationManager alloc] init]];```
4. For getting local notification

	Open up `AppDelegate.h` file and import 

	```objc
	#import <UserNotifications/UserNotifications.h>

	```

	and add `UNUserNotificationCenterDelegate`, looks like below

	```objc
	@interface AppDelegate : UIResponder <..., UNUserNotificationCenterDelegate>
	```

	and add two notification delegates into `AppDelegate.m` file
	
	```objc
	-(void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions options))completionHandler{
  		NSLog(@"User Info : %@",notification.request.content.userInfo);
  		completionHandler(UNAuthorizationOptionSound | UNAuthorizationOptionAlert | UNAuthorizationOptionBadge);
	}

	-(void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void(^)(void))completionHandler{
		NSLog(@"User Info : %@",response.notification.request.content.userInfo);[EngageLocationManager configNotification:response.notification.request.content.userInfo]completionHandler();
	}
	```



#### Android
1. Open up `android/build.gradle` 
	* change `minSdkVersion` to `21`
	* Add `maven { url 'https://jitpack.io’ }` into buildScript's respositories block and allproject's respositories block
2. Open up `android/app/build.gradle` and add `implementation "androidx.appcompat:appcompat:1.0.2"` into dependencies block
3. Open up `android/app/src/main/java/com/app/mainActivity.java` and add

```java
	@Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    EngageModule.onNotificationTapped(getReactNativeHost().getReactInstanceManager().getCurrentReactContext(), intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EngageModule.onNotificationTapped(getReactNativeHost().getReactInstanceManager().getCurrentReactContext(), getIntent());
  }
```

4. Add following permissions into `android/app/src/main/AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true”/>
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
```
## Usage
```js
import Engage from 'react-native-proximipro-engage';
```

## Methods

### initialize
The SDK needs to be initialized before one can use it. Please note that in order to use this SDK, it must be initialized before using it, otherwise it will throw SdkNotInitializedException. To initialize this sdk, following things are required:
* ProximiPro API Key: Provided by Engage Platform
* Client ID: Provided by Engage Platform
* Region Identifier: [User defined]
* UUID: Provided by Engage Platform
* Project Name/ Application Name: Used as Notification title for all the notifications displayed from the SDK.

#### Example
```js
Engage.initialize(apiKey, clientId, regionId, clientId,uuid).then((initResult)=> {
	// initialization success or fail
	if (initResult) {
	  // success
	} else {
	  // fail
	}
 }).catch(error => {
    // exception message
 });
```
---
### isInitialized
Check SDK is intialized or not

#### Example
```js
const isInitialized = Engage.isInitialized// true or false
```

---
### registerUser
Register user into SDK. To register the user with the SDK, 2 things are needed:
* BirthDate of the user birthDate: date into timestamp formate(string value)
* Gender of the user gender:Gender: 1. Male/male 2. Female/female

#### Example
```js
Engage.registerUser(birthdate, gender).then((registerResult)=> {
	// registration success or fail	
	if (registerResult) {
	  // success
	} else {
	  // fail
	}
 }).catch(error => {
    // exception message
 });
```
---
### isUserRegistered
Check user is already registered with sdk or not

#### Example
```js
const isUserRegistered = Engage.isUserRegistered//true or false
```
---

### updateUser
Update user into SDK,To update the user with the SDK, 3 things are needed:
* BirthDate of the user birthDate: date into timestamp formate(string value)
* Gender of the user gender:Gender: 1. Male/male 2. Female/female
* Tags- Array of objects, objects contains two key value pair (isSelected: true/false, name: 'SPORTS'(Technology etc))

#### Example
```js
Engage.updateUser(birthdate, gender, tags).then((updateResult)=>{
	// update profile success or fail
	if (updateResult) {
	  // success
	} else {
	  // fail
	}
 }).catch(error => {
    // exception message
 });
```
---
### startScan
Detect beacon start, if scan is stopped then start scan 
startScan method call requires Location permission to start scanning as underlying native module requires this permission for scanning beacons. Make sure that the app has location permission before starting the scan, otherwise the scanning process won’t get started and it won’t give any scan results

#### Example
```js
Engage.startScan();
```
---
### stopScan
Stop scanning for beacon

#### Example
```js
Engage.stopScan().then((stopResult)=>{
	// scan stop or not
	if (stopResult) {
	  // success
	} else {
	  // fail
	}
 }).catch(error => {
    // exception message
 });
```
---

## All listeners

### onBeaconEnter
listen for enter into beacon range
#### Example
```js
Engage.addListener('onBeaconEnter', (beaconInfo) => {

});
```
---

### onBeaconExit
listen for exit into beacon range
#### Example
```js
Engage.addListener('onBeaconExit', (beaconInfo) => {

});
```
---

### onBeaconLocation
listen for enter into location 
#### Example
```js
Engage.addListener('onBeaconLocation', (locationInfo) => {

});
```
---

### onStopScan
lister for stop scanning for beacon 
#### Example
```js
Engage.addListener('onStopScan', () => {

});
```
---
### onLocationChange
listen for location change 
#### Example
```js
Engage.addListener('onLocationChange', (locationInfo) => {

});
```
---
### onNotificationClicked
Notification tap android only - listen when notification tap
#### Example
```js
Engage.addListener('onNotificationClicked', (result) => {

});
```
---
### fetchContentLocation
Fetch Content using location based information get location based content from engage SDK server

#### Example
```js
Engage.fetchContentLocation(locationInfo).then((locationContent)=>{
 // array of location based content
 // for android = JSON.parse(locationContent)
	if (Platform.OS === 'android') {
		console.log(JSON.parse(locationContent))
	} else {
		// for ios = No need to parsing
		console.log(locationContent)
	}
 });
```
---
### fetchContentBeacon
Fetch Content using beacon based information get beacon based content from engage SDK server

#### Example
```js
Engage.fetchContentBeacon(beaconInfo).then((beaconContent)=>{
	// array of beacon based content
	// for android = JSON.parse(beaconContent)
   if (Platform.OS === 'android') {
	  console.log(JSON.parse(beaconContent))
   }else{
	  // for ios = No need to parsing
	 console.log(locationContent)
  }
});
```
---

### getContentForActions
When notification is receive passing notification data and get content from engage SDK server
#### Example
```js
Engage.getContentForActions(notificationInfo).then((result)=>{
	// android - result is into array of single object
	// ios = result is single object
	if (Platform.OS === 'android') {
		console.log(JSON.parse(result))
	} else {
		//for ios = No need to parsing
		console.log(result)
	}
 });
```
---
### isScanOnGoing
Check beacon scanning or not, it returns callback function with scan status (boolean).

#### Example
```js 
Engage.isScanOnGoing().then((isScanning)=>{
	// scanning is ongoing or not
	if (isScanning) {
	  // beacon scanning is ongoing
	} else {
	  // beacon scanning stopped
	}
 });
```
---
### logout
Logout from engage sdk
#### Example
```js
Engage.logout().then((logoutResult)=>{
	// logout  success or fail
	if (logoutResult) {
		// success
	} else {
		// fail
	}
 });
```
---
### updateApiKey
Updates the api key by verifying it before setting it

#### Example
```js
 Engage.updateApiKey(apiKey).then((updateResult)=>{
 	// update APIKey success or fail
	if (updateResult) {
		// success
	} else {
		// fail
	}
 });
```
---
### updateBeaconUUID
Updates beacon uuid by verifying the structure
#### Example
```js
Engage.updateBeaconUUID(uuidString).then((updateResult)=>{
	// update beacon uuid success or fail
	if (updateResult) {
		// success
	} else {
		// fail
	} 
 });
```
---
### setRegionParams
Sets region params like uuid and region identifier which is used to identify region
#### Example
```js
Engage.setRegionParams(uuid, regionIdentifier).then((result)=>{
	 // set region parameter success or fail
	 if (result) {
		// success
	 } else {
		// fail
	 } 
  });
```
---
### config
Provides configuration object that the sdk uses for internal configuration, configuation object contains following info
 * apiKey, appName,
 * beaconUUID, clientId, 
 * regionId, isBackgroundModeEnabled,
 * isLocationBasedContentEnabled, isNotificationEnabled,
 * isUserRegistered, pendingIntentClassName,
 * snoozeNotificationTimeInMinutes, snoozeContentTimeInHours,
 * tags
 #### Example
```js
Engage.config().then((configInfo)=>{
	 console.log(configInfo.apiKey)
	 console.log(configInfo.appName)
	 console.log(configInfo.beaconUUID)
	 console.log(configInfo.clientId)
	 console.log(configInfo.regionId)
	 // ....
 })
```
---
### setBackgroundMode
Background scan mode to keep scanning even when the app is not in foreground, Settings background mode enabled, it will also start scan on device boot, default mode false
#### Example
```js
Engage.setBackgroundMode(true);
```
---
### setNotificationMode
It displays notifications on scan results and those notifications leads back to main app, default mode false
#### Example
```js
Engage.setNotificationMode(true);
```
---
### setGeoLocationMode
It set geolocation mode enable/disable, default mode false
#### Example
```js
Engage.setGeoLocationMode(true);
```
---
### setSnoozeNotifications
Set Snooze notification time in minutes, 

default value is 1 minute, other options are - 0 min, 5 min, 15 min, 30 min, 60 min
#### Example
```js
Engage.setSnoozeNotifications('15');
```
---
### setSnoozeContent
Set Snooze content time in hours
default value is 24 hours

Other options are - 0 hour, 1 hour, 2 hour, 4 hour, 6 hour, 12 hour
#### Example
```js
Engage.setSnoozeContent('1');
```
