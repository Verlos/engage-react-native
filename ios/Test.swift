import Foundation
import EngageSDK
//Note that for objective-c (and therefore RN) to see the class you need to give the @objc hint
//Also, any method exposed to objective-c runtime will also require the hint.
@objc(Engage)
class Engage: RCTEventEmitter {
    //Demonstrate a basic promise-based function in swift
    private let errorMessage: String = "Engage SDK initialization missing or entered invalid API Key, please try again"
    
    @objc
    func initialize(_ apiKey: String, AppName appName: String, RegionId regionId: String, ClientId clientId: String, initializeWithResolve resolve : @escaping RCTPromiseResolveBlock, initializeWithReject reject: @escaping RCTPromiseRejectBlock){
        let intiReq = InitializationRequest.init(apiKey: apiKey, appName: appName, regionId: regionId, clientId: clientId)
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
    
    @objc func startScan(_ uuid: String){
        guard let manager = EngageSDK.shared else { return }
        manager.start(uuid: uuid, identifier: "") { (message, permission) in
            DispatchQueue.main.async {
                if !(message.isEmpty) {
                    if !permission {
                        // scanning block
                    } else {
                        // scanning start
                        self.setupBeaconMoitorBlock()
                    }
                } else {
                    // false
                }
            }
        }
    }
    
    @objc func stopScan(_ uuid:String, stopWithResolve resolve : @escaping RCTPromiseResolveBlock, stopWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.stop(uuid, Identifire: "")
        resolve(true)
    }
    
//    @objc func isInitialized(_ callBack: @escaping RCTResponseSenderBlock){
//        EngageSDK.shared == nil ? callBack([false]) : callBack([true])
//    }
    
    @objc func isInitialized(_ resolve : @escaping RCTPromiseResolveBlock, isInitializeWithReject reject: @escaping RCTPromiseRejectBlock){
        EngageSDK.shared != nil ? resolve(true) : resolve(false);
    }
    
    @objc func isScanOnGoing(_ resolve : @escaping RCTPromiseResolveBlock, checkScanStatusWithReject reject: @escaping RCTPromiseRejectBlock){
        //check isScanOnGoing or not
    }
    
    @objc func updateApiKey(_ apiKey: String, updateApiKeyWithResolve resolve : @escaping RCTPromiseResolveBlock, updateApiKeyWithReject reject: @escaping RCTPromiseRejectBlock){
        if(EngageSDK.shared != nil){
            resolve(true)
        }else{
            reject("exception", self.errorMessage, nil);
        }
    }
    
    @objc func logout(_ resolve : @escaping RCTPromiseResolveBlock, logoutWithReject reject: @escaping RCTPromiseRejectBlock){
        //logout
        if(EngageSDK.shared != nil){
            resolve(true)
        }else{
            reject("exception", self.errorMessage, nil);
        }
    }
    
    @objc func updateBeaconUUID(_ uuidString: String, updateBeaconWithResolve resolve : @escaping RCTPromiseResolveBlock, updateBeaconWithReject reject: @escaping RCTPromiseRejectBlock){
        if(EngageSDK.shared != nil){
            resolve(true)
        }else{
            reject("exception", self.errorMessage, nil);
        }
    }
//    setRegionWithResolve
    @objc func setRegionParams(_ uuid:String, RegionId regionIdentifier: String, setRegionWithResolve resolve : @escaping RCTPromiseResolveBlock, setRegionWithReject reject: @escaping RCTPromiseRejectBlock){
        if(EngageSDK.shared != nil){
            resolve(true)
        }else{
            reject("exception", self.errorMessage, nil);
        }
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
        //        dicInfo.setValue(manager.initData?.beaconUUID, forKey: "beaconUUID")
        dicInfo.setValue(manager.initData?.clientId, forKey: "clientId")
        dicInfo.setValue(manager.initData?.regionId, forKey: "regionId")
        dicInfo.setValue(manager.isBackgroundMode, forKey: "isBackgroundModeEnabled")
        //        dicInfo.setValue(manager.isLocationBasedContentEnabled, forKey: "isLocationBasedContentEnabled")
        dicInfo.setValue(manager.isNotificationEnabled, forKey: "isNotificationEnabled")
        //        dicInfo.setValue(manager.isUserRegistered, forKey: "isUserRegistered")
        //        dicInfo.setValue(manager.pendingIntentClassName, forKey: "pendingIntentClassName")
        resolve(dicInfo)
    }
    
    /// Setup Beacon Moitor Block
    func setupBeaconMoitorBlock() {
        
        guard let manager = EngageSDK.shared else { return }
        manager.onBeaconCamped = { beacon in
            print("Entry beacon \(beacon)")
            
        }
        manager.onBeaconExit = { beacon in
            print("Exit beacon \(beacon)")
            
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
        return ["Engage"]
    }
    //Demonstrate setting constants. Note that constants can be (almost) any type, but that this function is only evaluated once, at initialidation
    @objc override func constantsToExport() -> Dictionary<AnyHashable, Any> {
        return [
            "a": "A",
            "b": "B",
            "startTime": Date().description
        ];
    }
    override class func requiresMainQueueSetup() -> Bool {
        return false;
    }
}
