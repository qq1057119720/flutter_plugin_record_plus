import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_plugin_record_plus_method_channel.dart';

abstract class FlutterPluginRecordPlusPlatform extends PlatformInterface {
  /// Constructs a FlutterPluginRecordPlusPlatform.
  FlutterPluginRecordPlusPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterPluginRecordPlusPlatform _instance = MethodChannelFlutterPluginRecordPlus();

  /// The default instance of [FlutterPluginRecordPlusPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterPluginRecordPlus].
  static FlutterPluginRecordPlusPlatform get instance => _instance;
  
  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterPluginRecordPlusPlatform] when
  /// they register themselves.
  static set instance(FlutterPluginRecordPlusPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
