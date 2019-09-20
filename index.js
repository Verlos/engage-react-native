import { Platform } from 'react-native';

const EngageModule = Platform.select({
  ios: () => require('./lib/module.ios.js'),
  android: () => require('./lib/module.android.js'),
})();

export default EngageModule;