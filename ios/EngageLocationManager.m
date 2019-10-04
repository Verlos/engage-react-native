//
//  EngageLocationManager.m
//  react-native-engage
//
//  Created by Brijesh Shiroya on 02/10/19.
//

#import "EngageLocationManager.h"
#import <React/RCTBridge.h>

static CLLocationManager* mainManager = nil;

@implementation EngageLocationManager

- (dispatch_queue_t)methodQueue{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE(EngageLocationManager)

+ (void)config:(CLLocationManager *)manager {
    mainManager = manager;
}

+ (CLLocationManager*)getLocationManager {
    return mainManager;
}

RCT_EXPORT_METHOD(locationManager) {
    [EngageLocationManager config:nil];
}

RCT_EXPORT_METHOD(getLocationManager) {
    [EngageLocationManager getLocationManager];
}

@end

