package com.xwaydesigns.youvan.ExtraClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.xwaydesigns.youvan.LoginActivity;

import java.util.HashMap;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class SessionManager
{
    public Context ctx;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public static final String pref_name = "mydata";
    public int PRIVATE_MODE = 0;
    public static final String IsLogin = "IsLoggedIn";
    public static final String IsReg = "IsRegistered";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String STUDENT_ID = "student_id";
    public static final String Default_Value = "DEFAULT";

    public SessionManager(Context context)
    {
        ctx = context;
        pref = ctx.getSharedPreferences(pref_name,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void SignUpSuccessful(String email,String password,String student_id)
    {
        editor.putString(EMAIL,email);
        editor.putString(PASSWORD,password);
        editor.putString(STUDENT_ID,student_id);
        editor.putBoolean(IsReg, true);
        editor.putBoolean(IsLogin,true);
        editor.apply();
    }

    public HashMap<String,String> getUserData()
    {
        HashMap<String,String> user = new HashMap<String,String>();
        user.put("user_email",pref.getString(EMAIL,Default_Value));
        user.put("user_password",pref.getString(PASSWORD,Default_Value));
        user.put("student_id",pref.getString(STUDENT_ID,Default_Value));
        return user;
    }

    public void LoginSuccessful(String email,String password,String student_id)
    {
        editor.putBoolean(IsLogin,true);
        editor.putBoolean(IsReg, true);
        editor.putString(EMAIL,email);
        editor.putString(PASSWORD,password);
        editor.putString(STUDENT_ID,student_id);
        editor.apply();
    }

    public void checkLogin()
    {
        if (!this.IsLoggedIn())
        {
            Intent log_intent = new Intent(ctx, LoginActivity.class);
            log_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            log_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(log_intent);
        }

    }

    public void Logout()
    {
        editor.putBoolean(IsLogin,false);
        editor.apply();
        Intent logout_intent = new Intent(ctx,LoginActivity.class);
        logout_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(logout_intent);
        Toast.makeText(ctx,"You have Logged out succesfully",Toast.LENGTH_SHORT).show();
    }

    public boolean IsLoggedIn()
    {
        return pref.getBoolean(IsLogin, false);
    }

    public boolean IsRegistered()
    {
        return pref.getBoolean(IsReg, false);
    }

    public boolean connectivity(){
        boolean wifi = false;
        boolean mobiledata = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI"))
                if(info.isConnected())
                    wifi = true;
            if(info.getTypeName().equalsIgnoreCase("MOBILE"))
                if(info.isConnected())
                    mobiledata = true;
        }
        return mobiledata||wifi;
    }


}
