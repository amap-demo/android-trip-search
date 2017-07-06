package com.amap.tripmodule;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.poisearch.util.CityModel;

/**
 * Created by liangchao_suxun on 2017/5/8.
 */

public interface ITripHostModule {

    public interface IParentDelegate {

        /**
         * 用户点击了头像按钮
         */
        public void onIconClick();

        /**
         * 用户点击了消息中心的按钮
         */
        public void onMsgClick();

        /**
         * 用户点击了切换城市
         */
        public void onChooseCity();

        /**
         * 选择目的地
         */
        public void onChooseDestPoi();

        /**
         * 选择开始地
         */
        public void onChooseStartPoi();

        /**
         * 用户点击撤销，进入输入模式
         */
        public void onBackToInputMode();

        /**
         * 由于定位或者用户滑动屏幕造成初始点切换
         */
        public void onStartPoiChange(PoiItem poiItem);

        /**
         * 开始交车的回调
         */
        public void onStartCall();
    }

    public interface IWidget {

        public void bindDelegate(IDelegate delegate);

        /**
         * 分为输入模式和结果展示模式
         */
        public void setMode(int mode);

        /**
         * 设置城市
         */
        public void setCity(CityModel cityModel);

        /**
         * 设置开始地点
         */
        public void setStartLocation(String location);

        /**
         * 设置结束地点
         */
        public void setDestLocation(String location);

        /**
         * 设置地图camera位置
         */
        public void setMapCameraPos(LatLng latLng);

        /**
         * 设置屏幕中心的marker是否可见
         */
        public void setSCMarkerVisible(int visible);

        /**
         * MapView 需要的生命周期hook
         */
        public void onCreate(Bundle bundle);

        /**
         * MapView 需要的生命周期hook
         */
        public void onResume();

        /**
         * MapView 需要的生命周期hook
         */
        public void onPause();

        /**
         * MapView 需要的生命周期hook
         */
        public void onDestroy();

        /**
         * 用户定位发生了变化
         */
        public void onLocationChanged(AMapLocation aMapLocation);

        /**
         * 起始点发生了变化
         * @param latLng
         */
        public void onStartLocChanged(LatLng latLng);

        /**
         * 展示选择的结果
         */
        public void showPoiRes(LatLng startLL, LatLng destLL);

        /**
         * 删除开始和结束地点的marker
         */
        public void removeStartAndDestMarkers();

        /**
         * geo结果返回触发了地图移动，忽略一次CameraChangeFinish的回调
         */
        public void ignoreCamereMoveOnce();
    }

    public interface IDelegate {

        /**
         * 输入模式。用户可以进行定位，设置起始和结束点，进行重定位
         */
        public static final int INPUT_MODE = 0;

        /**
         * 结果展示模式。只能查看结果
         */
        public static final int SHOW_RES_MODE = 1;

        /**
         * 绑定parentdelegate
         */
        public void bindParentDelegate(IParentDelegate parentDelegate);

        /**
         * 分为输入模式和结果展示模式*
         * @param mode       模式
         * @param oriStartPoi 之前用户选择起始点的PoiItem
         */
        public void setMode(int mode, PoiItem oriStartPoi);

        /**
         * 移动地图中心点
         * @param latLng
         */
        public void moveCameraPosTo(LatLng latLng);

        /**
         * 获得模式
         */
        public int getMode();

        /**
         * 获得View
         */
        public View getWidget(Context context);

        /**
         * 获得当前城市
         */
        public CityModel getCurrCity();

        /**
         * 获得用户的定位地址
         * @return
         */
        AMapLocation getCurrLocation();

        /**
         * 设置当前城市
         */
        public void setCurrCity(CityModel city);

        /**
         * 用户点击了切换城市
         */
        public void onChooseCity();

        /**
         * 点击了用户中心
         */
        public void onClickUserIcon();

        /**
         * 点击了信息
         */
        public void onClickMsgIcon();

        /**
         * 选择结束的地点
         */
        public void onChooseDestPoi();

        /**
         * 选择开始的地点
         */
        public void onChooseStartPoi();

        /**
         * 用户点击撤销，进入输入模式
         */
        public void onBackToInputMode();

        /**
         * cameraposition 被改变
         */
        public void onCameraChange(LatLng camPos);

        /**
         * 设置开始地点
         */
        public void setStartLocation(String location);

        /**
         * 设置结束地点
         */
        public void setDestLocation(String location);

        /**
         * 设置屏幕中心的marker是否可见
         */
        public void setSCMarkerVisible(int visible);

        /**
         * 在mapview上展示选择的结果
         */
        public void showPoiRes(LatLng startLL, LatLng destLL);

        /**
         * 重新定位到当前位置
         */
        public void onReLocate();

        /**
         * MapView 需要的生命周期hook
         */
        public void onCreate(Bundle bundle);

        /**
         * MapView 需要的生命周期hook
         */
        public void onResume();

        /**
         * MapView 需要的生命周期hook
         */
        public void onPause();

        /**
         * MapView 需要的生命周期hook
         */
        public void onDestroy();
    }

}
