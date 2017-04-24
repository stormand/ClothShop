package com.example.clothshop.Model;

import com.example.clothshop.Info.UserInfo;
/**
 * Created by 一凡 on 2017/3/25.
 */
public class Model {
    //http
    public static final String IMAGE_SAVE_PATH = "http://122.112.247.7:8080/Order/";
    public static final String PATH = "http://122.112.247.7:8080/Order/Main/";
    public static final String IMAGE_PATH = PATH + "Image.php";
    public static final String REGISTER_PATH = PATH + "Register.php";
    public static final String LOGIN_PATH = PATH + "Login.php";
    public static final String PUBLISH_PATH = PATH + "SaveMessage.php";
    public static final String USER_INFO_UPLOAD_PATH = PATH + "UserInfoUpload.php";
    public static final String HOME_PATH = PATH + "ShowHome.php";
    public static final String DETAIL_POST_PATH = PATH + "DetailPost.php";
    public static final String GET_COMMENT_PATH = PATH + "GetComment.php";
    public static final String SEND_COMMENT_PATH = PATH + "SendComment.php";
    public static final String ADD_LOVE_PATH = PATH + "AddLove.php";
    public static final String ADD_COLLECTION_PATH = PATH + "AddCollection.php";
    public static final String USER_POST_PATH = PATH + "UserPost.php";
    public static final String DELETE_POST_PATH=PATH+"DeletePost.php";

    //user
    public static final String USER_NAME_ATTR="username";
    public static final String USER_PASSWORD_ATTR="password";
    public static final String USER_PHONE_ATTR="phone";
    public static final String USER_EMAIL_ATTR="email";
    public static final String USER_AGE_ATTR="age";
    public static final String USER_SEX_ATTR="sex";
    public static final String USER_WEIGHT_ATTR="weight";
    public static final String USER_HEIGHT_ATTR="height";
    public static final String USER_AVATAR_ATTR="avatar";
    //post
    public static final String POST_ID_ATTR="pid";
    public static final String POST_TITLE_ATTR="title";
    public static final String POST_CONTENT_ATTR="content";
    public static final String POST_UID_ATTR="uid";
    public static final String POST_UNAME_ATTR="uname";
    public static final String POST_UAGE_ATTR="uage";
    public static final String POST_USEX_ATTR="usex";
    public static final String POST_UHEIGHT_ATTR="uheight";
    public static final String POST_UWEIGHT_ATTR="uweight";
    public static final String POST_IMAGE_ATTR="image";
    public static final String POST_DAY_TIME_ATTR="day_time";
    public static final String POST_LOVE_NUM="love_num";
    public static final String POST_LINK_ATTR="link";
    //pcomment
    public static final String COMMENT_UID="uid";
    public static final String COMMENT_PID="pid";
    public static final String COMMENT_UNAME="uname";
    public static final String COMMENT_TIME="date_time";
    public static final String COMMENT_CONTENT="ccontent";
    //love collection
    public static final String LOVE="love";
    public static final String COLLECTION="collection";

    public static final String FEMALE_TEXT="女";
    public static final String MALE_TEXT="男";

    public static int SCREEMWIDTH;
    public static int LISTMARGIN;
    //islogin
    public static boolean ISLOGIN=false;
    public static final String LOGIN_MODE="login_mode";
    public static final String AUTO_LOGIN_MODE="auto_login";
    public static final String USER_LOGIN_MODE="user_login";
    //my user
    public static UserInfo MYUSER=null;

    public static final String SP_NAME_PASSWD="name_passwd";
}
