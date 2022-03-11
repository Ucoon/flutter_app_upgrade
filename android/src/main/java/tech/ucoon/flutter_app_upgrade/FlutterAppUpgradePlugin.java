package tech.ucoon.flutter_app_upgrade;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import tech.ucoon.flutter_app_upgrade.upgrade.AppUpgradeKit;
import tech.ucoon.flutter_app_upgrade.upgrade.ChannelConstants;

/**
 * FlutterAppUpgradePlugin
 */
public class FlutterAppUpgradePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
    private Activity mContext;
    private AppUpgradeKit mAppUpgradeKit;
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_app_upgrade");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        String method = call.method;
        switch (method) {
            case ChannelConstants.GET_APK_DOWNLOAD_PATH:
                result.success(mContext.getExternalFilesDir("").getAbsolutePath());
                break;
            case ChannelConstants.APK_INSTALL:
                String path = call.argument("path");
                mAppUpgradeKit.install(path);
                break;
            case ChannelConstants.PATCH_INSTALL:
                String patchPath = call.argument("patchPath");
                mAppUpgradeKit.patchInstall(patchPath);
                break;
            case ChannelConstants.GO_TO_MARKET:
                mAppUpgradeKit.goToMarket();
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        mContext = activityPluginBinding.getActivity();
        mAppUpgradeKit = new AppUpgradeKit(mContext);
        activityPluginBinding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppUpgradeKit.REQUEST_UNKNOWN_CODE){
            mAppUpgradeKit.install(mAppUpgradeKit.appDownloadPath);
            return true;
        }
        return false;
    }
}
