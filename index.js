import { NativeModules } from 'react-native';
const { ReactNativeEngage } = NativeModules;

function registerUser({ apiKey, birthDate, gender = null, feature1 = null, feature2 = null, feature3 = null, callback } = {}) {
  ReactNativeEngage.registerUser(apiKey, birthDate, gender, feature1, feature2, feature3, callback);
};

export default {
  initialize: ReactNativeEngage.initialize,
  isInitialized: ReactNativeEngage.isInitialized,
  isScanOnGoing: ReactNativeEngage.isScanOnGoing,
  startScan: ReactNativeEngage.startScan,
  stopScan: ReactNativeEngage.stopScan,
  updateApiKey: ReactNativeEngage.updateApiKey,
  logout: ReactNativeEngage.logout,
  setRegionParams: ReactNativeEngage.setRegionParams,
  config: ReactNativeEngage.config,
  registerUser: registerUser
};
