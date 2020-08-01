package com.gochiusa.imagepicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gochiusa.picker.ImagePicker;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 1;
    private static final int RESULT_REQUEST = 100;
    private String mRefuseTip = "拒绝权限将无法使用该功能";

    private Button mButton;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.btn_main);
        mTextView = findViewById(R.id.tv_main);

        mButton.setOnClickListener((view) -> {
            // 如果没有获得读写的权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                ImagePicker.from(this).choose().forResult(RESULT_REQUEST);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImagePicker.from(this).choose().forResult(RESULT_REQUEST);
            } else {
                Toast.makeText(this, mRefuseTip, Toast.LENGTH_SHORT).show();
            }
        }
    }
}