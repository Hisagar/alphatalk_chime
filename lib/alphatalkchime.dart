import 'dart:async';

import 'package:flutter/services.dart';

class Alphatalkchime {
  static const MethodChannel _channel =
      const MethodChannel('alphatalkchime');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  static Future<String> startvideocall(String meetId,String userName,bool isVideoCall) async {
    final String version = await _channel.invokeMethod('startVideoCall',{"meetId":meetId,"userName":userName,"isVideoCall":isVideoCall});
    return version;
  }
}