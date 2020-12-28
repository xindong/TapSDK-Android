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

import com.tds.demo.R;
import com.tds.tapdb.sdk.TapDB;


public class TapDBActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    public final static String TAG = "TapDBActivity";

    private EditText setNameValueEditTextView, setLevelValueEditTextView, setServerValueEditTextView;
    private Button submitButton;

    private boolean isSubmitEnabled() {
        return !TextUtils.isEmpty(setNameValueEditTextView.getText().toString())
                || !TextUtils.isEmpty(setLevelValueEditTextView.getText().toString())
                || !TextUtils.isEmpty(setServerValueEditTextView.getText().toString());
    }

    private void bindView() {
        setNameValueEditTextView = findViewById(R.id.setNameValueEditTextView);
        setLevelValueEditTextView = findViewById(R.id.setLevelValueEditTextView);
        setServerValueEditTextView = findViewById(R.id.setServerValueEditTextView);
        submitButton = findViewById(R.id.submitButton);
        setNameValueEditTextView.addTextChangedListener(this);
        setLevelValueEditTextView.addTextChangedListener(this);
        setServerValueEditTextView.addTextChangedListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapdb);
        bindView();
        initView();
    }

    private void initView() {
        submitButton.setEnabled(isSubmitEnabled());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        submitButton.setEnabled(isSubmitEnabled());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitButton:
                if (!TextUtils.isEmpty(setNameValueEditTextView.getText().toString())) {
                    Log.d(TAG, "set name:" + setNameValueEditTextView.getText().toString());
                    TapDB.setName(setNameValueEditTextView.getText().toString());
                }
                if (!TextUtils.isEmpty(setLevelValueEditTextView.getText().toString())) {
                    try {
                        TapDB.setLevel(Integer.parseInt(setLevelValueEditTextView.getText().toString()));
                        Log.d(TAG, "set name:" + Integer.parseInt(setLevelValueEditTextView.getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(TapDBActivity.this, "只能输入蒸熟", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!TextUtils.isEmpty(setServerValueEditTextView.getText().toString())) {
                    TapDB.setServer(setServerValueEditTextView.getText().toString());
                    Log.d(TAG, "set server:" + setServerValueEditTextView.getText().toString());
                }
                break;
            default:
                break;
        }
    }
}
