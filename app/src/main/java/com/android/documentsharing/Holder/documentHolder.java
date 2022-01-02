package com.android.documentsharing.Holder;

import android.os.Parcel;
import android.os.Parcelable;
@SuppressWarnings("ALL")
public class documentHolder implements Parcelable {
    private String name,extension,size,date,time,uid,ownerName,nodeKey,receiverName,url;
    private boolean access;
    private boolean New=false;
    public documentHolder() {

    }

    public documentHolder(String name, String extension, String size, String date, String time, String uid, String ownerName, String nodeKey) {
        this.name = name;
        this.extension = extension;
        this.size = size;
        this.date = date;
        this.time = time;
        this.uid = uid;
        this.ownerName = ownerName;
        this.nodeKey = nodeKey;
    }

    protected documentHolder(Parcel in) {
        name = in.readString();
        extension = in.readString();
        size = in.readString();
        date = in.readString();
        time = in.readString();
        uid = in.readString();
        ownerName = in.readString();
        nodeKey = in.readString();
        receiverName = in.readString();
        url = in.readString();
        access = in.readByte() != 0;
    }

    public static final Creator<documentHolder> CREATOR = new Creator<documentHolder>() {
        @Override
        public documentHolder createFromParcel(Parcel in) {
            return new documentHolder(in);
        }

        @Override
        public documentHolder[] newArray(int size) {
            return new documentHolder[ size ];
        }
    };

    public boolean isNew() {
        return New;
    }

    public void setNew(boolean aNew) {
        New = aNew;
    }

    public static Creator<documentHolder> getCREATOR() {
        return CREATOR;
    }

    public String getReceiverName() {
        return receiverName;
    }
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(extension);
        parcel.writeString(size);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(uid);
        parcel.writeString(ownerName);
        parcel.writeString(nodeKey);
        parcel.writeString(receiverName);
        parcel.writeString(url);
        parcel.writeByte((byte) ( access ? 1 : 0 ));
        parcel.writeByte((byte)(New ? 1:0));
    }

}
