package com.tds.demo;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.taptap.sdk.AccessToken;
import com.taptap.sdk.AccountGlobalError;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapLoginHelper;
import com.taptap.sdk.net.Api.ApiCallback;
import com.tds.TdsInitializer;
import com.tds.demo.tapdb.TapDBActivity;
import com.tds.moment.TapTapMomentSdk;
import com.tds.moment.TapTapMomentSdk.Config;
import com.tds.moment.TapTapMomentSdk.TapMomentCallback;
import com.tds.tapdb.sdk.LoginType;
import com.tds.tapdb.sdk.TapDB;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "TDSDemoActivity";


  private Button tapLoginButton;

  private boolean enableTapDB = true;

  private AlertDialog alertDialog;

  private TextView infoTextView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tapLoginButton = findViewById(R.id.btn_login_tap);
    infoTextView = findViewById(R.id.infoTextView);

    //初始化SDK
    com.tds.TdsConfig.Builder configBuilder = new com.tds.TdsConfig.Builder()
            .appContext(getApplicationContext())
            .clientId("FwFdCIr6u71WQDQwQN");

    TdsInitializer.init(configBuilder.build());

    Profile profile = Profile.getCurrentProfile();
    if (profile != null) {
      Log.e(TAG, profile.toString());
      setLoginButton(true);
    } else {
      setLoginButton(false);
    }

    if (enableTapDB) {
      //开启TapDB
      TdsInitializer.enableTapDB(this, "1.0", "default");

      /**userId 为游戏平台中用户唯一的账号 ID。
       * 可以根据自己的业务将用户userId传给sdk，也可以用SDK返回的openId作为用户userId来标记用户
       * 此处调用表示用户已经登录: 可以通过Profile.getCurrentProfile()获取到登录信息的情况下调用setUser
       */
      if (profile != null) {
        String userId = profile.getOpenid() + "-userId"; //添加后缀方便
        TapDB.setUser(userId, LoginType.TapTap);
      }
    }

    // 开启动态
    TdsInitializer.enableMoment(this);
    //注册动态回调。动态相关的回调都会到这里
    TapTapMomentSdk.setCallback(new TapMomentCallback() {
      @Override
      public void onCallback(int code, String msg) {
        switch (code) {
          case TapTapMomentSdk.CALLBACK_CODE_GET_NOTICE_SUCCESS:
            infoTextView.setText("动态未读消息数目:" + msg);
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
        infoTextView.setText("用户登录成功" + Profile.getCurrentProfile().getName());
        setLoginButton(true);
        if (enableTapDB) {
          TapDB.setUser(Profile.getCurrentProfile().getOpenid() + "-userId", LoginType.TapTap);
        }
      }

      @Override
      public void onLoginCancel() {
        // 用户取消登录
        Log.e(TAG, "onLoginCancel" + "");
        infoTextView.setText("用户取消登录");
      }

      @Override
      public void onLoginError(com.taptap.sdk.AccountGlobalError accountGlobalError) {
        setLoginButton(false);
        infoTextView.setText("登录异常");
        // 登录过程中出现异常
        if (null != accountGlobalError) {
          // 执行 TapTap Token 失效后的相关处理操作
          if (AccountGlobalError.LOGIN_ERROR_ACCESS_DENIED.equals(accountGlobalError.getError())) {
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
    if (Profile.getCurrentProfile() != null) {
      TapLoginHelper.logout();
      setLoginButton(false);
      infoTextView.setText("退出登录");
    } else {
      TapLoginHelper.startTapLogin(MainActivity.this, TapLoginHelper.SCOPE_PUBLIC_PROFILE);
    }
  }

  public void sdkMoment(View view) {
    Config config = new Config();
    TapTapMomentSdk.openTapMoment(config);
  }

  public void getRedPoint(View view) {
    if (AccessToken.getCurrentAccessToken() == null) {
      infoTextView.setText("当前用户未登录");
      setLoginButton(false);
    } else {
      TapTapMomentSdk.getNoticeData();
    }
  }

  public void fetchUserProfile(View view) {
    if (AccessToken.getCurrentAccessToken() == null) {
      infoTextView.setText("当前用户未登录");
      setLoginButton(false);
    } else {
      TapLoginHelper.fetchProfileForCurrentAccessToken(new ApiCallback<Profile>() {
        @Override
        public void onSuccess(Profile profile) {
          Log.e(TAG, "onSuccess:" + profile);
          infoTextView.setText("获取用户信息成功:" + profile.getName());
          setLoginButton(true);
        }

        @Override
        public void onError(Throwable throwable) {
          Log.e(TAG, "onError:" + "", throwable);
          infoTextView.setText("获取用户信息失败:");
          setLoginButton(false);
        }
      });
    }
  }

  public void configTapDB(View view) {
    Intent intent = new Intent(MainActivity.this, TapDBActivity.class);
    startActivity(intent);
  }

  public void setLoginButton(boolean login) {
    if (login) {
      tapLoginButton.setText("退出登录");
    } else {
      tapLoginButton.setText("TapTap登录");
    }
  }
}