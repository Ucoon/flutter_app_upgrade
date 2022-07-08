package tech.ucoon.flutter_app_upgrade.service;

import androidx.annotation.NonNull;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import okhttp3.Request;

import java.io.File;

/**
 * 使用okhttp下载文件
 */
public class OkHttpUpdateHttpService {
    public static void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull DownloadCallback callback) {
        OkHttpUtils.get()
                .url(url)
                .tag(url)
                .build()
                .execute(new FileCallBack(path, fileName) {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        callback.onStart();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        callback.onProgress(progress, total);
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        callback.onError(e);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        callback.onSuccess(response);
                    }

                });
    }

    public static void cancelDownload(@NonNull String url) {
        OkHttpUtils.getInstance().cancelTag(url);
    }


    /**
     * 下载回调
     */
    interface DownloadCallback {
        /**
         * 下载开始
         */
        void onStart();

        /**
         * 更新进度
         *
         * @param progress 进度
         * @param total    文件总大小，单位字节
         */
        void onProgress(float progress, long total);

        /**
         * 结果回调
         *
         * @param file 下载好的文件
         */
        void onSuccess(File file);

        /**
         * 错误回调
         *
         * @param throwable 错误提示
         */
        void onError(Throwable throwable);

    }

}



