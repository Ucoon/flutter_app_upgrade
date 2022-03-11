#include <jni.h>
#include <string>
#include <android/log.h>
#include <exception>
#include "patchUtil.h"
#include <jni.h>
#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL
Java_tech_ucoon_flutter_1app_1upgrade_upgrade_AppUpgradeKit_patch(JNIEnv *env, jclass clazz,
                                                                  jstring old_apk_path,
                                                                  jstring new_apk_path,
                                                                  jstring patch_path) {
    int argc = 4;
    char *ch[argc];
    ch[0] = (char *) "bspatch";
    ch[1] = const_cast<char *>(env->GetStringUTFChars(old_apk_path, 0));
    ch[2] = const_cast<char *>(env->GetStringUTFChars(new_apk_path, 0));
    ch[3] = const_cast<char *>(env->GetStringUTFChars(patch_path, 0));
    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "old_apk_path = %s ", ch[1]);
    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "new_apk_path = %s ", ch[2]);
    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "patch_path = %s ", ch[3]);
    int result = applyPatch(argc, ch);

    env->ReleaseStringUTFChars(old_apk_path, ch[1]);
    env->ReleaseStringUTFChars(new_apk_path, ch[2]);
    env->ReleaseStringUTFChars(patch_path, ch[3]);
    return result;
}



