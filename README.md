# react-native-engage

 Add beacon technology in your React Native application for both iOS and Android.
# Getting started
---
### If you are using `react-native` >= 0.60.0
## Automatic Way
---
``` 
yarn add react-native-engage
```

or if you're using npm
``` 
npm install react-native-engage --save
```
---
#### Important:
Linking is not needed anymore. ``react-native@0.60.0+`` supports dependencies auto linking.
For iOS you also need additional step to install auto linked Pods (Cocoapods should be installed):
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
yarn add react-native-engage
react-native link react-native-engage
```

or if you're using npm
``` 
npm install react-native-engage --save
react-native link react-native-engage
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
2. Go to `node_modules` ➜ `react-native-engage` and add `Engage.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libEngage.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

# iOS Install(using Pods)
You just need to add to your Podfile the react-native-engage dependency.

```ruby
  # React-Native-Share pod
  pod 'RNShare', :path => '../node_modules/react-native-engage'
```

After that, just run a `pod install` or `pod udpate` to get up and running with react-native-engage. 

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.EngagePackage;` to the imports at the top of the file
  - Add `new EngagePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-engage'
  	project(':react-native-engage').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-engage/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-engage')
  	```


## Methods
```javascript
import Engage from 'react-native-engage';

// TODO: What to do with the module?
Engage;
```
