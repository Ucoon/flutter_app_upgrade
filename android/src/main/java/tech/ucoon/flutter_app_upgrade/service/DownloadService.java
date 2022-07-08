package tech.ucoon.flutter_app_upgrade.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import tech.ucoon.flutter_app_upgrade.entity.UpdateEntity;
import tech.ucoon.flutter_app_upgrade.util.UpgradeUtils;

/**
 * APK 下载服务
 */
public class DownloadService extends Service {
    private static final String TAG = DownloadService.class.getSimpleName();

    private static boolean mRunning = false;

    public static void bindService(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mRunning = true;
    }

    /**
     * 停止下载服务
     */
    private void stop() {
        close();
    }

    /**
     * 关闭服务
     */
    private void close() {
        mRunning = false;
        stopSelf();
    }

    /**
     * 下载服务是否在运行
     *
     * @return 是否在运行
     */
    public static boolean isRunning() {
        return mRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mRunning = true;
        return new DownloadBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mRunning = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class DownloadBinder extends Binder {
        private FileDownloadCallBack mFileDownloadCallBack;
        private UpdateEntity mUpdateEntity;

        /**
         * 开始下载
         *
         * @param updateEntity     app信息
         * @param downloadListener 下载监听
         */
        public void start(@NonNull UpdateEntity updateEntity, @NonNull OnFileDownloadListener downloadListener) {
            mUpdateEntity = updateEntity;
            startDownload(updateEntity, mFileDownloadCallBack = new FileDownloadCallBack(downloadListener));
        }

        /**
         * 停止下载服务
         */
        public void stop() {
            if (mFileDownloadCallBack != null) {
                mFileDownloadCallBack.onCancel();
                mFileDownloadCallBack = null;
            }
            OkHttpUpdateHttpService.cancelDownload(mUpdateEntity.getDownloadUrl());
            DownloadService.this.stop();
        }
    }

    private void startDownload(@NonNull UpdateEntity updateEntity, @NonNull FileDownloadCallBack fileDownloadCallBack) {
        String apkUrl = updateEntity.getDownloadUrl();
        if (TextUtils.isEmpty(apkUrl)) {
            fileDownloadCallBack.dispatchOnError(new Throwable("apk url Can not be empty"));
            stop();
            return;
        }
        String apkName = UpgradeUtils.getApkNameByDownloadUrl(apkUrl);
        File apkCacheDir = UpgradeUtils.getFileByPath(UpgradeUtils.getDiskCacheDir(DownloadService.this, "kooboo_upgrade"));
        try {
            if (!UpgradeUtils.isFileExists(DownloadService.this, apkCacheDir)) {
                boolean mkdir = apkCacheDir.mkdirs();
                if (!mkdir) return;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        String target = apkCacheDir + File.separator + updateEntity.getVersionName();
        OkHttpUpdateHttpService.download(apkUrl, target, apkName, fileDownloadCallBack);
    }


    /**
     * 文件下载处理
     */
    private class FileDownloadCallBack implements OkHttpUpdateHttpService.DownloadCallback {

        /**
         * 文件下载监听
         */
        private OnFileDownloadListener mOnFileDownloadListener;


        private boolean mCancel;

        private final Handler mMainHandler;

        FileDownloadCallBack(@NonNull OnFileDownloadListener listener) {
            this.mOnFileDownloadListener = listener;
            this.mMainHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void onStart() {
            if (mCancel) return;
            dispatchOnStart();
        }

        @Override
        public void onProgress(float progress, long total) {
            if (mCancel) return;
            dispatchOnProgress(progress, total);
        }

        @Override
        public void onSuccess(File file) {
            if (UpgradeUtils.isMainThread()) {
                handleOnSuccess(file);
            } else {
                mMainHandler.post(() -> handleOnSuccess(file));
            }
        }

        @Override
        public void onError(Throwable throwable) {
            if (mCancel) return;
            dispatchOnError(throwable);
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void dispatchOnStart() {
            if (UpgradeUtils.isMainThread()) {
                if (mOnFileDownloadListener != null) {
                    mOnFileDownloadListener.onStart();
                }
            } else {
                mMainHandler.post(() -> {
                    if (mOnFileDownloadListener != null) {
                        mOnFileDownloadListener.onStart();
                    }
                });
            }
        }

        private void dispatchOnProgress(final float progress, final long total) {
            if (UpgradeUtils.isMainThread()) {
                if (mOnFileDownloadListener != null) {
                    mOnFileDownloadListener.onProgress(progress, total);
                }
            } else {
                mMainHandler.post(() -> {
                    if (mOnFileDownloadListener != null) {
                        mOnFileDownloadListener.onProgress(progress, total);
                    }
                });
            }
        }

        private void handleOnSuccess(File file) {
            if (mCancel) return;
            if (mOnFileDownloadListener != null) {
                mOnFileDownloadListener.onCompleted(file);
            }
            Log.d(TAG, "更新文件下载完成, 文件路径: " + file.getAbsolutePath());
            close();
        }

        private void dispatchOnError(final Throwable throwable) {
            if (UpgradeUtils.isMainThread()) {
                if (mOnFileDownloadListener != null) {
                    mOnFileDownloadListener.onError(throwable);
                }
            } else {
                mMainHandler.post(() -> {
                    if (mOnFileDownloadListener != null) {
                        mOnFileDownloadListener.onError(throwable);
                    }
                });
            }
        }

        /**
         * 取消下载
         */
        void onCancel() {
            mOnFileDownloadListener = null;
            mCancel = true;
        }
    }

}
