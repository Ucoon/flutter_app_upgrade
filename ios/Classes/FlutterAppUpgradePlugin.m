#import "FlutterAppUpgradePlugin.h"
#if __has_include(<kooboo_flutter_app_upgrade/kooboo_flutter_app_upgrade-Swift.h>)
#import <kooboo_flutter_app_upgrade/kooboo_flutter_app_upgrade-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "kooboo_flutter_app_upgrade-Swift.h"
#endif

@implementation FlutterAppUpgradePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterAppUpgradePlugin registerWithRegistrar:registrar];
}
@end
