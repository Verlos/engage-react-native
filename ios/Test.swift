import Foundation
import EngageSDK
//Note that for objective-c (and therefore RN) to see the class you need to give the @objc hint
//Also, any method exposed to objective-c runtime will also require the hint.
@objc(Engage)
class Engage: RCTEventEmitter {
    //Demonstrate a basic promise-based function in swift
    @objc
    func initialize(_ apiKey: String, AppName appName: String, RegionId regionId: String, ClientId clientId: String, CallBack callBack: @escaping RCTResponseSenderBlock){
        let intiReq = InitializationRequest.init(apiKey: apiKey, appName: appName, regionId: regionId, clientId: clientId)
        _ = EngageSDK.init(initData: intiReq, onSuccess: {
            print("SDK success")
            callBack([true])
        }) { (message) in
            print("SDK fail")
            callBack([false])
        }
    }
    
    
    @objc func registerUser(_ birthDate: String, Gender gender:String, CallBack callBack: @escaping RCTResponseSenderBlock){
        guard let manager = EngageSDK.shared else{
            callBack([false])
            return
        }
        manager.callRegisterUserApi(birthDate: birthDate, gender: gender, tags: nil) { (userData) in
            if let _ = userData {
                callBack([true])
            }else {
                callBack([false])
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
    
    @objc func stopScan(_ uuid:String, CallBack callBack: @escaping RCTResponseSenderBlock){
        guard let manager = EngageSDK.shared else {
            callBack([false])
            return
        }
        manager.stop(uuid, Identifire: "")
        callBack([true])
    }
    
    @objc func isInitialized(_ callBack: @escaping RCTResponseSenderBlock){
        EngageSDK.shared == nil ? callBack([false]) : callBack([true])
    }
    
    @objc func isScanOnGoing(_ callBack: @escaping RCTResponseSenderBlock){
        //check isScanOnGoing or not
    }
    
    @objc func updateApiKey(_ apiKey: String, callBack: @escaping RCTResponseSenderBlock){
        if (true){
            callBack([true])
        }else{
            callBack([false])
        }
    }
    
    @objc func logout(_ callBack: @escaping RCTResponseSenderBlock){
        //logout
        if (true){
            callBack([true])
        }else{
            callBack([false])
        }
    }
    
    @objc func updateBeaconUUID(_ uuidString: String, CallBack callBack: @escaping RCTResponseSenderBlock){
        if (true){
            callBack([true])
        }else{
            callBack([false])
        }
    }
    
    @objc func setRegionParams(_ uuid:String, RegionId regionIdentifier: String, CallBack callBack: @escaping RCTResponseSenderBlock){
        if (true){
            callBack([true])
        }else{
            callBack([false])
        }
        
    }
    
    @objc func config(_ callBack: @escaping RCTResponseSenderBlock){
        guard let manager = EngageSDK.shared else {
            callBack([[]])
            return
        }
        let dicInfo = NSMutableDictionary();
        dicInfo.setValue(manager.appId, forKey: "appId")
        //        dicInfo.setValue(manager.apiKey, forKey: "apiKey")
        //        dicInfo.setValue(manager.appName, forKey: "appName")
        //        dicInfo.setValue(manager.beaconUUID, forKey: "beaconUUID")
        //        dicInfo.setValue(manager.clientId, forKey: "clientId")
        //        dicInfo.setValue(manager.regionId, forKey: "regionId")
        //        dicInfo.setValue(manager.isBackgroundModeEnabled, forKey: "isBackgroundModeEnabled")
        //        dicInfo.setValue(manager.isLocationBasedContentEnabled, forKey: "isLocationBasedContentEnabled")
        dicInfo.setValue(manager.isNotificationEnabled, forKey: "isNotificationEnabled")
        //        dicInfo.setValue(manager.isUserRegistered, forKey: "isUserRegistered")
        //        dicInfo.setValue(manager.pendingIntentClassName, forKey: "pendingIntentClassName")
        callBack([dicInfo])
        
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
