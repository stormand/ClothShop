package com.example.clothshop.Activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.clothshop.DB.DatabaseUtil;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    Button mLogoutButton;
    TextView testTextView;

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

        testTextView= (TextView) findViewById(R.id.test_text_view);
        DatabaseUtil databaseUtil=DatabaseUtil.getInstance(SettingActivity.this);
        StringBuilder sb=new StringBuilder();
        ArrayList<PostInfo> list=databaseUtil.query();
        for(int i=0;i<list.size();i++){
            sb.append(list.get(i).getPid())
                    .append(" ")
                    .append(list.get(i).getUid())
                    .append(" ")
                    .append(list.get(i).isMyLove())
                    .append(" ")
                    .append(list.get(i).isMyCollection())
                    .append("\n");
        }
        testTextView.setText(sb);

    }
}
