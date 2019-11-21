import { Platform, NativeModules, DeviceEventEmitter, NativeEventEmitter } from 'react-native';
const { EngageModule } = NativeModules;

/**
 * Initializes the engage sdk
 * 
 * @param {*} apiKey api key for sdk
 * @param {*} appName application name
 * @param {*} regionId bundleid or appid
 * @param {*} clientId client id
 * @param {*} uuid uuid of beacon
 * @param {*} promise function that returns boolean value or error
 */
async function initialize(apiKey, appName, regionId, clientId, uuid) {
  return await EngageModule.initialize(apiKey, appName, regionId, clientId, uuid)
}

/**
 * Registers new user by making api call to server.
 * 
 * @param {*} birthDate user birthdate into timestamp formate
 * @param {*} gender user gender - Male or female
 * @param {*} promise function that returns boolean value or error
 */
async function registerUser(birthDate, gender = null) {
  return await EngageModule.registerUser(birthDate, gender);
};


/**
 * 
 * @param {*} birthDate update birthdate into timestamp formate
 * @param {*} gender update gender - Male or female
 * @param {*} promise function that returns boolean value or error
 */
async function updateUser(birthDate, gender = null, tags = null) {
  return await EngageModule.updateUser(birthDate, gender, tags);
};

/**
 * detect beacon start, if scan is stopped then start scan 
 */
function startScan() {
  EngageModule.isScanOnGoing().then((isGoing) => {
    if (!isGoing) {
      EngageModule.startScan();
    }
  })
}

/**
 * Stops the ongoing beacon scan
 * 
 * @param {*} promise function that returns boolean value or error
 */
async function stopScan() {
  return await EngageModule.stopScan()
}

/**
 * @param {*} beaconInfo fetch content using beacon
 */
async function fetchContentBeacon(beaconInfo) {
  return await Platform.OS === 'android' ?
    EngageModule.fetchContentBeacon(JSON.stringify(beaconInfo)) :
    EngageModule.fetchContentBeacon(beaconInfo)
}

/**
 * 
 * @param {*} locationInfo fetch content using location
 */
async function fetchContentLocation(locationInfo) {
  return await Platform.OS === 'android' ?
    EngageModule.fetchContentLocation(JSON.stringify(locationInfo)) :
    EngageModule.fetchContentLocation(locationInfo)
}

/**
 * 
 * @param {*} userInfo fetch content using notification data
 */
async function getContentForActions(userInfo) {
  return await Platform.OS === 'android' ?
    EngageModule.getContentForActions(JSON.stringify(userInfo)) :
    EngageModule.getContentForActions(userInfo)
}


/**
 * remove enter and exit listeners
 */
function removeBeaconListener() {
  DeviceEventEmitter.removeListener('onBeaconEnter');
  DeviceEventEmitter.removeListener('onBeaconExit');
  DeviceEventEmitter.removeListener('onBeaconLocation');
}

/**
 * check beacon scanning or not, it returns callback function with scan status (boolean).
 * 
 * @param {*} promise function that returns boolean value or error
 */
async function isScanOnGoing() {
  return await EngageModule.isScanOnGoing()
}

/**
 * Updates the api key by verifying it before setting it
 * 
 * @param {*} apiKey SDK api key
 * @param {*} promise function that returns boolean value or error
 */
async function updateApiKey(apiKey) {
  return await EngageModule.updateApiKey(apiKey)
}

/**
 * logout from engage sdk
 * 
 * @param {*} promise function that returns boolean value or error
 */
async function logout() {
  return await EngageModule.logout()
}

/**
 * Updates beacon uuid by verifying the structure
 * 
 * @param {*} uuidString new beacon UUID
 * @param {*} promise function that returns boolean value or error
 */
async function updateBeaconUUID(uuidString) {
  return await EngageModule.updateBeaconUUID(uuidString);
}

/**
 * Sets region params like uuid and region identifier which is used to identify region
 * 
 * @param {*} uuid beacon uuid
 * @param {*} regionIdentifier - new region identifier 
 * @param {*} promise function that returns boolean value or error
 */
async function setRegionParams(uuid, regionIdentifier) {
  return await EngageModule.setRegionParams(uuid, regionIdentifier)
}

/**
 * Provides configuration object that the sdk uses for internal configuration
 * 
 * @param {*} promise function that returns object value, object contains value like 
 * apiKey, appName,
 * beaconUUID, clientId, 
 * regionId, isBackgroundModeEnabled,
 * isLocationBasedContentEnabled, isNotificationEnabled,
 * isUserRegistered, pendingIntentClassName
 */
async function config() {
  return await EngageModule.config();
}

/**
 * If your app is closed, you can check if it was opened by a notification being clicked / tapped / opened
 */
async function getInitialNotification() {
  return await EngageModule.getInitialNotification();
}

/**
 * 
 * @param {*} enable background scan mode to keep scanning even when the app is not in foreground, Settings background mode enabled, it will also start scan on device boot
 */
function setBackgroundMode(enable) {
  EngageModule.setBackgroundMode(enable);
}

/**
 * @param {*} enable It displays notifications on scan results and those notifications leads back to main app.
 */
function setNotificationMode(enable) {
  EngageModule.setNotificationMode(enable);
}

/**
 * @param {*} enable It set geolocation mode enable disvale.
 */
function setGeoLocationMode(enable) {
  EngageModule.setGeoLocationMode(enable);
}

/**
 * addListeners for enterBeacon and exitBeacon
 */
const engageModule = new NativeEventEmitter(EngageModule)
function addListener(evantName, listener) {
  engageModule.addListener(evantName, (beaconInfo) => {
    const info = Platform.OS === 'ios' ? beaconInfo : JSON.parse(beaconInfo)
    listener(info);
  });
}

const Engage = {
  initialize,
  isInitialized: EngageModule.isInitialized,
  isScanOnGoing,
  startScan,
  stopScan,
  updateApiKey,
  logout,
  setRegionParams: EngageModule.setRegionParams,
  config,
  registerUser,
  updateUser,
  updateBeaconUUID,
  removeBeaconListener,
  isUserRegistered: EngageModule.isUserRegistered,
  addListener,
  setBackgroundMode,
  setNotificationMode,
  setGeoLocationMode,
  fetchContentBeacon,
  fetchContentLocation,
  getContentForActions,
  setSnoozeNotifications: EngageModule.setSnoozeNotifications,
  setSnoozeContent: EngageModule.setSnoozeContent,
  getInitialNotification
};

export default Engage;
