#import <React/RCTViewManager.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>
@interface RCT_EXTERN_MODULE(Engage, RCTEventEmitter)
RCT_EXTERN_METHOD(initialize:(NSString *)apiKey AppName:(NSString *)appName RegionId:(NSString *)regionId ClientId:(NSString *)clientId CallBack: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(registerUser:(NSString *)birthDate Gender:(NSString *)gender CallBack: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(startScan: (NSString *)uuid)
RCT_EXTERN_METHOD(stopScan: (NSString *)uuid CallBack: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(isInitialized: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(isScanOnGoing: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(updateApiKey: (NSString *)apiKey CallBack: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(logout: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(updateBeaconUUID: (NSString *)uuidString CallBack: (RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(setRegionParams: (NSString *)uuid RegionId:(NSString *)regionId CallBack:(RCTResponseSenderBlock)callBack)
RCT_EXTERN_METHOD(config: (RCTResponseSenderBlock)callBack)
@end

