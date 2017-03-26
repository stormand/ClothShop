package com.example.clothshop.Model;

import com.example.clothshop.Info.UserInfo;

/**
 * Created by 一凡 on 2017/3/25.
 */

public class Model {

    //http
    public static final String PATH = "http://122.112.247.7:8080/Order/Main/";
    public static final String IMAGE_PATH = PATH + "Image.php";
    public static final String REGISTER_PATH = PATH + "Register.php";
    public static final String LOGIN_PATH = PATH + "Login.php";
    public static final String PUBLISH_PATH = PATH + "SaveMessage.php";
    public static final String MENU_PATH = PATH + "ShowMenu.php";
    public static final String ORDER_PATH = PATH + "AddOrder.php";
    public static final String HOME_PATH = PATH + "ShowHome.php";

    //user
    public static final String USER_NAME="username";
    public static final String USER_PASSWORD="password";
    public static final String USER_PHONE="phone";
    public static final String USER_EMAIL="email";
    public static final String USER_AGE="age";
    public static final String USER_SEX="sex";
    public static final String USER_WEIGHT="weight";
    public static final String USER_HEIGHT="height";
    public static final String USER_AVATAR="avatar";
    //post
    public static final String TITLE="title";
    public static final String CONTENT="content";
    public static final String UID="uid";

    public static int SCREEMWIDTH;
    public static int LISTMARGIN;
    //islogin
    public static boolean ISLOGIN=false;
    //my user
    public static UserInfo MYUSER=null;
}
