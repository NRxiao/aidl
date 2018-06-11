package com.example.android.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sz132 on 2018/4/16.
 */

public class Book implements Parcelable{
    private String name;
    private int code;

    protected Book(Parcel in) {
        name = in.readString();
        code = in.readInt();
    }

    public Book(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(code);
    }
}
