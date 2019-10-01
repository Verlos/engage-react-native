#import <React/RCTViewManager.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>
@interface RCT_EXTERN_MODULE(Engage, RCTEventEmitter)
//RCT_EXTERN_METHOD(initialize:(NSString *)apiKey AppName:(NSString *)appName RegionId:(NSString *)regionId ClientId:(NSString *)clientId CallBack: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(initialize:(NSString *)apiKey AppName:(NSString *)appName RegionId:(NSString *)regionId ClientId:(NSString *)clientId initializeWithResolve: (RCTPromiseResolveBlock)resolve initializeWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(registerUser:(NSString *)birthDate Gender:(NSString *)gender registerWithResolve: (RCTPromiseResolveBlock)resolve registerWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(startScan: (NSString *)uuid)
RCT_EXTERN_METHOD(stopScan: (NSString *)uuid stopWithResolve: (RCTPromiseResolveBlock)resolve stopWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(isInitialized:(RCTPromiseResolveBlock)resolve isInitializeWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(isScanOnGoing: (RCTPromiseResolveBlock)resolve checkScanStatusWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(updateApiKey: (NSString *)apiKey updateApiKeyWithResolve: (RCTPromiseResolveBlock)resolve updateApiKeyWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(logout: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(updateBeaconUUID: (NSString *)uuidString updateBeaconWithResolve: (RCTPromiseResolveBlock)resolve updateBeaconWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(setRegionParams: (NSString *)uuid RegionId:(NSString *)regionId setRegionWithResolve: (RCTPromiseResolveBlock)resolve setRegionWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(config:(RCTPromiseResolveBlock)resolve configWithReject:(RCTPromiseRejectBlock)reject)
@end


