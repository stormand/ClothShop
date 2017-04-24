package com.example.clothshop.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.utils.HttpPostUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private EditText mNameEditView;
    private EditText mAgeEditView;
    private EditText mHeightEditView;
    private EditText mWeightEditView;
    private RadioGroup mSexRadioGroup;
    private ImageView mAvatar;
    private String mSex=null;
    private SaveHandler handler;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_user_info,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mSex=Model.MYUSER.getUsex();
        handler=new SaveHandler();
        mNameEditView= (EditText) findViewById(R.id.edit_user_name_view);
        mAgeEditView= (EditText) findViewById(R.id.edit_age_view);
        mHeightEditView= (EditText) findViewById(R.id.edit_height_view);
        mWeightEditView= (EditText) findViewById(R.id.edit_weight_view);
        mSexRadioGroup= (RadioGroup) findViewById(R.id.sex_radio_group);
        mAvatar= (ImageView) findViewById(R.id.user_info_avatar);
        if (Model.MYUSER!=null){
            //头像
            Drawable draw1;
            mAvatar.setImageDrawable(Model.MYUSER.getUavatar());
            if (!Model.MYUSER.getUname().equals("null")){
                mNameEditView.setText(Model.MYUSER.getUname());
            }
            if (!Model.MYUSER.getUage().equals("null") && !Model.MYUSER.getUage().equals("0")){
                mAgeEditView.setText(Model.MYUSER.getUage());
            }
            if (!Model.MYUSER.getUheight().equals("null") && !Model.MYUSER.getUheight().equals("0")){
                mHeightEditView.setText(Model.MYUSER.getUheight());
            }
            if (!Model.MYUSER.getUweight().equals("null") && !Model.MYUSER.getUweight().equals("0")){
                mWeightEditView.setText(Model.MYUSER.getUweight());
            }
            if (!Model.MYUSER.getUsex().equals("null")){
                if (Model.MYUSER.getUsex().equals(Model.MALE_TEXT)){
                    mSexRadioGroup.check(R.id.radio_male);
                }else {
                    mSexRadioGroup.check(R.id.radio_female);
                }
            }
        }
        mSexRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radio_female:
                        mSex=Model.FEMALE_TEXT;
                        mAvatar.setImageResource(R.drawable.avatar_female);
                        break;
                    case R.id.radio_male:
                        mSex=Model.MALE_TEXT;
                        mAvatar.setImageResource(R.drawable.avatar_male);
                        break;
                    default:
                        break;
                }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                SaveThread saveThread=new SaveThread();
                saveThread.start();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class SaveThread extends Thread{
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String, String>();

            SharedPreferences sharedPreferences=getSharedPreferences(Model.SP_NAME_PASSWD,MODE_PRIVATE);
            String password=sharedPreferences.getString(Model.USER_PASSWORD_ATTR,"error");


            params.put(Model.USER_NAME_ATTR,mNameEditView.getText().toString());
            params.put(Model.USER_PASSWORD_ATTR,password);
            params.put(Model.USER_SEX_ATTR,mSex);
            params.put(Model.USER_AGE_ATTR,mAgeEditView.getText().toString());
            params.put(Model.USER_HEIGHT_ATTR,mHeightEditView.getText().toString());
            params.put(Model.USER_WEIGHT_ATTR,mWeightEditView.getText().toString());
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.USER_INFO_UPLOAD_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"),jsonObject.getString("status"));
                    finish();
                }else {
                    showMessage(jsonObject.getString("mes"),jsonObject.getString("status"));
                }
            } catch (JSONException e) {
                showMessage(e.toString(),"");
                e.printStackTrace();
            }
        }
        private void showMessage(String message,String status){
            if (status.equals("0")){
                Message msg=Message.obtain(handler, SaveHandler.SUCCESS);
                msg.obj=message;
                msg.sendToTarget();
                msg.setTarget(handler);
            }else{
                Message msg=Message.obtain(handler, SaveHandler.FAILURE);
                msg.obj=message;
                msg.sendToTarget();
                msg.setTarget(handler);
            }

        }
    }

    class SaveHandler extends Handler {

        public static final int SUCCESS=0x0001;
        public static final int FAILURE=0x0002;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    Toast.makeText(UserInfoActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    Model.MYUSER.setUname(mNameEditView.getText().toString());
                    Model.MYUSER.setUheight(mHeightEditView.getText().toString());
                    Model.MYUSER.setUweight(mWeightEditView.getText().toString());
                    Model.MYUSER.setUage(mAgeEditView.getText().toString());
                    Model.MYUSER.setUsex(mSex);
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
                    break;
                case FAILURE:
                    Toast.makeText(UserInfoActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
