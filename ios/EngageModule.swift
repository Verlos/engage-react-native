import Foundation
import EngageSDK
import CoreLocation
//Note that for objective-c (and therefore RN) to see the class you need to give the @objc hint
//Also, any method exposed to objective-c runtime will also require the hint.
@objc(EngageModule)
class EngageModule: RCTEventEmitter {
    //Demonstrate a basic promise-based function in swift
    private let errorMessage: String = "Engage SDK initialization missing or entered invalid API Key, please try again"
    private let ISINITIALIZED : String = "isInitialized"
    private let ISUSERREGISTERED : String = "isUserRegistered"
    private let locationManager = CLLocationManager()
    private var latestBeacon: FilterBeacon?
    private var latestLocation: CLLocationCoordinate2D?
    @objc
    func initialize(_ apiKey: String, AppName appName: String, RegionId regionId: String, ClientId clientId: String, UUID uuid: String, initializeWithResolve resolve : @escaping RCTPromiseResolveBlock, initializeWithReject reject: @escaping RCTPromiseRejectBlock){
        print("initialize with apiKey: \(apiKey) and uuid: \(uuid)")
        let intiReq = InitializationRequest.init(apiKey: apiKey, appName: appName, regionId: regionId, clientId: clientId, uuid: uuid)
        _ = EngageSDK.init(initData: intiReq, onSuccess: {
            print("SDK success")
            resolve(true)
        }) { (message) in
            print("SDK fail")
            resolve(false)
        }
    }
    
    @objc func registerUser(_ birthDate: String, Gender gender:String, registerWithResolve resolve : @escaping RCTPromiseResolveBlock, registerWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else{
            reject("exception", self.errorMessage, nil);
            return
        }
        let timeInterval = TimeInterval(birthDate)
        let date = Date(timeIntervalSince1970:timeInterval!)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy/MM/dd" //Specify your format that you want
        let strDate = dateFormatter.string(from: date)
        manager.callRegisterUserApi(birthDate: strDate, gender: gender, tags: nil) { (userData) in
            if let _ = userData {
                resolve(true)
            }else {
                resolve(false)
            }
        }
    }
    
    @objc func updateUser(_ birthDate: String, Gender gender:String, Tags tags:[[String: Any]], updateUserWithResolve resolve : @escaping RCTPromiseResolveBlock, updateUserWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else{
            reject("exception", self.errorMessage, nil);
            return
        }
        
        let newTags = tags.map { (tag) in
            return Tag.init(isSelected: tag["isSelected"] as? Bool ?? false, name: tag["name"] as? String ?? "")
        }
        let timeInterval = TimeInterval(birthDate)
        let date = Date(timeIntervalSince1970:timeInterval!)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy/MM/dd" //Specify your format that you want
        let strDate = dateFormatter.string(from: date)
        manager.callRegisterUserApi(birthDate: strDate, gender: gender, tags: newTags) { (userData) in
            if let _ = userData {
                resolve(true)
            }else {
                resolve(false)
            }
        }
    }
    
    
    
    func json(from object:Any)->Data?{
        guard let data = try?JSONSerialization.data(withJSONObject: object, options:[])
            else{
                return nil
        }
        return data
    }
    
    @objc func fetchContentLocation(_ locationInfo: NSDictionary?, fetchContentWithResolve resolve: @escaping RCTPromiseResolveBlock, fetchContentWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else{
            reject("exception", self.errorMessage, nil);
            return
        }
        
        var userLocation: CLLocationCoordinate2D?
        if let locationInfo = locationInfo, let latitude = Double(locationInfo["latitude"] as! String), let longitude = Double(locationInfo["longitude"] as! String){
            userLocation = CLLocationCoordinate2D.init(latitude: latitude, longitude: longitude)
        }
        
        manager.callFetchForgroundContentApi(location: userLocation) { (result) in
            var arrResult = [Any]();
            if let result = result{
                result.forEach({ (item) in
                    let dic = [
                        "bigtext" : item.bigtext,
                        "id" : item.id,
                        "image" : item.image,
                        "note1": item.note1,
                        "note2" :item.note2,
                        "title" : item.title,
                        "type" : item.type,
                        "url" :  item.url
                    ]
                    arrResult.append(dic)
                })
                resolve(arrResult as? NSArray)
            }
        }
    }
    
