package com.record.flutter_plugin_record_plus.utils;

import java.io.File;

public interface IConvertCallback {

    void onSuccess(File convertedFile);

    void onFailure(Exception error);

}