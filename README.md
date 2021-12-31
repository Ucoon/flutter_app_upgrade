# kooboo_flutter_app_upgrade

Android app应用内升级，iOS跳转至AppStore

安装：

```yaml
dependencies:
  flutter:
    sdk: flutter
  kooboo_flutter_app_upgrade: ^0.0.2
```

导入：

```dart
import 'package:flutter_app_upgrade/flutter_app_upgrade.dart';
```

使用：

```dart
///安装apk(仅适用于Android)
String apkDownloadPath = await FlutterAppUpgrade.apkDownloadPath;
FlutterAppUpgrade.installAppForAndroid('$apkDownloadPath/temp.apk');
///跳转应用市场(仅适用于Android)
FlutterAppUpgrade.goToMarket();
///跳转AppStore(仅适用于iOS)
FlutterAppUpgrade.goToAppStore('appId');
```
