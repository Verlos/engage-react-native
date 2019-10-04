//
//  EngageLocationManager.h
//  react-native-engage
//
//  Created by Brijesh Shiroya on 02/10/19.
//

#import <React/RCTBridgeModule.h>
#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import <React/RCTBridge.h>
@interface EngageLocationManager : NSObject<RCTBridgeModule>
+ (void)config:(CLLocationManager*)manager;
+ (CLLocationManager*)getLocationManager;
@end
