package com.gochiusa.picker.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.gochiusa.picker.R;
import com.gochiusa.picker.entity.ImageRequest;
import com.gochiusa.picker.model.SelectedItemCollection;
import com.gochiusa.picker.ui.fragment.AlbumFragment;
import com.gochiusa.picker.util.FragmentManageUtil;

import java.util.Observable;
import java.util.Observer;

public class PickerActivity extends AppCompatActivity implements Observer {

    /**
     *  界面上的控件
     */
    private Toolbar mToolbar;
    private Button mConfirmButton;

    private SelectedItemCollection mItemCollection;
    /**
     *  最大可选数
     */
    private static final int MAX_COUNT = ImageRequest.getInstance().maxSelectable;

    private static final String EMPTY_TIP = "至少需要选择一张图片才能提交";

    /**
     *  请求结果对应的键值
     */
    public static final String SELECTION_KEY  = "selection_key";
    

    private ImageRequest mImageRequest = ImageRequest.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 重置主题
        setTheme(mImageRequest.themeId);
        setContentView(R.layout.activity_ui_picker);
        // 获取存放选择项的集合
        mItemCollection = SelectedItemCollection.getInstance();
        // 注册观察者
        mItemCollection.addObserver(this);
        // 设置全局的碎片管理器
        FragmentManageUtil.setFragmentManager(getSupportFragmentManager());
        initToolbar();
        initFragment();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.tb_picker);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            // 打开返回的菜单按钮
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void initFragment() {
        // 打开显示相册的碎片
        AlbumFragment albumFragment = new AlbumFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_ui_container, albumFragment).commit();
    }


    private void initConfirmButton(Button button) {
        button.setText(getString(R.string.picker_ok));
        // 设置按钮文字为黑色
        button.setTextColor(Color.BLACK);
        // 设置点击事件
        button.setOnClickListener((view) -> {
            if (mItemCollection.getSize() == 0) {
                // 弹出选择项目为空的提示
                Toast.makeText(this, EMPTY_TIP, Toast.LENGTH_SHORT).show();
            } else {
                // 设置选择的结果
                Intent intent = new Intent();
                intent.putExtra(SELECTION_KEY, mItemCollection.getUriArray());
                // 返回结果
                setResult(Activity.RESULT_OK, intent);
                clear();
                finish();
            }
        });
    }

    /**
     *  将一些全局性的对象清除，避免下次打开时，数据与上一次混淆
     */
    private static void clear() {
        SelectedItemCollection.clearCollection();
        FragmentManageUtil.setFragmentManager(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_toolbar_menu, menu);
        // 找到标题栏上的button
        mConfirmButton = (Button) menu.findItem(R.id.menu_picker_toolbar_confirm).getActionView();
        initConfirmButton(mConfirmButton);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }


    /**
     *  侦听选择的集合中每一次的增加和删除，并及时更新按钮的文字
     */
    @Override
    public void update(Observable o, Object arg) {
        mConfirmButton.setText(getString(
                R.string.picker_ok_with_count, mItemCollection.getSize(), MAX_COUNT));
    }
}