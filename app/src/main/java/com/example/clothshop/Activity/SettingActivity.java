package com.example.clothshop.Activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.clothshop.Model.Model;
import com.example.clothshop.R;

public class SettingActivity extends AppCompatActivity {

    Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mLogoutButton= (Button) findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.MYUSER=null;
                SharedPreferences sp=getSharedPreferences(Model.SP_NAME_PASSWD,MODE_PRIVATE);
                sp.edit().clear().commit();
                SettingActivity.this.finish();
            }
        });

    }
}
