package tech.ucoon.flutter_app_upgrade;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import tech.ucoon.flutter_app_upgrade.entity.UpdateEntity;
import tech.ucoon.flutter_app_upgrade.upgrade.AppUpgradeKit;
import tech.ucoon.flutter_app_upgrade.upgrade.ChannelConstants;

/**
 * FlutterAppUpgradePlugin
 */
public class FlutterAppUpgradePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, EventChannel.StreamHandler {
    private Activity mContext;
    private AppUpgradeKit mAppUpgradeKit;
    private MethodChannel mMethodChannel;
    private EventChannel mEventChannel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        mMethodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_app_upgrade_method");
        mEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_app_upgrade_event");
        mMethodChannel.setMethodCallHandler(this);
        mEventChannel.setStreamHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        String method = call.method;
        switch (method) {
            case ChannelConstants.DOWNLOAD_APK_INSTALL:
                String downloadUrl = call.argument("downloadUrl");
                String versionName = call.argument("versionName");
                UpdateEntity updateEntity = new UpdateEntity();
                updateEntity.setDownloadUrl(downloadUrl);
                updateEntity.setVersionName(versionName);
                mAppUpgradeKit.startDownloadService(updateEntity);
                break;
            case ChannelConstants.PATCH_INSTALL:
                String patchPath = call.argument("patchPath");
                mAppUpgradeKit.patchInstall(patchPath);
                break;
            case ChannelConstants.GO_TO_MARKET:
                mAppUpgradeKit.goToMarket();
            case ChannelConstants.GO_TO_GOOGLE_MARKET:
                String packageId = call.argument("packageId");
                mAppUpgradeKit.goToGoogleMarket(packageId);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        mMethodChannel.setMethodCallHandler(null);
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
        if (requestCode == AppUpgradeKit.REQUEST_UNKNOWN_CODE) {
            mAppUpgradeKit.install(mAppUpgradeKit.appDownloadPath);
            return true;
        }
        return false;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        mAppUpgradeKit.setEventSink(events);
    }

    @Override
    public void onCancel(Object arguments) {
        mAppUpgradeKit.setEventSink(null);
    }
}
