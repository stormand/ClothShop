package com.example.clothshop.Info;

import android.graphics.drawable.Drawable;

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
    private String uweight;
    private String uheight;
    private Drawable uavatar;

    private String uage;
    private String usex;
    private String uname;
    private String loveNum;

    private boolean myCollection=false;
    private boolean myLove=false;


    public void setUavatar(Drawable uavatar) {
        this.uavatar = uavatar;
    }

    public Drawable getUavatar() {

        return uavatar;
    }


    public void setLoveNum(String loveNum) {
        this.loveNum = loveNum;
    }

    public String getLoveNum() {

        return loveNum;
    }

    public void setMyCollection(boolean myCollection) {
        this.myCollection = myCollection;
    }

    public void setMyLove(boolean myLove) {
        this.myLove = myLove;
    }

    public boolean isMyCollection() {
        return myCollection;
    }

    public boolean isMyLove() {
        return myLove;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUname() {

        return uname;
    }

    public void setUage(String uage) {
        this.uage = uage;
    }

    public String getUage() {

        return uage;
    }

    public void setUweight(String uweight) {
        this.uweight = uweight;
    }

    public void setUheight(String uheight) {
        this.uheight = uheight;
    }

    public void setUsex(String usex) {
        this.usex = usex;
    }

    public String getUweight() {
        return uweight;
    }

    public String getUheight() {
        return uheight;
    }

    public String getUsex() {
        return usex;
    }



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
