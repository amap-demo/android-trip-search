package com.amap.tripdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;
import com.amap.poisearch.searchmodule.CityChooseDelegate;
import com.amap.poisearch.searchmodule.CityChooseWidget;
import com.amap.poisearch.searchmodule.ICityChooseModule;
import com.amap.poisearch.searchmodule.ICityChooseModule.IParentDelegate;
import com.amap.poisearch.util.CityModel;
import com.google.gson.Gson;

/**
 * Created by liangchao_suxun on 2017/5/9.
 */

public class ChooseCityActivity extends AppCompatActivity {
    private CityChooseWidget mCityChooseWidget;
    private CityChooseDelegate mCityChooseDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_city_choose);

        RelativeLayout contentView = (RelativeLayout)findViewById(R.id.content_view);

        mCityChooseDelegate = new CityChooseDelegate();
        mCityChooseDelegate.bindParentDelegate(mCityChooseParentDelegate);
        contentView.addView(mCityChooseDelegate.getWidget(this));
    }

    public static final String CURR_CITY_KEY = "curr_city_key";
    @Override
    protected void onResume() {
        super.onResume();
        String currCity = getIntent().getStringExtra(CURR_CITY_KEY);
        if (TextUtils.isEmpty(currCity)) {
            currCity = "北京市";
        }
        mCityChooseDelegate.setCurrCity(currCity);
    }

    private ICityChooseModule.IParentDelegate mCityChooseParentDelegate = new IParentDelegate() {
        @Override
        public void onChooseCity(CityModel city) {
            Intent intent = new Intent();
            intent.putExtra(CURR_CITY_KEY, city);
            ChooseCityActivity.this.setResult(RESULT_OK, intent);
            ChooseCityActivity.this.finish();
            ChooseCityActivity.this.overridePendingTransition(0, R.anim.slide_out_down);
        }

        @Override
        public void onCancel() {
            ChooseCityActivity.this.finish();
            ChooseCityActivity.this.overridePendingTransition(0, R.anim.slide_out_down);
        }
    };

}
