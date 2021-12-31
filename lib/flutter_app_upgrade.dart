import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAppUpgrade {
  static const MethodChannel _methodChannel =
      MethodChannel('flutter_app_upgrade');

  ///获取apk 下载路径(仅适用于Android)
  static Future<String> get apkDownloadPath async {
    return await _methodChannel.invokeMethod('getApkDownloadPath');
  }

  ///安装apk(仅适用于Android)
  static installAppForAndroid(String path) async {
    var map = {'path': path};
    return await _methodChannel.invokeMethod('install', map);
  }

  ///跳转AppStore(仅适用于iOS)
  static goToAppStore(String id) async {
    var map = {'id': id};
    return _methodChannel.invokeMethod('goToAppStore', map);
  }

  ///跳转应用市场(仅适用于Android)
  static goToMarket() async {
    return await _methodChannel.invokeMethod('goToMarket');
  }
}
