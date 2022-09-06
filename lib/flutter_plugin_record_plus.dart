
import 'flutter_plugin_record_plus_platform_interface.dart';

class FlutterPluginRecordPlus {
  Future<String?> getPlatformVersion() {
    return FlutterPluginRecordPlusPlatform.instance.getPlatformVersion();
  }
}
