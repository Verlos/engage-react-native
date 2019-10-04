import Foundation
import EngageSDK
import CoreLocation
//Note that for objective-c (and therefore RN) to see the class you need to give the @objc hint
//Also, any method exposed to objective-c runtime will also require the hint.
@objc(Engage)
class Engage: RCTEventEmitter {
    //Demonstrate a basic promise-based function in swift
    private let errorMessage: String = "Engage SDK initialization missing or entered invalid API Key, please try again"
    private let ISINITIALIZED : String = "isInitialized"
    private let ISUSERREGISTERED : String = "isUserRegistered"
    private let locationManager = CLLocationManager()
    @objc
    func initialize(_ apiKey: String, AppName appName: String, RegionId regionId: String, ClientId clientId: String, UUID uuid: String, initializeWithResolve resolve : @escaping RCTPromiseResolveBlock, initializeWithReject reject: @escaping RCTPromiseRejectBlock){
        print("initialize with apiKey: \(apiKey) and uuid: \(uuid)")
        let intiReq = InitializationRequest.init(apiKey: apiKey, appName: appName, regionId: regionId, clientId: clientId, uuid: uuid)
        _ = EngageSDK.init(initData: intiReq, onSuccess: {
            print("SDK success")
            resolve(true)
        }) { (message) in
            print("SDK fail")
            reject("exception", self.errorMessage, nil);
        }
    }
    
