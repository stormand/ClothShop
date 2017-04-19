package com.example.clothshop.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothshop.Info.UserInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.utils.HttpPostUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static String LOGIN_TO_MAIN = "login_to_main";
    public static String USER_NAME = "user_name";

    private ImageHandler handler = new ImageHandler();

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mImageConfirmTextView;
    private ImageView mImageConfirmView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_or_phone);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mImageConfirmTextView = (EditText) findViewById(R.id.image_confirm_text_view);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//注册按钮
                //打开注册页面
                SMSSDK.initSDK(LoginActivity.this, "1d082f36cee98", "92d871fb8953017cd157da7a586c08a1");
                RegisterPage registerPage = new RegisterPage();
                registerPage.setRegisterCallback(new EventHandler() {
                    public void afterEvent(int event, int result, Object data) {
                        // 解析注册结果
                        Log.i("register_result", Integer.toString(result));
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                            String country = (String) phoneMap.get("country");
                            String phone = (String) phoneMap.get("phone");
                            // 提交用户信息
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            intent.putExtra("phone", phone);
                            startActivityForResult(intent, 0);
                        }
                    }
                });
                registerPage.show(LoginActivity.this);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //验证码
        mImageConfirmView = (ImageView) findViewById(R.id.image_comfirm_view);
        ImageThread imageThread=new ImageThread();
        imageThread.start();
        mImageConfirmView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/3/23
                ImageThread imageThread=new ImageThread();
                imageThread.start();
            }
        });

    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
           case RESULT_OK:
               this.finish();
               break;
           default:
               break;
       }

   }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }



        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);


        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmcode = mImageConfirmTextView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, confirmcode);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$") || email.matches("^1+[0-9]{10}$");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mConfirmCode;
        private String mes;

        UserLoginTask(String email, String password, String confirmcode) {
            mEmail = email;
            mPassword = password;
            mConfirmCode = confirmcode;
        }

        /**
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            //传入http请求参数
            Map<String, String> myparams = new HashMap<String, String>();
            myparams.put(Model.USER_NAME_ATTR, mEmail);
            myparams.put(Model.USER_PASSWORD_ATTR, mPassword);
            myparams.put(Model.LOGIN_MODE, Model.USER_AGE_ATTR);
            myparams.put("code", mConfirmCode);
            String result = HttpPostUtil.sendPostMessage(myparams, "utf-8", Model.LOGIN_PATH);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equals("0")) {

                    Model.ISLOGIN = true;
                    Model.MYUSER = new UserInfo();
                    Model.MYUSER.setUname(mEmail);

                    Model.MYUSER.setUserid(jsonObject.getString("id"));
                    Model.MYUSER.setUage(jsonObject.getString("age"));
                    Model.MYUSER.setUsex(jsonObject.getString("sex"));
                    Model.MYUSER.setUheight(jsonObject.getString("height"));
                    Model.MYUSER.setUweight(jsonObject.getString("weight"));
                    Intent intent = new Intent();
                    //跳转到person页面
                    return true;
                } else {
                    mes = jsonObject.getString("mes");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mes = "JSONException";
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                LoginActivity.this.finish();
            } else {
                Toast.makeText(LoginActivity.this, mes, Toast.LENGTH_SHORT).show();
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 保存用户名和密码
        SharedPreferences settings = getSharedPreferences(Model.SP_NAME_PASSWD, MODE_PRIVATE);
        settings.edit().putString(Model.USER_NAME_ATTR, mEmailView.getText().toString())
                .putString(Model.USER_PASSWORD_ATTR, mPasswordView.getText().toString()).commit();

    }

    class ImageHandler extends android.os.Handler {
        public static final int SHOW_NETWORK_IMAGE = 0x0001;

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_NETWORK_IMAGE) {
                Bitmap bitmap = (Bitmap) msg.obj;
                mImageConfirmView.setImageBitmap(bitmap);
            }
        }
    }

    class ImageThread extends Thread {
        @Override
        public void run() {
            HttpPostUtil httpPostUtil = new HttpPostUtil();
            Bitmap bitmap = HttpPostUtil.getHttpBitmap(Model.IMAGE_PATH);
            showImage(bitmap);
        }

        private void showImage(Bitmap bitmap) {

            Message msg = Message.obtain(handler, ImageHandler.SHOW_NETWORK_IMAGE);
            msg.obj = bitmap;
            msg.sendToTarget();
        }


    }
}

