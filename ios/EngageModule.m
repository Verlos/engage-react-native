#import <React/RCTViewManager.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>
@interface RCT_EXTERN_MODULE(EngageModule, RCTEventEmitter)

RCT_EXTERN_METHOD(initialize:(NSString *)apiKey AppName:(NSString *)appName RegionId:(NSString *)regionId ClientId:(NSString *)clientId UUID:(NSString *)uuid initializeWithResolve: (RCTPromiseResolveBlock)resolve initializeWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(registerUser:(NSString *)birthDate Gender:(NSString *)gender registerWithResolve: (RCTPromiseResolveBlock)resolve registerWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(updateUser:(NSString *)birthDate Gender:(NSString *)gender Tags:(NSArray *)tags updateUserWithResolve: (RCTPromiseResolveBlock)resolve updateUserWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(fetchContentBeacon:(NSDictionary *)beaconInfo fetchContentWithResolve: (RCTPromiseResolveBlock)resolve fetchContentWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(fetchContentNotification:(NSString *)url fetchContentWithResolve: (RCTPromiseResolveBlock)resolve fetchContentWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(fetchContentLocation:(NSDictionary *)locationInfo fetchContentWithResolve: (RCTPromiseResolveBlock)resolve fetchContentWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(getContentForActions:(NSDictionary *)userInfo onPromitionWithResolve: (RCTPromiseResolveBlock)resolve onPromitionWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(logEvent:(NSString *)logType ContentId: (NSString *)contentId ContenType:(NSString *)contentType Param2:(NSString *)param2)
RCT_EXTERN_METHOD(logNotificationEvent:(NSString *)notificationId Action:(NSString *)action)
RCT_EXTERN_METHOD(callPushNotificationRegister:(NSString *)fcmToken registerPushWithResolve: (RCTPromiseResolveBlock)resolve registerPushWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(startScan)
RCT_EXTERN_METHOD(stopScan:(RCTPromiseResolveBlock)resolve stopWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(isInitialized:(RCTPromiseResolveBlock)resolve isInitializeWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(isScanOnGoing: (RCTPromiseResolveBlock)resolve checkScanStatusWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(updateApiKey: (NSString *)apiKey updateApiKeyWithResolve: (RCTPromiseResolveBlock)resolve updateApiKeyWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(logout: (RCTPromiseResolveBlock)resolve logoutWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(updateBeaconUUID: (NSString *)uuidString updateBeaconWithResolve: (RCTPromiseResolveBlock)resolve updateBeaconWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(setRegionParams: (NSString *)uuid RegionId:(NSString *)regionId setRegionWithResolve: (RCTPromiseResolveBlock)resolve setRegionWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(config:(RCTPromiseResolveBlock)resolve configWithReject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(setBackgroundMode: (BOOL)enable)
RCT_EXTERN_METHOD(setNotificationMode: (BOOL)enable)
RCT_EXTERN_METHOD(setGeoLocationMode: (BOOL)enable)
RCT_EXTERN_METHOD(setSnoozeNotifications: (NSString *)notificationMin)
RCT_EXTERN_METHOD(setSnoozeContent: (NSString *)notificationHour)

@end


