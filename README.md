# react-native-react-native-engage

## Getting started

`$ npm install react-native-react-native-engage --save`

### Mostly automatic installation

`$ react-native link react-native-react-native-engage`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-react-native-engage` and add `ReactNativeEngage.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libReactNativeEngage.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.ReactNativeEngagePackage;` to the imports at the top of the file
  - Add `new ReactNativeEngagePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-react-native-engage'
  	project(':react-native-react-native-engage').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-react-native-engage/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-react-native-engage')
  	```


## Usage
```javascript
import ReactNativeEngage from 'react-native-react-native-engage';

// TODO: What to do with the module?
ReactNativeEngage;
```
