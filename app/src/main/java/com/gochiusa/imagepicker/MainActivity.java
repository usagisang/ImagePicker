package com.gochiusa.imagepicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gochiusa.picker.ImagePicker;
import com.gochiusa.picker.ui.PickerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 1;
    /**
     *  获取图片选择的请求
     */
    private static final int IMAGE_RESULT_REQUEST = 100;
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
                ImagePicker.from(this).choose(true).setMaxCount(9)
                        .setLIFO(true).forResult(IMAGE_RESULT_REQUEST);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImagePicker.from(this).choose(false).forResult(IMAGE_RESULT_REQUEST);
            } else {
                Toast.makeText(this, mRefuseTip, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_RESULT_REQUEST) {
            if (resultCode == RESULT_OK && data != null
                    && data.getParcelableArrayExtra(PickerActivity.SELECTION_KEY) != null) {
                // 获取数据
                Parcelable[] parcelableArray =
                        data.getParcelableArrayExtra(PickerActivity.SELECTION_KEY);
                List<Uri> uriList = new ArrayList<>();
                for (Parcelable parcelable : parcelableArray) {
                    if (parcelable instanceof Uri) {
                        Uri uri = (Uri) parcelable;
                        uriList.add(uri);
                    }
                }
                // 显示信息在TextView上
                mTextView.setText(String.format(Locale.CHINA,
                        "拿到选择的图片%d张", uriList.size()));
            } else if (resultCode == RESULT_CANCELED) {
                mTextView.setText("已取消选择图片");
            } else {
                mTextView.setText("获取框架返回的数据失败");
            }

        }
    }
}