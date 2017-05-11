package com.amap.tripmodule;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.poisearch.searchmodule.AMapSearchUtil;
import com.amap.poisearch.searchmodule.AMapSearchUtil.OnLatestPoiSearchListener;
import com.amap.poisearch.util.CityModel;
import com.amap.poisearch.util.CityUtil;
import com.amap.tripmodule.ITripHostModule.IDelegate;
import com.amap.tripmodule.ITripHostModule.IParentDelegate;
import com.amap.tripmodule.ITripHostModule.IWidget;

/**
 * Created by liangchao_suxun on 2017/5/8.
 */

public class TripHostModuleDelegate implements IDelegate {

    private IWidget mWidget;
    private IParentDelegate mParentDelegate;
    private Context mContext;
    private CityModel mCurrCity;

    @Override
    public void bindParentDelegate(IParentDelegate parentTripDelegate) {
        this.mParentDelegate = parentTripDelegate;
    }

    private int mMode = 0;
    @Override
    public void setMode(int mode , PoiItem oriStartPoi) {
        if (mMode == mode) {
            return;
        }

        mWidget.setMode(mode);
        if (mode == INPUT_MODE) {
            // 重置地图camera的位置,如果之前选择了startpoi，则重置到之前的startpoi，否则回到用户的定位点
            if (oriStartPoi != null) {
                mWidget.setMapCameraPos(new LatLng(oriStartPoi.getLatLonPoint().getLatitude(),
                    oriStartPoi.getLatLonPoint().getLongitude()));
                mWidget.setStartLocation(oriStartPoi.getTitle());
            } else if (mCurrLoc != null) {
                mWidget.onLocationChanged(mCurrLoc);
                mWidget.setStartLocation("正在获取上车地点");
            }
            mWidget.setDestLocation(null);
        }

        mMode = mode;
    }

    @Override
    public void moveCameraPosTo(LatLng latLng) {
        mWidget.setMapCameraPos(latLng);
    }

    @Override
    public int getMode() {
        if (mMode != INPUT_MODE && mMode != SHOW_RES_MODE) {
            mMode = INPUT_MODE;
        }

        return mMode;
    }

    @Override
    public View getWidget(Context context) {
        mContext = context;

        if (mWidget == null) {
            mWidget = new TripHostModuleWidget(context);
            mWidget.bindDelegate(this);

            mCurrCity = CityUtil.getDefCityModel(mContext);
            mWidget.setCity(mCurrCity);
        }

        return (View)mWidget;
    }

    @Override
    public CityModel getCurrCity() {
        return mCurrCity;
    }

    @Override
    public void setCurrCity(CityModel city) {
        this.mCurrCity = city;

        //切换城市
        mWidget.setCity(city);
        mWidget.setStartLocation("正在获取上车地点");
        doGeoSearch(mContext, city.getLat(), city.getLng());
    }

    @Override
    public void onChooseCity() {
        if (mParentDelegate == null) {
            return;
        }
        mParentDelegate.onChooseCity();
    }

    @Override
    public void onClickUserIcon() {
        if (mParentDelegate == null) {
            return;
        }
        mParentDelegate.onIconClick();
    }

    @Override
    public void onClickMsgIcon() {
        if (mParentDelegate == null) {
            return;
        }
        mParentDelegate.onMsgClick();
    }

    @Override
    public void onChooseStartPoi() {
        mParentDelegate.onChooseStartPoi();
    }

    @Override
    public void onBackToInputMode() {
        mParentDelegate.onBackToInputMode();
    }

    @Override
    public void onChooseDestPoi() {
        mParentDelegate.onChooseDestPoi();
    }

    @Override
    public void onCameraChange(LatLng camPos) {
        //如果是结果展示模式，则忽略onCameraChange
        if (mMode == SHOW_RES_MODE) {
            return;
        }

        mWidget.setStartLocation("正在获取上车地点");
        doGeoSearch(mContext, camPos.latitude, camPos.longitude);
    }

