import Flutter
import UIKit

public class SwiftFlutterAppUpgradePlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_app_upgrade", binaryMessenger: registrar.messenger())
        let instance = SwiftFlutterAppUpgradePlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if(call.method=="goToAppStore"){
            if let args = call.arguments as? Dictionary<String, Any>,
               let appId = args["id"] as? String{
                self.goToAppStore(appId)
                result(nil)
            } else {
                result(FlutterError.init(code: "error", message: "data or format error", details: nil))
            }
        }
    }
    
    func goToAppStore(_ appId:String) {
        let url = URL(string: "itms-apps://itunes.apple.com/app/id\(appId)")
        if !UIApplication.shared.canOpenURL(url!) {
            return
        }
        if #available(iOS 10.0, *) {
            UIApplication.shared.open(url!, options: [:]) { (success) in
                print("goToAppStore \(success)")
            }
        } else {
            let success = UIApplication.shared.openURL(url!)
            print("goToAppStore \(success)")
        }
    }
}
