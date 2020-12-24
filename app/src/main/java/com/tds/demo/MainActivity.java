package com.tds.demo;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.taptap.sdk.AccessToken;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapTapSdk;
import com.taptap.sdk.helper.TapLoginHelper;
import com.taptap.sdk.net.Api.ApiCallback;
import com.tds.TdsInitializer;
import com.tds.moment.TapTapMomentSdk;
import com.tds.moment.TapTapMomentSdk.Config;
import com.tds.moment.TapTapMomentSdk.TapMomentCallback;
import com.tds.tapdb.sdk.TapDB;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TDSDemoActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView.setWebContentsDebuggingEnabled(true);

        //初始化SDK
        com.tds.TdsConfig.Builder configBuilder = new com.tds.TdsConfig.Builder()
                .appContext(getApplicationContext())
                .clientId("FwFdCIr6u71WQDQwQN");

        TdsInitializer.init(configBuilder.build());

        //开启TapDB
        TdsInitializer.enableTapDB(this, "1.0", "default");

        /**userId 为游戏平台中用户唯一的账号 ID。
         * 可以根据自己的业务将用户userId传给sdk，也可以用SDK返回的openId作为用户userId来标记用户
         * 此处调用表示用户已经登录: 可以通过Profile.getCurrentProfile()获取到登录信息的情况下调用setUser
         */
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Log.e(TAG,profile.toString());
            String userId = profile.getOpenid();
            TapDB.setUser(userId);
        }

        // 开启动态
        TdsInitializer.enableMoment(this);
        //注册动态回调。动态相关的回调都会到这里，详情 https://tapsdkdoc.xdrnd.com/tap-fun-moment#3-%E6%B7%BB%E5%8A%A0%E5%9B%9E%E8%B0%83
        TapTapMomentSdk.setCallback(new TapMomentCallback() {
            @Override
            public void onCallback(int code, String msg) {
                Log.e(TAG, "onCallback" + "  code:" + code + "  msg:" + msg);
            }
        });


        //注册登录回调
        TapLoginHelper.registerLoginCallback(new TapLoginHelper.TapLoginResultCallback() {
            @Override
            public void onLoginSuccess(AccessToken accessToken) {
                Log.e(TAG, "onLoginSuccess" + accessToken);
            }

            @Override
            public void onLoginCancel() {
                Log.e(TAG, "onLoginCancel" + "");
            }

            @Override
            public void onLoginError(com.taptap.sdk.AccountGlobalError accountGlobalError) {
                Log.e(TAG, accountGlobalError.getError());
            }
        });
    }

    public void sdkLogin(View view) {
        TapLoginHelper.startTapLogin(MainActivity.this, TapTapSdk.SCOPE_PUIBLIC_PROFILE);
    }

    public void sdkLogout(View view) {
        TapLoginHelper.logout();
    }

    public void sdkMoment(View view) {
        Config config = new Config();
        TapTapMomentSdk.openTapMoment(config);
    }

    public void getRedPoint(View view) {
        TapTapMomentSdk.getNoticeData();
    }

    public void fetchUserProfile(View view) {
        TapLoginHelper.fetchProfileForCurrentAccessToken(new ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                Log.e(TAG, "onSuccess:" + profile);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError:" + "", throwable);
            }
        });
    }
}