package com.example.clothshop.Info;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class CommentsInfo implements Serializable{
	private String cid;
	private String pid;
	private String uid;
	private String ccontent;
	private String ctime;
	private String uname;
	private Drawable uavatar;

	public void setUavatar(Drawable uavatar) {
		this.uavatar = uavatar;
	}

	public Drawable getUavatar() {

		return uavatar;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setCcontent(String ccontent) {
		this.ccontent = ccontent;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getCid() {

		return cid;
	}

	public String getPid() {
		return pid;
	}

	public String getUid() {
		return uid;
	}

	public String getCcontent() {
		return ccontent;
	}

	public String getCtime() {
		return ctime;
	}

	public String getUname() {
		return uname;
	}



}
