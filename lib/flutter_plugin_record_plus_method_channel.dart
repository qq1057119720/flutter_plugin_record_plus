import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_plugin_record_plus_platform_interface.dart';

/// An implementation of [FlutterPluginRecordPlusPlatform] that uses method channels.
class MethodChannelFlutterPluginRecordPlus extends FlutterPluginRecordPlusPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_plugin_record_plus');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
