package com.tds.demo;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.taptap.sdk.AccessToken;
import com.taptap.sdk.AccountGlobalError;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapLoginHelper;
import com.taptap.sdk.net.Api.ApiCallback;
import com.tds.TdsInitializer;
import com.tds.moment.TapTapMomentSdk;
import com.tds.moment.TapTapMomentSdk.Config;
import com.tds.moment.TapTapMomentSdk.TapMomentCallback;
import com.tds.tapdb.sdk.TapDB;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TDSDemoActivity";

    private View buttonContainer;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonContainer = findViewById(R.id.tds_button_container);

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
                switch (code) {
                    case TapTapMomentSdk.CALLBACK_CODE_GET_NOTICE_SUCCESS:
                        Snackbar.make(buttonContainer, "动态未读消息数目:" + msg, Snackbar.LENGTH_LONG).show();
                        break;
                }
                Log.e(TAG, "onCallback" + "  code:" + code + "  msg:" + msg);
            }
        });


        //注册登录回调
        TapLoginHelper.registerLoginCallback(new TapLoginHelper.TapLoginResultCallback() {
            @Override
            public void onLoginSuccess(AccessToken accessToken) {
                Log.e(TAG, "onLoginSuccess" + accessToken);
                // 执行登录后相关操作
                Snackbar.make(buttonContainer, "用户登录成功:" + Profile.getCurrentProfile().getName(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onLoginCancel() {
                // 用户取消登录
                Log.e(TAG, "onLoginCancel" + "");
                Snackbar.make(buttonContainer, "用户取消登录", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onLoginError(com.taptap.sdk.AccountGlobalError accountGlobalError) {
                // 登录过程中出现异常
                if (null != accountGlobalError) {
                    // 执行 TapTap Token 失效后的相关处理操作
                    if (AccountGlobalError.LOGIN_ERROR_ACCESS_DENIED.equals(accountGlobalError.getError())
                            || AccountGlobalError.LOGIN_ERROR_FORBIDDEN.equals(accountGlobalError.getError())) {
                        if (null != alertDialog && alertDialog.isShowing()) {
                            return;
                        }
                        alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("错误")
                            .setMessage("当前用户已失效， 请重新登录!")
                            .setNegativeButton(
                            "取消", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("重新登录", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TapLoginHelper.startTapLogin(MainActivity.this, TapLoginHelper.SCOPE_PUBLIC_PROFILE);
                                }
                            }).create();
                        alertDialog.show();
                    }
                }
                Log.e(TAG, accountGlobalError.getError());
            }
        });
    }

    public void sdkLogin(View view) {
        TapLoginHelper.startTapLogin(MainActivity.this, TapLoginHelper.SCOPE_PUBLIC_PROFILE);
    }

    public void sdkLogout(View view) {
        TapLoginHelper.logout();
        Snackbar.make(buttonContainer, "退出登录", Snackbar.LENGTH_LONG).show();
    }

    public void sdkMoment(View view) {
        Config config = new Config();
        TapTapMomentSdk.openTapMoment(config);
    }

    public void getRedPoint(View view) {
        if (AccessToken.getCurrentAccessToken() == null) {
            Snackbar.make(buttonContainer, "当前用户未登录", Snackbar.LENGTH_LONG).show();
        } else {
            TapTapMomentSdk.getNoticeData();
        }
    }

    public void fetchUserProfile(View view) {
        if (AccessToken.getCurrentAccessToken() == null) {
            Snackbar.make(buttonContainer, "当前用户未登录", Snackbar.LENGTH_LONG).show();
        } else {
            TapLoginHelper.fetchProfileForCurrentAccessToken(new ApiCallback<Profile>() {
                @Override
                public void onSuccess(Profile profile) {
                    Log.e(TAG, "onSuccess:" + profile);
                    Snackbar.make(buttonContainer, "获取用户信息成功:" + profile.getName(), Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(TAG, "onError:" + "", throwable);
                    Snackbar.make(buttonContainer, "获取用户信息失败", Snackbar.LENGTH_LONG).show();
                }
            });
        }

    }
}