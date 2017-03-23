package com.example.clothshop.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.clothshop.R;

public class MainActivity extends AppCompatActivity implements PersonFragment.OnFragmentInteractionListener{


    public static int TAB_HOME=1;
    public static int TAB_PERSON=3;
    private TextView mTextMessage;

    private FragmentManager fragmentManager;
    private PersonFragment personFragment;
    private String userName;

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
            case 1:break;
            case 2:break;
            case 3:
                if(personFragment==null){
                    personFragment=new PersonFragment();
                    transaction.add(R.id.content,personFragment);
                    Log.v("bb","aa");
                }else{
                    transaction.show(personFragment);
                }break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (personFragment!= null) {
            transaction.hide(personFragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager=getSupportFragmentManager();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onResume() {
        int id = getIntent().getIntExtra(LoginActivity.LOGIN_TO_MAIN, TAB_HOME);

        //从注册页面返回
        if(id==TAB_PERSON){
            chooseNav(TAB_PERSON);
            userName=getIntent().getBundleExtra(LoginActivity.USER_NAME).getString("username");
            TextView userNameView= (TextView) findViewById(R.id.user_name_textview);
            userNameView.setText(userName);
        }
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
