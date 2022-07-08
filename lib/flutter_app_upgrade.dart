import 'package:flutter/services.dart';

class FlutterAppUpgrade {
  static const MethodChannel _methodChannel =
      MethodChannel('flutter_app_upgrade_method');

  static const EventChannel _eventChannel = EventChannel('flutter_app_upgrade_event');

  ///监听下载进度
  static void onListenStreamData(Function onEvent, {Function? onError}) {
    _eventChannel
        .receiveBroadcastStream()
        .listen((event) {
      onEvent.call(event);
    }, onError: onError);
  }

  ///下载并安装apk(仅适用于Android)
  static downloadApkInstall(String downloadUrl, String versionName) async {
    var map = {'downloadUrl': downloadUrl, 'versionName': versionName};
    return await _methodChannel.invokeMethod('downloadApkInstall', map);
  }

  ///增量更新apk(仅适用于Android)
  static patchInstallAppForAndroid(String patchPath) async {
    var map = {'patchPath': patchPath};
    return await _methodChannel.invokeMethod('patchInstall', map);
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

  ///跳转Google应用市场(仅适用于Android)
  static goToGoogleMarket() async {
    return await _methodChannel.invokeMethod('goToGoogleMarket');
  }
}
