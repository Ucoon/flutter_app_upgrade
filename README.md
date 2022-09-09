# kooboo_flutter_app_upgrade

Android app应用内升级，iOS跳转至AppStore

安装：

```yaml
dependencies:
  flutter:
    sdk: flutter
  kooboo_flutter_app_upgrade: ^0.0.9
```

导入：

```dart
import 'package:flutter_app_upgrade/flutter_app_upgrade.dart';
```

使用：

```dart
///下载并安装apk(仅适用于Android)
FlutterAppUpgrade.downloadApkInstall('url','1.0.1');
///监听下载进度
FlutterAppUpgrade.onListenStreamData(
	(event) {
        debugPrint('_MyAppState.initState receive data $event');
	},
	onError: (error) {
        debugPrint('_MyAppState.initState receive error ${error.message}');
    },
);

///增量更新apk(仅适用于Android)
String apkDownloadPath =await FlutterAppUpgrade.apkDownloadPath;
FlutterAppUpgrade.patchInstallAppForAndroid('$apkDownloadPath/app-V1.0_2.0.patch');
///跳转应用市场(仅适用于Android)
FlutterAppUpgrade.goToMarket();
///跳转Google应用市场(仅适用于Android)
FlutterAppUpgrade.goToGoogleMarket();
///跳转AppStore(仅适用于iOS)
FlutterAppUpgrade.goToAppStore('appId');
```

参考：

[Android增量更新原理和实践](https://www.jianshu.com/p/9b0c10270759)