    @Override
    public void setStartLocation(String location) {
        mWidget.setStartLocation(location);
    }

    @Override
    public void setDestLocation(String location) {
        mWidget.setDestLocation(location);
    }

    @Override
    public void setSCMarkerVisible(int visible) {
        mWidget.setSCMarkerVisible(visible);
    }

    @Override
    public void showPoiRes(LatLng startLL, LatLng destLL) {

        mWidget.showPoiRes(startLL, destLL);
        setSCMarkerVisible(View.GONE);

        //切换模式,进入结果展示模式
        setMode(IDelegate.SHOW_RES_MODE, null);
    }

    @Override
    public void onReLocate() {
        mWidget.setStartLocation(mCurrLoc.getPoiName());
        mWidget.onLocationChanged(mCurrLoc);

        resetCityModelByLocation(mCurrLoc);
    }

    private AMapLocationClient mLocationClient;
    private AMapLocation mCurrLoc;
    private double mIgnoreLLDiff = 0.001;

    @Override
    public void onCreate(Bundle bundle) {
        mWidget.onCreate(bundle);
        mLocationClient = new AMapLocationClient(mContext.getApplicationContext());
        mWidget.onLocationChanged(null);
        mWidget.setStartLocation("正在获取上车地点");

        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
                    Toast.makeText(mContext, aMapLocation.getErrorInfo()
                        , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (aMapLocation == null) {
                    return;
                }

                if (mCurrLoc == null || (mCurrLoc.getLatitude() - aMapLocation.getLatitude() >= mIgnoreLLDiff
                    || mCurrLoc.getLongitude() - aMapLocation.getLongitude() >= mIgnoreLLDiff)) {
                    mCurrLoc = aMapLocation;
                    mWidget.setStartLocation(mCurrLoc.getPoiName());
                    mWidget.onLocationChanged(mCurrLoc);

                    resetCityModelByLocation(aMapLocation);

                    PoiItem poiItem = new PoiItem(System.currentTimeMillis() + "",
                        new LatLonPoint(mCurrLoc.getLatitude(), mCurrLoc.getLongitude()), mCurrLoc.getPoiName(),
                        mCurrLoc.getAddress());
                    mParentDelegate.onStartPoiChange(poiItem);
                }
            }

        });
        mLocationClient.startLocation();
    }

    /** 设置城市。如果定位结果返回的城市和当前城市不同，则进行修正*/
    private void resetCityModelByLocation(AMapLocation location) {
        if (location == null || location.getCityCode() == null) {
            return;
        }

        CityModel cityModel = CityUtil.getCityByCode(mContext, location.getCityCode());
        if (!mCurrCity.getAdcode().equals(cityModel.getAdcode())) {
            mCurrCity = cityModel;
            mWidget.setCity(mCurrCity);
        }
    }


    @Override
    public void onResume() {
        mWidget.onResume();
    }

    @Override
    public void onPause() {
        mWidget.onPause();
    }

    @Override
    public void onDestroy() {
        mCurrLoc = null;
        mWidget.onDestroy();
    }

    private long mSearchId = 0;

    /**
     * 根据latlng获得PoiItem
     * @param context
     * @param lat
     * @param lng
     */
    private void doGeoSearch(Context context, double lat, double lng) {
        mSearchId = java.lang.System.currentTimeMillis();
        AMapSearchUtil.doGeoSearch(context, lat, lng , mSearchId,
            new OnLatestPoiSearchListener() {
                @Override
                public void onPoiSearched(PoiResult var1, int var2, long searchId) {
                    ;
                }

                @Override
                public void onPoiItemSearched(com.amap.api.services.core.PoiItem var1, int var2, long searchId) {
                    //只获得最新的结果
                    if (searchId < mSearchId) {
                        return;
                    }

                    if (var1 == null) {
                        return;
                    }

                    mWidget.setStartLocation(var1.getTitle());
                    mParentDelegate.onStartPoiChange(var1);
                }
            });
    }

    private void showMsg(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
