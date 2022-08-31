package tech.ucoon.flutter_app_upgrade.upgrade;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import tech.ucoon.flutter_app_upgrade.entity.UpdateEntity;
import tech.ucoon.flutter_app_upgrade.service.DownloadService;
import tech.ucoon.flutter_app_upgrade.service.OnFileDownloadListener;

/**
 * app升级安装工具类
 */
public class AppUpgradeKit {
    public static final int REQUEST_UNKNOWN_CODE = 10086;
    private final Activity mContext;
    public String appDownloadPath;
    private DownloadService.DownloadBinder mDownloadBinder;
    private EventChannel.EventSink mEventSink;

    /**
     * 服务绑定连接
     */
    private ServiceConnection mServiceConnection;

    /**
     * 是否已经绑定下载服务
     */
    private boolean mBound;

    public AppUpgradeKit(Activity context) {
        this.mContext = context;
    }

    public void setEventSink(EventChannel.EventSink mEventSink) {
        this.mEventSink = mEventSink;
    }

    private void sendEventToStream(Map<String, Object> data) {
        new Handler(Looper.getMainLooper()).post(() -> {
            mEventSink.success(data);
        });
    }

    private void sendErrorEventToStream(String errorCode, Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            mEventSink.error(errorCode, throwable.getMessage(), throwable);
        });
    }

    public void startDownloadService(@NonNull final UpdateEntity updateEntity) {
        DownloadService.bindService(mContext, mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBound = true;
                startDownload((DownloadService.DownloadBinder) service, updateEntity);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
            }
        });
    }

    private void startDownload(DownloadService.DownloadBinder downloadBinder, @NonNull final UpdateEntity updateEntity) {
        mDownloadBinder = downloadBinder;
        mDownloadBinder.start(updateEntity, new OnFileDownloadListener() {
            @Override
            public void onStart() {
                Map<String, Object> data = new HashMap<>();
                data.put("msg", "start download");
                sendEventToStream(data);
            }

            @Override
            public void onProgress(float progress, long total) {
                Map<String, Object> data = new HashMap<>();
                data.put("progress", progress);
                data.put("total", total);
                sendEventToStream(data);
            }

            @Override
            public void onCompleted(File file) {
                Map<String, Object> data = new HashMap<>();
                data.put("msg", "download complete");
                data.put("filePath", file.getAbsolutePath());
                sendEventToStream(data);
                install(file.getAbsolutePath());
                cancelDownload();
            }

            @Override
            public void onError(Throwable throwable) {
                sendErrorEventToStream("downloadErrorCode", throwable);
                cancelDownload();
            }
        });
    }

    private void cancelDownload() {
        if (mDownloadBinder != null) {
            mDownloadBinder.stop();
        }
        if (mBound && mServiceConnection != null) {
            mContext.unbindService(mServiceConnection);
            mBound = false;
            mServiceConnection = null;
        }
    }

    public void install(String path) {
        appDownloadPath = path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startInstallO(mContext, path);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startInstallN(mContext, path);
        } else {
            startInstall(mContext, path);
        }
    }

    public void patchInstall(String patchPath) {
        String newApkPath = mContext.getExternalFilesDir("").getAbsolutePath() + "/new.apk";
        int result = patch(mContext.getApplicationInfo().sourceDir, newApkPath, patchPath);
        if (result == 0) {
            install(newApkPath);
        }
    }

    /**
     * 安装app, Android 7.0以下
     *
     * @param context 上下文
     * @param path    apk文件路径
     */
    private static void startInstall(Context context, String path) {
        if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装app, Android 7.0及以上
     *
     * @param context 上下文
     * @param path    apk文件路径
     */
    private static void startInstallN(Context context, String path) {
        if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri installUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(installUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装app, Android 8.0及以上
     *
     * @param context 上下文
     * @param path    apk文件路径
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallO(Context context, String path) {
        boolean isGranted = context.getPackageManager().canRequestPackageInstalls();
        if (isGranted) {
            startInstallN(context, path);
        } else {
            OpenUnKnownSettingDialog dialog = new OpenUnKnownSettingDialog(context, "安装应用需要打开未知来源权限，请去设置中开启权限");
            dialog.setOnClickListener(v -> {
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    Uri uri = Uri.parse("package:" + packageInfo.packageName);
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, REQUEST_UNKNOWN_CODE);
                    } else {
                        context.startActivity(intent);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            });
            dialog.show();
        }
    }

    /**
     * 跳转应用市场安装app
     * 如果手机上安装多个应用市场则弹出对话框，由用户选择进入哪个市场
     */
    public void goToMarket() {
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            Uri uri = Uri.parse("market://details?id=" + packageInfo.packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mContext.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "您的手机没有安装应用商店", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到谷歌应用市场
     */
    public void goToGoogleMarket(String packageId) {
        try {
            String _packageId = packageId;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (TextUtils.isEmpty(_packageId)) {
                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                _packageId = packageInfo.packageName;
            }
            intent.setData(Uri.parse("market://details?id=" + _packageId));
            intent.setPackage("com.android.vending");//这里对应的是谷歌商店，跳转别的商店改成对应的即可
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            } else {//没有应用市场，通过浏览器跳转到Google Play
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + _packageId));
                if (intent2.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static {
        System.loadLibrary("diff-update");
    }

    /**
     * native方法 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
     *
     * @param oldApkPath 旧的Apk文件路径
     * @param newApkPath 新的Apk文件路径
     * @param patchPath  补丁包文件路径
     * @return 0: 操作成功
     */
    public static native int patch(String oldApkPath, String newApkPath, String patchPath);
}
