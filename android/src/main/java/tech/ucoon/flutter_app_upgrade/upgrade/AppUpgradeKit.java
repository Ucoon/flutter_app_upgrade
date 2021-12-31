package tech.ucoon.flutter_app_upgrade.upgrade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * app升级安装工具类
 */
public class AppUpgradeKit {
    public static final int REQUEST_UNKNOWN_CODE = 10086;
    private Activity mContext;
    public String appDownloadPath;

    public AppUpgradeKit(Activity context) {
        mContext = context;
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

    /**
     * 安装app, Android 7.0以下
     *
     * @param context
     * @param path
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
     * @param context
     * @param path
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallO(Activity context, String path) {
        boolean isGranted = context.getPackageManager().canRequestPackageInstalls();
        if (isGranted) {
            startInstallN(context, path);
        } else {
            OpenUnKnownSettingDialog dialog = new OpenUnKnownSettingDialog(context, "安装应用需要打开未知来源权限，请去设置中开启权限");
            dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        Uri uri = Uri.parse("package:" + packageInfo.packageName);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
                        context.startActivityForResult(intent, REQUEST_UNKNOWN_CODE);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
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
}
