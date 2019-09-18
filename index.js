import { NativeModules } from 'react-native';

const { Engage } = NativeModules;

function registerUser({ apiKey, birthDate, gender = null, feature1 = null, feature2 = null, feature3 = null, callback } = {}) {
  Engage.registerUser(apiKey, birthDate, gender, feature1, feature2, feature3, callback);
};

export default {
  initialize: Engage.initialize,
  isInitialized: Engage.isInitialized,
  isScanOnGoing: Engage.isScanOnGoing,
  startScan: Engage.startScan,
  stopScan: Engage.stopScan,
  updateApiKey: Engage.updateApiKey,
  logout: Engage.logout,
  setRegionParams: Engage.setRegionParams,
  config: Engage.config,
  registerUser: registerUser
};