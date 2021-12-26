package com.android.documentsharing.Holder;

public class Users {
    String name,about,uId,code,number,finalNo,url;

    public Users() {
    }

    public Users(String name, String about, String uId, String code, String number, String finalNo, String url) {
        this.name = name;
        this.about = about;
        this.uId = uId;
        this.code = code;
        this.number = number;
        this.finalNo = finalNo;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFinalNo() {
        return finalNo;
    }

    public void setFinalNo(String finalNo) {
        this.finalNo = finalNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
