package com.example.clothshop.Info;

/**
 * Created by 一凡 on 2017/3/25.
 */

public class PostInfo {

    private String pid;         //帖子id
    private String ptitle;      //帖子标题
    private String uid;         //帖子作者id
    private String pcontent;    //帖子内容

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
