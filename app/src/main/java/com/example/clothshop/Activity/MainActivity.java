package com.example.clothshop.Activity;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements PersonFragment.OnFragmentInteractionListener,HomeFragment.OnFragmentInteractionListener{

    public static boolean isLogin=false;

    public static String mUserName;
    public static int TAB_HOME=1;
    public static int TAB_PERSON=3;
    private TextView mTextMessage;
    private BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;
    private PersonFragment personFragment;
    private HomeFragment homeFragment;

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
                }break;
            case 2:

                break;
            case 3:
                if(personFragment==null){
                    personFragment=new PersonFragment();
                    Bundle bundle = new Bundle();
                    if (isLogin){
                        bundle.putString("username",mUserName);
                    }else{
                        bundle.putString("username",getString(R.string.not_login));
                    }
                    personFragment.setArguments(bundle);
                    transaction.add(R.id.content,personFragment);
                }else{
                    transaction.show(personFragment);
                }break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (personFragment!= null) {
            transaction.hide(personFragment);
        }if (homeFragment!= null) {
            transaction.hide(homeFragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager=getSupportFragmentManager();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    @Override
    protected void onResume() {
        int id = getIntent().getIntExtra(LoginActivity.LOGIN_TO_MAIN, TAB_HOME);
        chooseNav(id);
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
