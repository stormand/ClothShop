package com.example.clothshop.Info;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by 一凡 on 2017/3/25.
 */

public class UserInfo implements Serializable{

    private String userid;      //用户id
    private String uname;       //昵称
    private String uage;        //年龄
    private String usex;        //性别
    private String uheight;     //身高
    private String uweight;     //体重
    private Drawable uavatar;     //头像

    public String getUsex() {
        return usex;
    }

    public void setUsex(String usex) {
        this.usex = usex;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUage() {
        return uage;
    }

    public void setUage(String uage) {
        this.uage = uage;
    }

    public void setUweight(String uweight) {
        this.uweight = uweight;
    }

    public String getUweight(){
        return  uweight;
    }

    public void setUheight(String uheight){
        this.uheight=uheight;
    }

    public String getUheight(){
        return uheight;
    }

    public void setUavatar(Drawable uavatar){
        this.uavatar=uavatar;
    }

    public Drawable getUavatar(){
        return uavatar;
    }


}