    @objc func registerUser(_ birthDate: String, Gender gender:String, registerWithResolve resolve : @escaping RCTPromiseResolveBlock, registerWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else{
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.callRegisterUserApi(birthDate: birthDate, gender: gender, tags: nil) { (userData) in
            if let _ = userData {
                resolve(true)
            }else {
                resolve(false)
            }
        }
    }
    
    @objc func startScan(){
        guard let manager = EngageSDK.shared else { return }
        if manager.locationManager == nil {
            manager.locationManager = EngageLocationManager.getLocationManager()
        }
        manager.start() { (message, permission) in
            if !(message.isEmpty) {
                if !permission {
                    // scanning block
                } else {
                    // scanning start
                    DispatchQueue.main.async {
                        print("-------------Start scanning------------")
                        self.setupBeaconMoitorBlock()
                    }
                }
            } else {
                // false
            }
        }
    }
    
    @objc func stopScan(_ resolve : @escaping RCTPromiseResolveBlock, stopWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.stop()
        print("-------------Stop scanning------------")
        resolve(true)
    }
    
    @objc func isInitialized(_ resolve : @escaping RCTPromiseResolveBlock, isInitializeWithReject reject: @escaping RCTPromiseRejectBlock){
        EngageSDK.shared != nil ? resolve(true) : resolve(false);
    }
    
    @objc func isScanOnGoing(_ resolve : @escaping RCTPromiseResolveBlock, checkScanStatusWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        resolve(manager.isScanOnGoing)
        //check isScanOnGoing or not
    }
    
    @objc func updateApiKey(_ apiKey: String, updateApiKeyWithResolve resolve : @escaping RCTPromiseResolveBlock, updateApiKeyWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.updateApiKey(apiKey) { (result) in
            resolve(result)
        }
        
    }
    
    @objc func logout(_ resolve : @escaping RCTPromiseResolveBlock, logoutWithReject reject: @escaping RCTPromiseRejectBlock){
        //logout
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.logout()
        resolve(true)
    }
    
    @objc func updateBeaconUUID(_ uuidString: String, updateBeaconWithResolve resolve : @escaping RCTPromiseResolveBlock, updateBeaconWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.updateBeaconUUID(uuid: uuidString, callBack: { (result) in
            resolve(result)
        })
    }
    //    setRegionWithResolve
    @objc func setRegionParams(_ uuid:String, RegionId regionIdentifier: String, setRegionWithResolve resolve : @escaping RCTPromiseResolveBlock, setRegionWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.setRegionParams(uuid, regionId: regionIdentifier, callBack: { (result) in
            resolve(result)
        })
    }
    
    @objc func config(_ resolve : @escaping RCTPromiseResolveBlock, configWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        let dicInfo = NSMutableDictionary();
        dicInfo.setValue(manager.appId, forKey: "appId")
        dicInfo.setValue(manager.initData?.apiKey, forKey: "apiKey")
        dicInfo.setValue(manager.initData?.appName, forKey: "appName")
        dicInfo.setValue(manager.initData?.uuid, forKey: "beaconUUID")
        dicInfo.setValue(manager.initData?.clientId, forKey: "clientId")
        dicInfo.setValue(manager.initData?.regionId, forKey: "regionId")
        dicInfo.setValue(manager.isBackgroundMode, forKey: "isBackgroundModeEnabled")
        //        dicInfo.setValue(manager.isLocationBasedContentEnabled, forKey: "isLocationBasedContentEnabled")
        dicInfo.setValue(manager.isNotificationEnabled, forKey: "isNotificationEnabled")
        dicInfo.setValue(manager.isUserRegistered, forKey: "isUserRegistered")
        //        dicInfo.setValue(manager.pendingIntentClassName, forKey: "pendingIntentClassName")
        resolve(dicInfo)
    }
    
    @objc func setBackgroundMode(_ enable: Bool){
        guard let manager = EngageSDK.shared else { return }
        manager.isBackgroundMode = enable
    }
    
    @objc func setNotificationMode(_ enable: Bool){
        guard let manager = EngageSDK.shared else { return }
        manager.isNotificationEnabled = enable
    }
    
    /// Setup Beacon Moitor Block
    func setupBeaconMoitorBlock() {
        guard let manager = EngageSDK.shared else { return }
        manager.onBeaconCamped = { beacon in
            print("Entry beacon \(beacon)")
            let beaconInfo = NSMutableDictionary();
            beaconInfo.setValue(beacon.beacon.major, forKey: "major")
            beaconInfo.setValue(beacon.beacon.minor, forKey: "minor")
            beaconInfo.setValue(beacon.rssi, forKey: "rssi")
            beaconInfo.setValue("\(beacon.beacon.proximityUUID)", forKey: "uuid")
            self.sendEvent(withName: "onBeaconEnter", body: beaconInfo)
        }
        manager.onBeaconExit = { beacon in
            print("Exit beacon \(beacon)")
            var beaconInfo: [String: Any] = [:]
            beaconInfo["major"] = beacon.beacon.major
            beaconInfo["minor"] = beacon.beacon.minor
            beaconInfo["rssi"]  = beacon.rssi
            beaconInfo["uuid"]  = beacon.beacon.proximityUUID
            self.sendEvent(withName: "onBeaconExit", body: beaconInfo)
        }
        manager.onRangedBeacon = { beacons in
            print("Ranged beacons \(beacons)")
        }
        manager.onRuleTriggeres = { rule in
            print("Rule triggeres \(rule)")
            
        }
        manager.onPermissionChange = { (message, permission) in
            if !permission {
                
            }
        }
    }
    
    //Note that any event name used in sendEvent above needs to be in this array.
    override func supportedEvents() -> [String]! {
        return ["Engage", "onBeaconEnter", "onBeaconExit"]
    }
    //Demonstrate setting constants. Note that constants can be (almost) any type, but that this function is only evaluated once, at initialidation
    @objc override func constantsToExport() -> Dictionary<AnyHashable, Any> {
        return [
            ISINITIALIZED: EngageSDK.shared?.isInitialized as Any,
            ISUSERREGISTERED: EngageSDK.shared?.isUserRegistered as Any,
            "startTime": Date().description
        ];
    }
    override class func requiresMainQueueSetup() -> Bool {
        return true;
    }
}

//extension Engage: CLLocationManagerDelegate {
//  func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
//    print(locations.first ?? "")
//  }
//}
