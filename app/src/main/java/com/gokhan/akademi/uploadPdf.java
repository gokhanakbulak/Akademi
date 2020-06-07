package com.gokhan.akademi;

public class uploadPdf {

    public String name,uid;
    public String url;

    public uploadPdf() {
    }

    public uploadPdf(String name, String url,String uid) {
        this.name = name;
        this.url = url;
        this.uid = uid;
    }


    public String getName() {
        return name;
    }



    public String getUrl() {
        return url;
    }
    public String getUid() {
        return uid;
    }

}
