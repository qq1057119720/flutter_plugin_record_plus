package com.record.flutter_plugin_record_plus;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.record.flutter_plugin_record_plus.utils.AudioConverter;
import com.record.flutter_plugin_record_plus.utils.AudioFormat;
import com.record.flutter_plugin_record_plus.utils.AudioHandler;
import com.record.flutter_plugin_record_plus.utils.IConvertCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * FlutterPluginRecordPlusPlugin
 */
public class FlutterPluginRecordPlusPlugin implements FlutterPlugin, MethodCallHandler,
        ActivityAware, PluginRegistry.RequestPermissionsResultListener, AudioHandler.RecordListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private Result result;
    private MethodCall call;
    private String voicePlayPath;
    private boolean recordMp3 = false;
    private AudioHandler audioHandler = null;
    private Activity activity;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_plugin_record_plus");
        channel.setMethodCallHandler(this);
    }

    private void initRecordMp3() {
        recordMp3 = true;
        checkPermission();
    }

    private void checkPermission() {
        PackageManager packageManager = activity.getPackageManager();
        boolean permission = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(Manifest.permission.RECORD_AUDIO, activity.getPackageName());
        if (permission) {
            initRecord();
        } else {
            initPermission();
        }
    }

    private void init() {
        recordMp3 = false;
        checkPermission();
    }

    private void initRecord() {
        if (audioHandler != null) {
            audioHandler.release();
            audioHandler = null;
        }
        audioHandler = AudioHandler.createHandler(AudioHandler.Frequency.F_22050);

//    Log.d("android voice  ", "init");
//    val id = call.argument<String>("id")
        String id = call.argument("id");
        Map<String, String> m1 = new HashMap<String, String>();
        m1.put("id", id);
        m1.put("result", "success");
//    m1["id"] = id!!
//            m1["result"] = "success"
        channel.invokeMethod("onInit", m1);

    }

    private void stop() {
        if (audioHandler != null) {
            if (audioHandler.isRecording()) {
                audioHandler.stopRecord();
            }
        }
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        this.call=call;
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("init")) {
            init();
        } else if (call.method.equals("initRecordMp3")) {
            initRecordMp3();
        } else if (call.method.equals("start")) {
            start();
        }  else if (call.method.equals("stop")) {
            stop();
        } else {
            result.notImplemented();
        }
    }



    private void start() {
        PackageManager packageManager = activity.getPackageManager();
        boolean permission = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(Manifest.permission.RECORD_AUDIO, activity.getPackageName());
        if (permission) {
            //        recorderUtil.startRecord();
            if (audioHandler.isRecording()) {
//            audioHandler?.startRecord(null);
                audioHandler.stopRecord();
            }
            audioHandler.startRecord(this);

            String id = call.argument("id");
            Map<String, String> m1 = new HashMap<String, String>();
            m1.put("id", id);
            m1.put("result", "success");
            channel.invokeMethod("onStart", m1);
        } else {
            checkPermission();
        }

    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    private void initActivityBinding(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        initActivityBinding(binding);

    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        initActivityBinding(binding);
    }

    @Override
    public void onDetachedFromActivity() {

    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return false;
    }

    //////


    @Override
    public void onStart() {

    }

    @Override
    public String getFilePath() {
        return null;
    }

    @Override
    public void onVolume(double db) {
        String id = call.argument("id");
        Map<String, Object> m1 = new HashMap<String, Object>();
        m1.put("id", id);
        m1.put("amplitude", db / 100);
        m1.put("result", "success");
        activity.runOnUiThread(() -> {
            channel.invokeMethod("onAmplitude", m1);
        });
    }

    @Override
    public void onStop(File recordFile, Double audioTime) throws Exception {
        if (recordFile != null) {
            voicePlayPath = recordFile.getPath();
            if (recordMp3) {

                AudioConverter.convertedFile(recordFile, AudioFormat.MP3, new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {
                        String id = call.argument("id");
                        Map<String, String> m1 = new HashMap<String, String>();
                        m1.put("id", id);
                        m1.put("audioTimeLength", audioTime.toString());
                        m1.put("voicePath", convertedFile.getPath());
                        m1.put("result", "success");
                        activity.runOnUiThread(() -> {
                            channel.invokeMethod("onStop", m1);
                        });

                    }

                    @Override
                    public void onFailure(Exception error) {

                    }
                });

            } else {

                String id = call.argument("id");
                Map<String, String> m1 = new HashMap<String, String>();
                m1.put("id", id);
                m1.put("audioTimeLength", audioTime.toString());
                m1.put("voicePath", voicePlayPath);
                m1.put("result", "success");
                activity.runOnUiThread(() -> {
                    channel.invokeMethod("onStop", m1);
                });
            }
        }
    }

    @Override
    public void onError(int error) {

    }
}
