import 'package:flutter/material.dart';
import 'package:flutter_app_upgrade/flutter_app_upgrade.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance?.addPostFrameCallback((timeStamp) {
      FlutterAppUpgrade.onListenStreamData(
        (event) {
          if (event is Map && event.containsKey('progress')) {
            debugPrint(
                '_MyAppState.initState progress ${event['progress'] * 100}');
          } else {
            debugPrint('_MyAppState.initState receive data $event');
          }
        },
        onError: (error) {
          debugPrint('_MyAppState.initState receive error ${error.message}');
        },
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            TextButton(
              onPressed: () async {
                FlutterAppUpgrade.downloadApkInstall(
                    'http://video.kooboo.cn:5022/uploadAPK/apk/20220530/030cb2a6-1ce8-479b-8ce6-f88793677d2c.apk',
                    '1.0.1');
              },
              child: const Text('下载并安装Apk(仅适用于Android)'),
            ),
            const SizedBox(
              height: 10,
            ),
            TextButton(
              onPressed: () async {
                FlutterAppUpgrade.patchInstallAppForAndroid(
                    'app-V1.0_2.0.patch');
              },
              child: const Text('增量更新Apk(仅适用于Android)'),
            ),
            const SizedBox(
              height: 10,
            ),
            TextButton(
              onPressed: () async {
                FlutterAppUpgrade.goToMarket();
              },
              child: const Text('跳转应用市场(仅适用于Android)'),
            ),
            const SizedBox(
              height: 10,
            ),
            TextButton(
              onPressed: () async {
                FlutterAppUpgrade.goToAppStore('432274380');
              },
              child: const Text('跳转AppStore(仅适用于iOS)'),
            ),
            const SizedBox(
              height: 10,
            ),
          ],
        ),
      ),
    );
  }
}
