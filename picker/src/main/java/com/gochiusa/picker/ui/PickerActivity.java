package com.gochiusa.picker.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.gochiusa.picker.R;
import com.gochiusa.picker.entity.ImageRequest;

public class PickerActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ImageRequest mImageRequest = ImageRequest.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 重置主题
        setTheme(mImageRequest.themeId);
        setContentView(R.layout.activity_ui_picker);
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            // 设置图标
            mToolbar.setNavigationIcon(R.drawable.ic_ui_close);
            // 打开返回的菜单按钮
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void initRecyclerView() {

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // 如果点击返回按钮，结束这个Activity
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }
}