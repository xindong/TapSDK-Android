package com.tds.demo;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.taptap.sdk.AccessToken;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapTapSdk;
import com.taptap.sdk.helper.TapLoginHelper;
import com.taptap.sdk.helper.TapLoginHelper.TapLoginResultCallback;
import com.taptap.sdk.net.Api.ApiCallback;
import com.tds.TdsInitializer;
import com.tds.common.net.error.AccountGlobalError;
import com.tds.moment.TapTapMomentSdk;
import com.tds.moment.TapTapMomentSdk.Config;
import com.tds.moment.TapTapMomentSdk.TapMomentCallback;

public class MainActivity extends AppCompatActivity {

    private static final String Tag = "MainActivity";

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

        // 开启动态
        TdsInitializer.enableMoment(this);
        TapTapMomentSdk.setHandleLoginResult(true);

        //注册登录回调
        TapLoginHelper.registerLoginCallback(new TapLoginResultCallback() {
            @Override
            public void onLoginSuccess(AccessToken accessToken) {
                Log.e("MainActivity", "onLoginSuccess" + "" + accessToken);
            }

            @Override
            public void onLoginCancel() {
                Log.e("MainActivity", "onLoginCancel" + "");
            }

            @Override
            public void onLoginError(com.taptap.sdk.AccountGlobalError accountGlobalError) {
                Log.e("MainActivity", "onLoginError" + " " + accountGlobalError.toJsonString());
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
        TapTapMomentSdk.setCallback(new TapMomentCallback() {
            @Override
            public void onCallback(int i, String s) {
                Log.e("MainActivity", "onCallback moment " + "  " + i + "   " + s);
            }
        });
    }

    public void getRedPoint(View view) {
        TapTapMomentSdk.getNoticeData();
    }

    public void fetchUserProfile(View view) {
        TapLoginHelper.fetchProfileForCurrentAccessToken(new ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                Log.e("MainActivity", "onSuccess" + " " + profile);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "onError" + "");
            }
        });
    }
}