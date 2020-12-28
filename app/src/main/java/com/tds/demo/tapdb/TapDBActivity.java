package com.tds.demo.tapdb;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.taptap.sdk.Profile;
import com.tds.demo.R;
import com.tds.tapdb.sdk.TapDB;
import org.json.JSONException;
import org.json.JSONObject;


public class TapDBActivity extends AppCompatActivity {

  public final static String TAG = "TapDBActivity";

  private TextView tipsTextView;
  private EditText setNameValueEditTextView, setLevelValueEditTextView, setServerValueEditTextView;

  private void bindView() {
    tipsTextView = findViewById(R.id.tipsTextView);
    setNameValueEditTextView = findViewById(R.id.setNameValueEditTextView);
    setLevelValueEditTextView = findViewById(R.id.setLevelValueEditTextView);
    setServerValueEditTextView = findViewById(R.id.setServerValueEditTextView);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tapdb);
    bindView();
    if (Profile.getCurrentProfile() == null) {
      tipsTextView.setText("当前未登录，请先回上级页面完成登录");
    }
  }

  public void setName(View view) {
    if (!TextUtils.isEmpty(setNameValueEditTextView.getText().toString())) {
      Log.d(TAG, "set name:" + setNameValueEditTextView.getText().toString());
      TapDB.setName(setNameValueEditTextView.getText().toString());
    }
  }

  public void setLevel(View view) {
    if (!TextUtils.isEmpty(setLevelValueEditTextView.getText().toString())) {
      try {
        TapDB.setLevel(Integer.parseInt(setLevelValueEditTextView.getText().toString()));
        Log.d(TAG, "set name:" + Integer.parseInt(setLevelValueEditTextView.getText().toString()));
      } catch (NumberFormatException e) {
        e.printStackTrace();
        Toast.makeText(TapDBActivity.this, "只能输入蒸熟", Toast.LENGTH_SHORT).show();
      }
    }
  }

  public void setServer(View view) {
    if (!TextUtils.isEmpty(setServerValueEditTextView.getText().toString())) {
      TapDB.setServer(setServerValueEditTextView.getText().toString());
      Log.d(TAG, "set server:" + setServerValueEditTextView.getText().toString());
    }
  }

  public void onCharge(View view) {
    TapDB.onCharge("111111", "product1", 2, "cny", "30");
  }

  public void onEvent(View view) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("eventCode", 22);
      jsonObject.put("eventName", "testEvent");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    TapDB.onEvent("22", jsonObject);
  }
}
