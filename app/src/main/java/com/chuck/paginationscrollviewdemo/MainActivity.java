package com.chuck.paginationscrollviewdemo;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import com.chuck.itemfoundation.builder.PaginationProfile;
import com.chuck.paginationscrollview.data.Item;
import com.chuck.popupexchange.PopUpExchangeView;
import com.chuck.utils.LogUtils;
import com.chuck.paginationscrollview.view.PaginationScrollView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private final String pkgName = "com.chuck.paginationscrollview";

    private final Item[] fruits = new Item[]{
            new Item("菠萝", R.drawable.boluo, "com.chuck.paginationscrollview.activity.FruitActivity1", pkgName),
            new Item("草莓", R.drawable.caomei, "com.chuck.paginationscrollview.activity.FruitActivity2", pkgName),
            new Item("哈密瓜", R.drawable.hamigua, "com.chuck.paginationscrollview.activity.FruitActivity3", pkgName),
            new Item("火龙果", R.drawable.huolongguo, "com.chuck.paginationscrollview.activity.FruitActivity4", pkgName),
            new Item("蓝莓", R.drawable.lanmei, "com.chuck.paginationscrollview.activity.FruitActivity5", pkgName),
            new Item("梨", R.drawable.li, "com.chuck.paginationscrollview.activity.FruitActivity6", pkgName),
            new Item("芒果", R.drawable.mangguo, "com.chuck.paginationscrollview.activity.FruitActivity7", pkgName),
            new Item("猕猴桃", R.drawable.mihoutao, "com.chuck.paginationscrollview.activity.FruitActivity8", pkgName),
            new Item("柠檬", R.drawable.ningmeng, "com.chuck.paginationscrollview.activity.FruitActivity9", pkgName),
            new Item("苹果", R.drawable.pingguo, "com.chuck.paginationscrollview.activity.FruitActivity10", pkgName),
            new Item("葡萄", R.drawable.putao, "com.chuck.paginationscrollview.activity.FruitActivity11", pkgName),
            new Item("石榴", R.drawable.shiliu, "com.chuck.paginationscrollview.activity.FruitActivity12", pkgName),
            new Item("西瓜", R.drawable.shizi, "com.chuck.paginationscrollview.activity.FruitActivity13", pkgName),
            new Item("桃子", R.drawable.taozi, "com.chuck.paginationscrollview.activity.FruitActivity14", pkgName),
            new Item("香蕉", R.drawable.xiangjiao, "com.chuck.paginationscrollview.activity.FruitActivity15", pkgName),
            new Item("西瓜", R.drawable.xigua, "com.chuck.paginationscrollview.activity.FruitActivity16", pkgName),
            new Item("杨桃", R.drawable.yangmei, "com.chuck.paginationscrollview.activity.FruitActivity17", pkgName),
            new Item("椰子", R.drawable.yezi, "com.chuck.paginationscrollview.activity.FruitActivity18", pkgName),
            new Item("樱桃", R.drawable.yingtao, "com.chuck.paginationscrollview.activity.FruitActivity19", pkgName),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int row = 3;
        final int column = 5;

        // 获取屏幕分辨率
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        LogUtils.d(TAG, "device screen size: " + screenWidth + "x" + screenHeight);

        PaginationProfile paginationProfile = (PaginationProfile) new PaginationProfile.Builder()
                .setHeightPx(screenHeight)
                .setWidthPx(screenWidth)
                .setCellHeightPx(360)
                .setCellWidthPx(384)
                .setNumColumns(column)
                .setNumRows(row)
                .setIconDrawablePaddingPx(2)
                .setDefaultPageSpacingPx(2)
                .setEdgeMarginPx(2)
                .setIconSizePx(108)
                .setIconTextSizePx(16)
                .setCellTextColor(getColor(R.color.black))
                .setWorkspacePadding(new Rect(0, 0, 0, 0))
                .setSaveDataInDb(true)
                .build();

        setContentView(R.layout.activity_main);

        PaginationScrollView paginationScrollView = findViewById(R.id.pagination_scroll_view);
        PopUpExchangeView.getInstance().init(this);

//        PageItemsDataManager pageItemsDataManager = PageItemsDataManager.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("PaginationScrollView", MODE_PRIVATE);
        boolean dataSaved = sharedPreferences.getBoolean("data_saved", false);
        if (dataSaved) {
            paginationScrollView.bindItems();
        } else {
            paginationScrollView.bindItems(fruits);
            sharedPreferences.edit().putBoolean("data_saved", true).apply();
        }

        FloatingActionButton popupButton = findViewById(R.id.show_pop_window);
        popupButton.setOnClickListener(v -> {
            PopUpExchangeView.getInstance().showOrClosePopWindow();
        });
    }
}