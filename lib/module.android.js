import { NativeModules, DeviceEventEmitter } from 'react-native';
const { Engage } = NativeModules;

function registerUser(apiKey, birthDate, gender = null, callback) {
  Engage.registerUser(apiKey, birthDate, gender, callback);
};

/**
 * 
 * @param {*} onBeaconEnter  Add beaconScan listener, it will return beacon info
 */
function setBeaconScanListener(onBeaconEnter) {
  Engage.isScanOnGoing((isGoing) => {
    if (!isGoing) {
      Engage.startScan();
    }
    DeviceEventEmitter.addListener('onBeaconEnter', onBeaconEnter)
  })
}

/**
 * 
 * @param {*} onBeaconExit 
 */
function onBeaconExit(onBeaconExit) {
  Engage.isScanOnGoing((isGoing) => {
    if (!isGoing) {
      Engage.startScan();
    }
    DeviceEventEmitter.addListener('onBeaconExit', onBeaconExit)
  })
}

function removeBeaconListener() {
  DeviceEventEmitter.removeListener('onBeaconEnter');
}

module.exports = {
  // parsers constants
  initialize: Engage.initialize,
  isInitialized: Engage.isInitialized,
  isScanOnGoing: Engage.isScanOnGoing,
  startScan: Engage.startScan,
  stopScan: Engage.stopScan,
  updateApiKey: Engage.updateApiKey,
  logout: Engage.logout,
  setRegionParams: Engage.setRegionParams,
  config: Engage.config,
  registerUser: registerUser,
  updateBeaconUUID: Engage.updateBeaconUUID,
  setBeaconScanListener,
  removeBeaconListener,
  onBeaconExit
};