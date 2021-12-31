import 'package:flutter/material.dart';
import 'package:flutter_app_upgrade/flutter_app_upgrade.dart';
import 'package:fluttertoast/fluttertoast.dart';

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
                String apkDownloadPath =
                    await FlutterAppUpgrade.apkDownloadPath;
                Fluttertoast.showToast(msg: apkDownloadPath);
              },
              child: const Text('获取apk安装路径(仅适用于Android)'),
            ),
            const SizedBox(
              height: 10,
            ),
            TextButton(
              onPressed: () async {
                String apkDownloadPath =
                    await FlutterAppUpgrade.apkDownloadPath;
                FlutterAppUpgrade.installAppForAndroid(
                    '$apkDownloadPath/temp.apk');
              },
              child: const Text('安装已下载的Apk(仅适用于Android)'),
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
