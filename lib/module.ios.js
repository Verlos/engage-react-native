import { NativeModules, DeviceEventEmitter } from 'react-native';
const { Engage } = NativeModules;

/**
 * Initializes the engage sdk
 * 
 * @param {*} apiKey api key for sdk
 * @param {*} appName application name
 * @param {*} regionId bundleid or appid
 * @param {*} clientId client id
 * @param {*} uuid uuid of beacon
 * @param {*} callback function that returns boolean value
 */
function initialize(apiKey, appName, regionId, clientId, callback) {
  Engage.initialize(apiKey, appName, regionId, clientId, callback)
}

/**
 * Registers new user by making api call to server.
 * 
 * @param {*} apiKey apikey for sdk initialization
 * @param {*} birthDate user birthdate into timestamp formate
 * @param {*} gender user gender - Male or female
 * @param {*} callback returns true or false
 */
function registerUser(apiKey, birthDate, gender = null, callback) {
  Engage.registerUser(apiKey, birthDate, gender, callback);
};

/**
 * Beacon enter listener, it will return beacon information when beacon enter into beacon range.
 * @param {*} onBeaconEnter  
 */
function setBeaconScanListener(onBeaconEnter) {
  DeviceEventEmitter.addListener('onBeaconEnter', onBeaconEnter)
}

/**
 * Beacon exit listener, it will return beacon information when beacon exit from beacon range.
 * 
 * @param {*} onBeaconExit - 
 */
function onBeaconExit(onBeaconExit) {
  DeviceEventEmitter.addListener('onBeaconExit', onBeaconExit)
}

/**
 * detect beacon start, if scan is stopped then start scan 
 */
function startScan() {
  Engage.isScanOnGoing((isGoing) => {
    if (!isGoing) {
      Engage.startScan();
    }
  })
}

/**
 * Stops the ongoing beacon scan
 * 
 * @param {*} callback function that returns boolean value
 */
function stopScan(callback) {
  Engage.stopScan(callback)
}

/**
 * remove enter and exit listeners
 */
function removeBeaconListener() {
  DeviceEventEmitter.removeListener('onBeaconEnter');
  DeviceEventEmitter.removeListener('onBeaconExit');
}

/**
 * check sdk initialized or not, it returns callback function with initializes value (boolean).
 * 
 * @param {*} callback function that returns boolean value
 */
function isInitialized(callback) {
  Engage.isInitialized(callback);
}

/**
 * check beacon scanning or not, it returns callback function with scan status (boolean).
 * 
 * @param {*} callback function that returns boolean value
 */
function isScanOnGoing(callback) {
  Engage.isScanOnGoing(callback);
}

/**
 * Updates the api key by verifying it before setting it
 * 
 * @param {*} apiKey SDK api key
 */
function updateApiKey(apiKey, callback) {
  Engage.updateApiKey(apiKey, callback)
}

/**
 * logout from engage sdk
 * 
 * @param {*} callback function that returns boolean value
 */
function logout(callback) {
  Engage.logout(callback)
}

/**
 * Updates beacon uuid by verifying the structure
 * 
 * @param {*} uuidString new beacon UUID
 * @param {*} callback function that returns boolean value
 */
function updateBeaconUUID(uuidString, callback) {
  Engage.updateBeaconUUID(uuidString, callback);
}

/**
 * Sets region params like uuid and region identifier which is used to identify region
 * 
 * @param {*} uuid beacon uuid
 * @param {*} regionIdentifier - new region identifier 
 * @param {*} callback - function that return boolean value
 */
function setRegionParams(uuid, regionIdentifier, callback) {
  Engage.setRegionParams(uuid, regionIdentifier, callback)
}

/**
 * Provides configuration object that the sdk uses for internal configuration
 * 
 * @param {*} callback function that returns object value, object contains value like 
 * apiKey, appName,
 * beaconUUID, clientId, 
 * regionId, isBackgroundModeEnabled,
 * isLocationBasedContentEnabled, isNotificationEnabled,
 * isUserRegistered, pendingIntentClassName
 */
function config(callback) {
  Engage.config(callback);
}




module.exports = {
  // parsers constants
  initialize,
  isInitialized,
  isScanOnGoing,
  startScan,
  stopScan,
  updateApiKey,
  logout,
  setRegionParams: Engage.setRegionParams,
  config: Engage.config,
  registerUser,
  updateBeaconUUID,
  setBeaconScanListener,
  removeBeaconListener,
  onBeaconExit
};