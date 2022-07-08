package tech.ucoon.flutter_app_upgrade.service;

import java.io.File;

/**
 * 下载服务下载监听
 */
public interface OnFileDownloadListener {
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
     * 下载完毕
     *
     * @param file 下载好的文件
     */
    void onCompleted(File file);

    /**
     * 错误回调
     *
     * @param throwable 错误提示
     */
    void onError(Throwable throwable);

}
