package com.zxm.easymqttclient.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZhangXinmin on 2019/12/5.
 * Copyright (c) 2019 . All rights reserved.
 */
public final class LogEntity implements Parcelable {

    private String tag;
    private String message;
    private long timeMills;

    public LogEntity() {
    }

    protected LogEntity(Parcel in) {
        tag = in.readString();
        message = in.readString();
        timeMills = in.readLong();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(long timeMills) {
        this.timeMills = timeMills;
    }

    public static final Creator<LogEntity> CREATOR = new Creator<LogEntity>() {
        @Override
        public LogEntity createFromParcel(Parcel in) {
            return new LogEntity(in);
        }

        @Override
        public LogEntity[] newArray(int size) {
            return new LogEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tag);
        dest.writeString(message);
        dest.writeLong(timeMills);
    }

    @Override
    public String toString() {
        return "LogEntity{" +
                "tag='" + tag + '\'' +
                ", message='" + message + '\'' +
                ", timeMills=" + timeMills +
                '}';
    }
}
