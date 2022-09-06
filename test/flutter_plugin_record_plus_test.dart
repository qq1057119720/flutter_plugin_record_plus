import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_plugin_record_plus/flutter_plugin_record_plus.dart';
import 'package:flutter_plugin_record_plus/flutter_plugin_record_plus_platform_interface.dart';
import 'package:flutter_plugin_record_plus/flutter_plugin_record_plus_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterPluginRecordPlusPlatform 
    with MockPlatformInterfaceMixin
    implements FlutterPluginRecordPlusPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterPluginRecordPlusPlatform initialPlatform = FlutterPluginRecordPlusPlatform.instance;

  test('$MethodChannelFlutterPluginRecordPlus is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterPluginRecordPlus>());
  });

  test('getPlatformVersion', () async {
    FlutterPluginRecordPlus flutterPluginRecordPlusPlugin = FlutterPluginRecordPlus();
    MockFlutterPluginRecordPlusPlatform fakePlatform = MockFlutterPluginRecordPlusPlatform();
    FlutterPluginRecordPlusPlatform.instance = fakePlatform;
  
    expect(await flutterPluginRecordPlusPlugin.getPlatformVersion(), '42');
  });
}
