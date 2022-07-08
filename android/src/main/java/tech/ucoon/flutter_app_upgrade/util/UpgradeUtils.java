package tech.ucoon.flutter_app_upgrade.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * 工具类
 */
public class UpgradeUtils {

    /**
     * 是否是主线程
     *
     * @return 是否是主线程
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * 根据下载地址获取文件名
     *
     * @param downloadUrl
     * @return
     */
    @NonNull
    public static String getApkNameByDownloadUrl(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return "temp_" + System.currentTimeMillis() + ".apk";
        } else {
            String appName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
            if (!appName.endsWith(".apk")) {
                appName = "temp_" + System.currentTimeMillis() + ".apk";
            }
            return appName;
        }
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    @Nullable
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取应用的缓存目录
     *
     * @param uniqueName 缓存目录
     */
    public static String getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (isSDCardEnable() && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath + File.separator + uniqueName;
    }

    private static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final Context context, final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExists(context, file.getAbsolutePath());
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final Context context, final String filePath) {
        File file = getFileByPath(filePath);
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExistsApi29(context, filePath);
    }

    /**
     * Android 10判断文件是否存在的方法
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    private static boolean isFileExistsApi29(Context context, String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AssetFileDescriptor afd = null;
            try {
                Uri uri = Uri.parse(filePath);
                afd = openAssetFileDescriptor(context, uri);
                if (afd == null) {
                    return false;
                } else {
                    closeIOQuietly(afd);
                }
            } catch (FileNotFoundException e) {
                return false;
            } finally {
                closeIOQuietly(afd);
            }
            return true;
        }
        return false;
    }

    /**
     * 从uri资源符中读取文件描述
     *
     * @param uri 文本资源符
     * @return AssetFileDescriptor
     */
    public static AssetFileDescriptor openAssetFileDescriptor(Context context, Uri uri) throws FileNotFoundException {
        return context.getContentResolver().openAssetFileDescriptor(uri, "r");
    }

    /**
     * 安静关闭 IO
     *
     * @param closeables closeables
     */
    public static void closeIOQuietly(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
