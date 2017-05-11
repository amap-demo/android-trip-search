package com.amap.tripmodule;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.poisearch.util.CityModel;
import com.amap.tripmodule.ChooseLocationWidget.IParentWidget;
import com.amap.tripmodule.HostMapWidget.ParentWidget;
import com.amap.tripmodule.ITripHostModule.IDelegate;
import com.amap.tripmodule.ITripHostModule.IWidget;

/**
 * Created by liangchao_suxun on 2017/5/8.
 */

public class TripHostModuleWidget extends RelativeLayout implements IWidget, View.OnClickListener {

    public TripHostModuleWidget(Context context) {
        super(context);
        init();
    }

    public TripHostModuleWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TripHostModuleWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int mMode;
    private IDelegate mDelegate;
    private TitleBarWidget mTitleBarWidget;
    private HostMapWidget mHostMapWidget;
    private ChooseLocationWidget mChooseLocationWidget;
    private ImageView mLocIV;
    private ImageView mSCMarkerIV;

    private void init() {
        mMode = IDelegate.INPUT_MODE;

        LayoutInflater.from(getContext()).inflate(R.layout.widget_triphost, this);
        setBackgroundColor(Color.GREEN);

        mTitleBarWidget = (TitleBarWidget)findViewById(R.id.title_bar);
        mTitleBarWidget.setParentWidget(mTitleBarParentWidget);

        mHostMapWidget = (HostMapWidget)findViewById(R.id.host_map_widget);
        mHostMapWidget.setParentWidget(mMapParentWidget);

        mLocIV = (ImageView)findViewById(R.id.loc_iv);
        mLocIV.setOnClickListener(this);

        mSCMarkerIV = (ImageView)findViewById(R.id.loc_marker_iv);

        mChooseLocationWidget = (ChooseLocationWidget)findViewById(R.id.choose_location_widget);
        mChooseLocationWidget.setParentWidget(mChooseLocationParentWidget);
    }

    public void setSCMarkerVisible(int visible){
        mSCMarkerIV.setVisibility(visible);
    }

    @Override
    public void setStartLocation(String location) {
        mChooseLocationWidget.setStartLocation(location);
    }

    @Override
    public void setDestLocation(String location) {
        mChooseLocationWidget.setDestLocation(location);
    }

    @Override
    public void setMapCameraPos(LatLng latLng) {
        if (latLng == null) {
            return;
        }

        mHostMapWidget.setMapCameraPos(latLng);
    }

    @Override
    public void bindDelegate(IDelegate delegate) {
        this.mDelegate = delegate;
    }

    @Override
    public void setMode(int mode) {
        if (mMode == mode) {
            return;
        }

        mMode = mode;
        if (mMode == IDelegate.INPUT_MODE) {
            mLocIV.setClickable(true);
            mSCMarkerIV.setVisibility(View.VISIBLE);
            mHostMapWidget.removeStartAndDestMarkers();
        } else if (mMode == IDelegate.SHOW_RES_MODE) {
            mLocIV.setClickable(false);
            mSCMarkerIV.setVisibility(View.GONE);
        }

        mTitleBarWidget.setMode(mode);
        mChooseLocationWidget.setMode(mode);
    }

    @Override
    public void setCity(CityModel cityModel) {
        if (cityModel == null) {
            return;
        }

        mTitleBarWidget.setCityName(cityModel.getCity());
        LatLng destLL = new LatLng(cityModel.getLat(), cityModel.getLng());
        mHostMapWidget.setMapCameraPos(destLL);
    }

    @Override
    public void onCreate(Bundle bundle) {
        mHostMapWidget.onCreate(bundle);
    }

    @Override
    public void onResume() {
        mHostMapWidget.onResume();
    }

    @Override
    public void onPause() {
        mHostMapWidget.onPause();
    }

    @Override
    public void onDestroy() {
        mHostMapWidget.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mHostMapWidget.onLocationChanged(aMapLocation);
    }

    @Override
    public void showPoiRes(LatLng startLL, LatLng destLL) {
        mHostMapWidget.showPoiRes(startLL, destLL);
    }

    @Override
    public void removeStartAndDestMarkers() {
        mHostMapWidget.removeStartAndDestMarkers();
    }

    public TitleBarWidget.ParentWidget mTitleBarParentWidget = new TitleBarWidget.ParentWidget() {
        @Override
        public void onClickUserIcon() {
            if (mDelegate == null) {
                return;
            }
            mDelegate.onClickUserIcon();
        }

        @Override
        public void onClickMsg() {
            if (mDelegate == null) {
                return;
            }
            mDelegate.onClickMsgIcon();
        }

        @Override
        public void onClickChooseCity() {
            if (mDelegate == null) {
                return;
            }
            mDelegate.onChooseCity();
        }

        @Override
        public void onBack() {
            mDelegate.onBackToInputMode();
        }
    };

    public ParentWidget mMapParentWidget = new ParentWidget() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            //如果是展示结果的模式，则忽略onCameraChange
            if (mMode == IDelegate.SHOW_RES_MODE) {
                return;
            }

            mDelegate.onCameraChange(cameraPosition.target);
        }
    };

    private ChooseLocationWidget.IParentWidget mChooseLocationParentWidget = new IParentWidget() {
        @Override
        public void onChooseDestLoc() {
            mDelegate.onChooseDestPoi();
        }

        @Override
        public void onChooseStartLoc() {
            mDelegate.onChooseStartPoi();
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loc_iv) {
            mDelegate.onReLocate();
        }
    }
}
