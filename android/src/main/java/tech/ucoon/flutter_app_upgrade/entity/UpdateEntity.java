package tech.ucoon.flutter_app_upgrade.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateEntity implements Parcelable {
    /**
     * 下载地址
     */
    private String mDownloadUrl;

    /**
     * 版本号
     */
    private int mVersionCode;
    /**
     * 版本名称
     */
    private String mVersionName;

    public UpdateEntity() {
    }

    protected UpdateEntity(Parcel in) {
        mDownloadUrl = in.readString();
        mVersionCode = in.readInt();
        mVersionName = in.readString();
    }

    public static final Creator<UpdateEntity> CREATOR = new Creator<UpdateEntity>() {
        @Override
        public UpdateEntity createFromParcel(Parcel in) {
            return new UpdateEntity(in);
        }

        @Override
        public UpdateEntity[] newArray(int size) {
            return new UpdateEntity[size];
        }
    };

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.mDownloadUrl = downloadUrl;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public void setVersionCode(int versionCode) {
        this.mVersionCode = versionCode;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public void setVersionName(String versionName) {
        this.mVersionName = versionName;
    }

    @Override
    public String toString() {
        return "UpdateEntity{" +
                "mDownloadUrl='" + mDownloadUrl + '\'' +
                ", mVersionCode=" + mVersionCode +
                ", mVersionName='" + mVersionName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDownloadUrl);
        dest.writeInt(mVersionCode);
        dest.writeString(mVersionName);
    }
}
