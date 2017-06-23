package com.amap.tripdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.PoiItem;
import com.amap.poisearch.searchmodule.ISearchModule.IDelegate.IParentDelegate;
import com.amap.poisearch.searchmodule.SearchModuleDelegate;
import com.amap.poisearch.util.CityModel;
import com.amap.poisearch.util.FavAddressUtil;
import com.amap.poisearch.util.PoiItemDBHelper;
import com.google.gson.Gson;

import static com.amap.poisearch.searchmodule.ISearchModule.IDelegate.START_POI_TYPE;

/**
 * Created by liangchao_suxun on 2017/5/10.
 */

public class ChoosePoiActivity extends AppCompatActivity {

    private SearchModuleDelegate mSearchModuelDeletage;

    public static int MAIN_ACTIVITY_REQUEST_FAV_ADDRESS_CODE = 1;

    public static int MAIN_ACTIVITY_REQUEST_CHOOSE_CITY_ADDRESS_CODE = 2;

    public static final String POI_TYPE_KEY = "poi_type_key";
    public static final String CITY_KEY = "city_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout contentView = (RelativeLayout)findViewById(R.id.content_view);

        if (getIntent() != null) {
            mPoiType = getIntent().getIntExtra(POI_TYPE_KEY, START_POI_TYPE);
            mCityModel = getIntent().getParcelableExtra(CITY_KEY);
        }

        mSearchModuelDeletage = new SearchModuleDelegate();
        mSearchModuelDeletage.setPoiType(mPoiType);
        mSearchModuelDeletage.setCity(mCityModel);
        mSearchModuelDeletage.bindParentDelegate(mSearchModuleParentDelegate);
        contentView.addView(mSearchModuelDeletage.getWidget(this));
    }

    public AMapLocationClient mLocationClient = null;
    private AMapLocation mCurrLoc = null;

    private int mPoiType;
    private CityModel mCityModel;

    private void initLocationStyle() {
        mLocationClient = new AMapLocationClient(getApplicationContext());

        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (mCurrLoc == null) {
                    mCurrLoc = aMapLocation;
                    mSearchModuelDeletage.setCurrLoc(aMapLocation);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        initLocationStyle();
        mLocationClient.startLocation();
    }

    @Override
    protected void onPause() {
        mCurrLoc = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private SearchModuleDelegate.IParentDelegate mSearchModuleParentDelegate = new IParentDelegate() {
        @Override
        public void onChangeCityName() {
            showToast("选择城市");
            Intent intent = new Intent();
            intent.setClass(ChoosePoiActivity.this, ChooseCityActivity.class);
            intent.putExtra(ChooseCityActivity.CURR_CITY_KEY, mSearchModuelDeletage.getCurrCity().getCity());
            ChoosePoiActivity.this.startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_CHOOSE_CITY_ADDRESS_CODE);
        }

        @Override
        public void onCancel() {
            ChoosePoiActivity.this.finish();
            ChoosePoiActivity.this.overridePendingTransition(0, R.anim.slide_out_down);
        }

        private void toSetFavAddressActivity(int type) {
            Intent intent = new Intent();
            intent.setClass(ChoosePoiActivity.this, SetFavAddressActivity.class);
            intent.putExtra(FAVTYPE_KEY, type);
            Gson gson = new Gson();
            intent.putExtra(SetFavAddressActivity.CURR_CITY_KEY, gson.toJson(mSearchModuelDeletage.getCurrCity()));
            intent.putExtra(SetFavAddressActivity.CURR_LOC_KEY, mCurrLoc);

            startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_FAV_ADDRESS_CODE);
        }
        @Override
        public void onSetFavHomePoi() {
            showToast("设置家的地址");
            toSetFavAddressActivity(0);
        }

        @Override
        public void onSetFavCompPoi() {
            showToast("设置公司地址");
            toSetFavAddressActivity(1);
        }

        @Override
        public void onChooseFavHomePoi(PoiItem poiItem) {
            Intent intent = new Intent();
            intent.putExtra(POI_TYPE_KEY, mPoiType);
            intent.putExtra(POIITEM_OBJECT, poiItem);
            setResult(RESULT_OK, intent);
            ChoosePoiActivity.this.finish();
            ChoosePoiActivity.this.overridePendingTransition(0, R.anim.slide_out_down);
        }

        @Override
        public void onChooseFavCompPoi(PoiItem poiItem) {
            Intent intent = new Intent();
            intent.putExtra(POI_TYPE_KEY, mPoiType);
            intent.putExtra(POIITEM_OBJECT, poiItem);
            setResult(RESULT_OK, intent);
            ChoosePoiActivity.this.finish();
            ChoosePoiActivity.this.overridePendingTransition(0, R.anim.slide_out_down);
        }

        @Override
        public void onSelPoiItem(PoiItem poiItem) {
            saveToCache(poiItem);
            Intent intent = new Intent();
            intent.putExtra(POI_TYPE_KEY, mPoiType);
            intent.putExtra(POIITEM_OBJECT, poiItem);
            setResult(RESULT_OK, intent);
            ChoosePoiActivity.this.finish();
            ChoosePoiActivity.this.overridePendingTransition(0, R.anim.slide_out_down);
        }

        private void saveToCache(PoiItem poiItem) {
            PoiItemDBHelper.getInstance().savePoiItem(ChoosePoiActivity.this, poiItem);
        }
    };


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public static final String FAVTYPE_KEY = "favtype";
    public static final String POIITEM_OBJECT = "poiitem_object";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (MAIN_ACTIVITY_REQUEST_FAV_ADDRESS_CODE == requestCode && resultCode == RESULT_OK) {
            String poiitemStr = data.getStringExtra(POIITEM_OBJECT);
            int favType = data.getIntExtra(FAVTYPE_KEY, -1);

            PoiItem poiItem = new Gson().fromJson(poiitemStr, PoiItem.class);

            if (favType == 0) {
                FavAddressUtil.saveFavHomePoi(this, poiItem);
                mSearchModuelDeletage.setFavHomePoi(poiItem);
            } else if (favType == 1) {
                FavAddressUtil.saveFavCompPoi(this, poiItem);
                mSearchModuelDeletage.setFavCompPoi(poiItem);
            }
        }

        if (MAIN_ACTIVITY_REQUEST_CHOOSE_CITY_ADDRESS_CODE == requestCode && resultCode == RESULT_OK) {
            CityModel cityModel = data.getParcelableExtra(ChooseCityActivity.CURR_CITY_KEY);
            mSearchModuelDeletage.setCity(cityModel);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
