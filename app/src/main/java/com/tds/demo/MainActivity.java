package com.tds.demo;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.taptap.sdk.AccessToken;
import com.taptap.sdk.CallBackManager;
import com.taptap.sdk.LoginManager;
import com.taptap.sdk.LoginResponse;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapTapLoginCallback;
import com.taptap.sdk.TapTapSdk;
import com.taptap.sdk.helper.TapLoginHelper;
import com.taptap.sdk.net.Api.ApiCallback;
import com.tds.TdsInitializer;
import com.tds.moment.TapTapMomentSdk;
import com.tds.moment.TapTapMomentSdk.Config;
import com.tds.moment.TapTapMomentSdk.TapMomentCallback;
import com.tds.tapdb.sdk.LoginType;
import com.tds.tapdb.sdk.TapDB;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

  private static final String Tag = "MainActivity";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    com.tds.TdsConfig.Builder configBuilder = new com.tds.TdsConfig.Builder()
        .appContext(getApplicationContext())
        .clientId("FwFdCIr6u71WQDQwQN");

    TdsInitializer.init(configBuilder.build());
    TdsInitializer.enableTapDB(this, "1.0", "default");
    TdsInitializer.enableMoment(this);


    TapLoginHelper.getInstance().setLoginResultCallback(new TapLoginHelper.ITapLoginResultCallback() {
      @Override
      public void onLoginSuccess(AccessToken accessToken) {
        Log.e(Tag, "onLoginSuccess");
        Profile.fetchProfileForCurrentAccessToken(new ApiCallback<Profile>() {
          @Override
          public void onSuccess(Profile profile) {
            TapDB.setUser(profile.getOpenid(), profile.getOpenid(), LoginType.TapTap);
          }

          @Override
          public void onError(Throwable throwable) {

          }
        });

      }

      @Override
      public void onLoginCancel() {
        Log.e(Tag, "onLoginCancel");
      }

      @Override
      public void onLoginError(Throwable throwable) {
        Log.e(Tag, "onLoginError");
      }
    });
  }

  public void sdkLogin(View view) {
    TapLoginHelper.getInstance().startTapLogin(MainActivity.this,TapTapSdk.SCOPE_PUIBLIC_PROFILE);
  }


  public void sdkMoment(View view) {
    Config config = new Config();
    TapTapMomentSdk.openTapMoment(config);
    TapTapMomentSdk.setCallback(new TapMomentCallback() {
      @Override
      public void onCallback(int i, String s) {
        Log.e("MainActivity", "onCallback" + "" + i + " _______________   "  + s);
        if (i == 60000) {
          try {
            TapTapMomentSdk.setLoginToken(new AccessToken(s));
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }
}