import { NativeModules, DeviceEventEmitter, NativeEventEmitter } from 'react-native';
const { Engage } = NativeModules;

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
  return await Engage.initialize(apiKey, appName, regionId, clientId, uuid)
}

/**
 * Registers new user by making api call to server.
 * 
 * @param {*} birthDate user birthdate into timestamp formate
 * @param {*} gender user gender - Male or female
 * @param {*} promise function that returns boolean value or error
 */
async function registerUser(birthDate, gender = null) {
  return await Engage.registerUser(birthDate, gender);
};


/**
 * 
 * @param {*} birthDate update birthdate into timestamp formate
 * @param {*} gender update gender - Male or female
 * @param {*} promise function that returns boolean value or error
 */
async function updateUser(birthDate, gender = null) {
  return await Engage.updateUser(birthDate, gender);
};


/**
 * detect beacon start, if scan is stopped then start scan 
 */
function startScan() {
  Engage.isScanOnGoing().then((isGoing) => {
    if (!isGoing) {
      Engage.startScan();
    }
  })
}

/**
 * Stops the ongoing beacon scan
 * 
 * @param {*} promise function that returns boolean value or error
 */
async function stopScan() {
  return await Engage.stopScan()
}

/**
 * remove enter and exit listeners
 */
function removeBeaconListener() {
  DeviceEventEmitter.removeListener('onBeaconEnter');
  DeviceEventEmitter.removeListener('onBeaconExit');
}

/**
 * check beacon scanning or not, it returns callback function with scan status (boolean).
 * 
 * @param {*} promise function that returns boolean value or error
 */
async function isScanOnGoing() {
  return await Engage.isScanOnGoing()
}

/**
 * Updates the api key by verifying it before setting it
 * 
 * @param {*} apiKey SDK api key
 * @param {*} promise function that returns boolean value or error
 */
async function updateApiKey(apiKey) {
  return await Engage.updateApiKey(apiKey)
}

/**
 * logout from engage sdk
 * 
 * @param {*} promise function that returns boolean value or error
 */
async function logout() {
  return await Engage.logout()
}

/**
 * Updates beacon uuid by verifying the structure
 * 
 * @param {*} uuidString new beacon UUID
 * @param {*} promise function that returns boolean value or error
 */
async function updateBeaconUUID(uuidString) {
  return await Engage.updateBeaconUUID(uuidString);
}

/**
 * Sets region params like uuid and region identifier which is used to identify region
 * 
 * @param {*} uuid beacon uuid
 * @param {*} regionIdentifier - new region identifier 
 * @param {*} promise function that returns boolean value or error
 */
async function setRegionParams(uuid, regionIdentifier) {
  return await Engage.setRegionParams(uuid, regionIdentifier)
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
  return await Engage.config();
}

/**
 * 
 * @param {*} enable background scan mode to keep scanning even when the app is not in foreground, Settings background mode enabled, it will also start scan on device boot
 */
function setBackgroundMode(enable) {
  Engage.setBackgroundMode(enable);
}

/**
 * @param {*} enable It displays notifications on scan results and those notifications leads back to main app.
 */
function setNotificationMode(enable) {
  Engage.setNotificationMode(enable);
}

/**
 * addListeners for enterBeacon and exitBeacon
 */
const engageModule = new NativeEventEmitter(Engage)
function addListener(evantName, listener) {
  engageModule.addListener(evantName, listener);
}

module.exports = {
  // parsers constants
  initialize,
  isInitialized: Engage.isInitialized,
  isScanOnGoing,
  startScan,
  stopScan,
  updateApiKey,
  logout,
  setRegionParams: Engage.setRegionParams,
  config: Engage.config,
  registerUser,
  updateUser,
  updateBeaconUUID,
  removeBeaconListener,
  isUserRegistered: Engage.isUserRegistered,
  addListener,
  setBackgroundMode,
  setNotificationMode,
};