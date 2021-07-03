package com.mi.song.time.bean;

import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by BarryAllen
 *
 * @TheBotBox boxforbot@gmail.com
 */
public class AppData implements Parcelable {

    public String mName;
    public String mPackageName;
    public long mEventTime;
    public long mUsageTime;
    public int mEventType;
    public int mCount;
    public boolean mCanOpen;
    public boolean mIsSystem;

    public AppData() {
    }

    protected AppData(Parcel in) {
        mName = in.readString();
        mPackageName = in.readString();
        mEventTime = in.readLong();
        mUsageTime = in.readLong();
        mEventType = in.readInt();
        mCount = in.readInt();
        mCanOpen = in.readByte() != 0;
        mIsSystem = in.readByte() != 0;
    }

    public static final Creator<AppData> CREATOR = new Creator<AppData>() {
        @Override
        public AppData createFromParcel(Parcel in) {
            return new AppData(in);
        }

        @Override
        public AppData[] newArray(int size) {
            return new AppData[size];
        }
    };

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "name:%s package_name:%s time:%d total:%d type:%d system:%b count:%d",
                mName, mPackageName, mEventTime, mUsageTime, mEventType, mIsSystem, mCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPackageName);
        dest.writeLong(mEventTime);
        dest.writeLong(mUsageTime);
        dest.writeInt(mEventType);
        dest.writeInt(mCount);
        dest.writeByte((byte) (mCanOpen ? 1 : 0));
        dest.writeByte((byte) (mIsSystem ? 1 : 0));
    }
}
