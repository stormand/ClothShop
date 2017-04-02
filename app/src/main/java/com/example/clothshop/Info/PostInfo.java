package com.example.clothshop.Info;

import java.io.Serializable;

/**
 * Created by 一凡 on 2017/3/25.
 */

public class PostInfo implements Serializable{

    private String pid;         //帖子id
    private String ptitle;      //帖子标题
    private String uid;         //帖子作者id
    private String pcontent;    //帖子内容
    private String pdaytime;
    private String pimage;

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }

    public String getPimage() {

        return pimage;
    }



    public void setPdaytime(String pdaytime) {
        this.pdaytime = pdaytime;
    }

    public String getPdaytime() {

        return pdaytime;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPtitle(String ptitle) {
        this.ptitle = ptitle;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPcontent(String pcontent) {
        this.pcontent = pcontent;
    }

    public String getPid() {

        return pid;
    }

    public String getPtitle() {
        return ptitle;
    }

    public String getUid() {
        return uid;
    }

    public String getPcontent() {
        return pcontent;
    }
}