    @objc func fetchContentBeacon(_ beaconInfo: NSDictionary, fetchContentWithResolve resolve: @escaping RCTPromiseResolveBlock, fetchContentWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else{
            reject("exception", self.errorMessage, nil);
            return
        }
        
        
        manager.callFetchForgroundContentApi(uuid: beaconInfo["uuid"] as! String, major: "\(beaconInfo["major"] as! Int)", minor: "\(beaconInfo["minor"] as! Int)",  latitude: beaconInfo["latitude"] as! String, longitude:beaconInfo["longitude"] as! String) { (result) in
            var arrResult = [Any]();
            if let result = result{
                result.forEach({ (item) in
                    let dic = [
                        "bigtext" : item.bigtext,
                        "id" : item.id,
                        "image" : item.image,
                        "note1": item.note1,
                        "note2" :item.note2,
                        "title" : item.title,
                        "type" : item.type,
                        "url" :  item.url
                    ]
                    arrResult.append(dic)
                })
                resolve(arrResult as? NSArray)
            }
        }
    }
    
    @objc func fetchContentNotification(_ url: NSString, fetchContentWithResolve resolve: @escaping RCTPromiseResolveBlock, fetchContentWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else{
            reject("exception", self.errorMessage, nil);
            return
        }
        
        
        manager.callFetchForgroundContentApiForForgroundNotification(url: url as String) { (result) in
            var arrResult = [Any]();
            if let result = result{
                result.forEach({ (item) in
                    let dic = [
                        "bigtext" : item.bigtext,
                        "id" : item.id,
                        "image" : item.image,
                        "note1": item.note1,
                        "note2" :item.note2,
                        "title" : item.title,
                        "type" : item.type,
                        "url" :  item.url
                    ]
                    arrResult.append(dic)
                })
                resolve(arrResult as? NSArray)
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
        let domain = Bundle.main.bundleIdentifier!
        
        UserDefaults.standard.removePersistentDomain(forName: domain)
        UserDefaults.standard.synchronize()
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
        dicInfo.setValue(manager.isLocationBasedContentEnabled, forKey: "isLocationBasedContentEnabled")
        dicInfo.setValue(manager.isNotificationEnabled, forKey: "isNotificationEnabled")
        dicInfo.setValue(manager.isUserRegistered, forKey: "isUserRegistered")
        dicInfo.setValue("\(Int(manager.snoozeNotificationTimeInMinutes))", forKey: "snoozeNotificationTimeInMinutes")
        dicInfo.setValue("\(Int(manager.snoozeContentTimeInHours))", forKey: "snoozeContentTimeInHours")
        
        var arrTag = [Any]()
        if let tags = manager.userInfo?.tags{
            tags.forEach { (tag) in
                let dic = [
                    "name": tag.name ?? "",
                    "isSelected": tag.isSelected ?? false
                    ] as [String : Any]
                arrTag.append(dic)
            }
        }
        dicInfo.setValue(arrTag, forKey: "tags")
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
    
    @objc func setGeoLocationMode(_ enable: Bool){
        guard let manager = EngageSDK.shared else { return }
        manager.isLocationBasedContentEnabled = enable
    }
    
    /// Setup Beacon Moitor Block
    func setupBeaconMoitorBlock() {
        guard let manager = EngageSDK.shared else { return }
        manager.onBeaconCamped = { beacon, location in
            self.latestBeacon = beacon
            self.latestLocation = location
            print("Entry beacon \(beacon)")
            let beaconInfo = NSMutableDictionary();
            beaconInfo.setValue(location?.latitude.description, forKey: "latitude")
            beaconInfo.setValue(location?.longitude.description, forKey: "longitude")
            beaconInfo.setValue(beacon.beacon.major, forKey: "major")
            beaconInfo.setValue(beacon.beacon.minor, forKey: "minor")
            beaconInfo.setValue(beacon.rssi, forKey: "rssi")
            beaconInfo.setValue("\(beacon.beacon.proximityUUID)", forKey: "uuid")
            self.sendEvent(withName: "onBeaconEnter", body: beaconInfo)
        }
        
        manager.onBeaconExit = { beacon, location in
            self.latestBeacon = beacon
            self.latestLocation = location
            print("Exit beacon \(beacon)")
            let beaconInfo = NSMutableDictionary();
            beaconInfo.setValue(location?.latitude.description, forKey: "latitude")
            beaconInfo.setValue(location?.longitude.description, forKey: "longitude")
            beaconInfo.setValue(beacon.beacon.major, forKey: "major")
            beaconInfo.setValue(beacon.beacon.minor, forKey: "minor")
            beaconInfo.setValue(beacon.rssi, forKey: "rssi")
            beaconInfo.setValue("\(beacon.beacon.proximityUUID)", forKey: "uuid")
            self.sendEvent(withName: "onBeaconExit", body: beaconInfo)
        }
        manager.onRangedBeacon = { beacons in
            print("Ranged beacons \(beacons)")
        }
        
        manager.onRuleTriggeres = { rule, location in
            print("Rule triggeres \(rule)")
        }
        
        manager.onLocationRuleTriggeres = { rule, location in
            var locationInfo: [String: Any] = [:]
            locationInfo["latitude"] = location?.latitude.description
            locationInfo["longitude"] = location?.longitude.description
            self.sendEvent(withName: "onBeaconLocation", body: locationInfo)
            print("Rule triggeres \(rule)")
        }
        manager.onPermissionChange = { (message, permission) in
            if !permission {
                
            }
        }
        manager.onScanStopped = {
            self.sendEvent(withName: "onStopScan", body: "")
        }
        
        manager.locationCheckLiveData = { location in
            var locationInfo: [String: Any] = [:]
            locationInfo["latitude"] = location?.latitude.description
            locationInfo["longitude"] = location?.longitude.description
            self.sendEvent(withName: "onLocationChange", body: locationInfo)
        }
        
        manager.onPromotion = { promotion in
            manager.callPromotionApi(id: promotion.meta?.params?.id ?? "", responseData: { (response) in
                if let response = response{
                    let data = [ "bigtext" : response.bigtext,
                                 "id" : response.id,
                                 "image" : response.image,
                                 "note1": response.note1,
                                 "note2" :response.note2,
                                 "title" : response.title,
                                 "type" : response.type,
                                 "description": response.description ?? "",
                                 "url" :  response.url]
                    //resolve(data)
                }else{
                    // reject("exception", self.errorMessage, nil);
                }
            })
        }
    }
    
    @objc func callPushNotificationRegister(_ fcmToken: String, registerPushWithResolve resolve: @escaping RCTPromiseResolveBlock, registerPushWithReject reject: @escaping RCTPromiseRejectBlock){
        guard let manager = EngageSDK.shared else {
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.callPushNotificationRegister(pushToken: fcmToken) { (response) in
            resolve(response)
        }
    }
    
    @objc func getContentForActions(_ userInfo: NSDictionary?, onPromitionWithResolve resolve : @escaping RCTPromiseResolveBlock, onPromitionWithReject reject: @escaping RCTPromiseRejectBlock){
        
        guard let manager = EngageSDK.shared, let userInfo = userInfo else {
            reject("exception", self.errorMessage, nil);
            return
        }
        manager.onPromotion = { promotion in
            manager.callPromotionApi(id: promotion.meta?.params?.id ?? "", responseData: { (response) in
                if let response = response{
                    let data = [ "bigtext" : response.bigtext,
                                 "id" : response.id,
                                 "image" : response.image,
                                 "note1": response.note1,
                                 "note2" :response.note2,
                                 "title" : response.title,
                                 "type" : response.type,
                                 "url" :  response.url]
                    resolve(data)
                }else{
                    reject("exception", self.errorMessage, nil);
                }
            })
        }
        manager.handleNotification(userInfo: userInfo as! [AnyHashable : Any]) {
            print("handleNotification")
        }
    }
    
    @objc func logEvent(_ logType: String, ContentId contentId: String, ContenType contentType: String, Param2 param2:String){
        guard let manager = EngageSDK.shared else {
            return
        }
        manager.callLogEvent(logType: LogType.init(rawValue: logType) ?? .details, contentId: contentId, contentType: contentType, param2: param2, beacon: latestBeacon, location: latestLocation) { (response) in
            print("Log event : \(logType)",response ?? "")
        }
    }
    
    @objc func logNotificationEvent(_ notificationId: String, Action action: String){
        guard let manager = EngageSDK.shared else {
            return
        }
        manager.callLogPushNotification(notificationId: notificationId, action: action) { (response) in
            print("Log Notification event",response ?? "")
        }
    }
    
    @objc func setSnoozeNotifications(_ notificationMin: String){
        guard let manager = EngageSDK.shared else {
            return
        }
        manager.snoozeNotificationTimeInMinutes = Double(notificationMin) ?? 0
    }
    
    @objc func setSnoozeContent(_ notificationHour: String){
        guard let manager = EngageSDK.shared else {
            return
        }
        manager.snoozeContentTimeInHours = Double(notificationHour) ?? 0
    }
    
    //Note that any event name used in sendEvent above needs to be in this array.
    override func supportedEvents() -> [String]! {
        return ["Engage", "onBeaconEnter", "onBeaconExit", "onBeaconLocation", "onLocationChange","onStopScan", "onPromotion"]
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
