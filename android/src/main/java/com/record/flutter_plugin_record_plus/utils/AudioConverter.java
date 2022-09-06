package com.record.flutter_plugin_record_plus.utils;

import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;


public class AudioConverter {
    public static void convertedFile(File file, AudioFormat audioFormat, IConvertCallback callback) throws Exception {
//        showLoading("转换中...(时间可能较长，请耐心等待)");
        File convertedFile = getConvertedFile(file, audioFormat);
        String[] cmd = new String[]{"-y", "-i", file.getPath(),  convertedFile.getPath()};//源码
        FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (callback!=null){
                    callback.onSuccess(convertedFile);
                }
            }
        });


    }
    private static File getConvertedFile(File originalFile, AudioFormat format){
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format.getFormat());
        return new File(filePath);
    }
}
