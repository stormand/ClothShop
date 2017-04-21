package com.example.clothshop.Activity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothshop.Fragment.AllFragment;
import com.example.clothshop.Fragment.HomeFragment;
import com.example.clothshop.Fragment.PersonFragment;
import com.example.clothshop.Info.UserInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.utils.HttpPostUtil;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PersonFragment.OnFragmentInteractionListener,HomeFragment.OnFragmentInteractionListener,AllFragment.OnFragmentInteractionListener{

    public static int TAB_HOME=1;
    public static int TAB_PERSON=3;
    private TextView mTextMessage;
    private BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;
    private PersonFragment personFragment;
    private HomeFragment homeFragment;
    private AllFragment allFragment;

    private Loginhandler handler;

    private int NaviClickRecord;

    private final String CACHE_DIR_NAME ="/ClothShop/picasso";



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    chooseNav(1);
                    return true;
                case R.id.navigation_dashboard:
                    chooseNav(2);
                    return true;
                case R.id.navigation_person:
                    chooseNav(3);
                    return true;
            }
            return false;
        }

    };

    private void chooseNav(int num){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (num){
            case 1:
                if(homeFragment==null){
                    homeFragment=new HomeFragment();
                    transaction.add(R.id.content,homeFragment);
                }else{
                    transaction.show(homeFragment);
                    if (num==NaviClickRecord){
                        homeFragment.refresh();
                    }
                }
                break;
            case 2:
                if(allFragment==null){
                    allFragment=new AllFragment();
                    transaction.add(R.id.content,allFragment);
                }else{
                    transaction.show(allFragment);
                    if (num==NaviClickRecord){
                        allFragment.refresh();
                    }
                }
                break;
            case 3:
                if(personFragment==null){
                    personFragment=new PersonFragment();
                    Bundle bundle = new Bundle();
                    if (Model.ISLOGIN){
                        bundle.putString("username", Model.MYUSER.getUname());
                    }else{
                        bundle.putString("username",getString(R.string.not_login));
                    }
                    personFragment.setArguments(bundle);
                    transaction.add(R.id.content,personFragment);
                }else{
                    transaction.show(personFragment);
                }break;
        }
        NaviClickRecord=num;
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (personFragment!= null) {
            transaction.hide(personFragment);
        }if (homeFragment!= null) {
            transaction.hide(homeFragment);
        }if (allFragment!=null){
            transaction.hide(allFragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFileCache();
        loadImageCache();
        setContentView(R.layout.activity_main);
        WindowManager wm = this.getWindowManager();
        Model.SCREEMWIDTH=wm.getDefaultDisplay().getWidth();
        Model.LISTMARGIN=Model.SCREEMWIDTH/30;
        fragmentManager=getSupportFragmentManager();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        chooseNav(1);

        SharedPreferences sp=getSharedPreferences(Model.SP_NAME_PASSWD,MODE_PRIVATE);
        if (!sp.getString(Model.USER_NAME_ATTR,"").equals("") && Model.MYUSER==null){
            handler=new Loginhandler();
            LoginThread loginThread=new LoginThread();
            loginThread.start();
        }

    }

    //创建缓存路径
    public void initFileCache() {
        File file = new File(Environment.getExternalStorageDirectory(), CACHE_DIR_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    //
    private void loadImageCache() {
        final String imageCacheDir = Environment.getExternalStorageDirectory() + CACHE_DIR_NAME;
        Picasso picasso = new Picasso.Builder(this).downloader(
                new OkHttpDownloader(new File(imageCacheDir))).build();
        Picasso.setSingletonInstance(picasso);

    }

    @Override
    protected void onResume() {
        int id = getIntent().getIntExtra(LoginActivity.LOGIN_TO_MAIN, TAB_HOME);
        if (id==TAB_PERSON){
            chooseNav(id);
        }
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class LoginThread extends Thread{
        @Override
        public void run() {
            super.run();
            SharedPreferences sp=getSharedPreferences(Model.SP_NAME_PASSWD,MODE_PRIVATE);
            Map<String,String> myparams=new HashMap<String,String>();
            myparams.put(Model.USER_NAME_ATTR,sp.getString(Model.USER_NAME_ATTR,"error"));
            myparams.put(Model.USER_PASSWORD_ATTR,sp.getString(Model.USER_PASSWORD_ATTR,"error"));
            myparams.put(Model.LOGIN_MODE,Model.AUTO_LOGIN_MODE);
            String result= HttpPostUtil.sendPostMessage(myparams,"utf-8",Model.LOGIN_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("status").equals("0")){
                    Model.ISLOGIN=true;
                    Model.MYUSER=new UserInfo();
                    Model.MYUSER.setUname(sp.getString(Model.USER_NAME_ATTR,"error"));
                    Model.MYUSER.setUserid(jsonObject.getString("id"));
                    Model.MYUSER.setUage(jsonObject.getString("age"));
                    Model.MYUSER.setUsex(jsonObject.getString("sex"));
                    Model.MYUSER.setUheight(jsonObject.getString("height"));
                    Model.MYUSER.setUweight(jsonObject.getString("weight"));
                    Drawable draw1;
                    if (Model.MYUSER.getUsex()==null || Model.MYUSER.getUsex().isEmpty()){
                        draw1 = getResources().getDrawable(R.drawable.avatar);
                    }else if (Model.MYUSER.getUsex().equals("男")){
                        draw1 = getResources().getDrawable(R.drawable.avatar_male);
                    }else if (Model.MYUSER.getUsex().equals("女")){
                        draw1 = getResources().getDrawable(R.drawable.avatar_female);
                    }else {
                        draw1 = getResources().getDrawable(R.drawable.avatar);
                    }
                    Model.MYUSER.setUavatar(draw1);
                }else {
                    showMessage(jsonObject.getString("mes"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showMessage(e.toString());
            }
        }

        private void showMessage(String message){
            Message msg=Message.obtain(handler, Loginhandler.MESSAGE);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(handler);
        }
    }

    class Loginhandler extends Handler{
        public static final int MESSAGE=0x0001;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==MESSAGE){
                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
