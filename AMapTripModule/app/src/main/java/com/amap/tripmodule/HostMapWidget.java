package com.amap.tripmodule;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

/**
 * Created by liangchao_suxun on 2017/5/9.
 */

public class HostMapWidget extends MapView {

    public HostMapWidget(Context context) {
        super(context);
        init();
    }

    public HostMapWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public HostMapWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        getMap().getUiSettings().setZoomControlsEnabled(false);
        getMap().getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        getMap().setOnCameraChangeListener(mOnCameraChangeListener);
        getMap().setPointToCenter(getWidth() / 2, getHeight() / 2);
        getMap().getUiSettings().setGestureScaleByMapCenter(true);
    }

    private Marker mUserLocMarker;
    private Marker mStartLocMarker;
    private Marker mDestLocMarker;

    private static final int ZOOM_LEVEL_AFTER_LOCATION = 16;

    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            return;
        }

        if (mUserLocMarker == null) {
            int size = (int)getContext().getResources().getDimension(R.dimen.user_icon_marker_size);
            mUserLocMarker = addUserIconMarker(R.mipmap.user_loc_marker_icon, size, size,
                new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 0.5f, 0.5f);
            mUserLocMarker.setZIndex(USER_ICON_MASRKER_Z_INDEX);
        }

        mUserLocMarker.setPosition(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
        getMap().animateCamera(
            CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()),
                ZOOM_LEVEL_AFTER_LOCATION));
    }

    public void onStartLocChanged(LatLng latLng){
        getMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private Marker addUserIconMarker(int iconRes, int destWidth, int destHeight, LatLng latLng, float ax, float ay) {
        InputStream var2 = getContext().getResources().openRawResource(iconRes);
        Bitmap var3 = BitmapFactory.decodeStream(var2);
        Bitmap scaledBM = Bitmap.createScaledBitmap(var3, destWidth, destHeight, false);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBM);
        return getMap().addMarker(new MarkerOptions().icon(bitmapDescriptor).anchor(ax, ay).position(latLng));
    }

    private Marker addLocMarker(int iconRes, int destWidth, int destHeight, LatLng latLng) {
        InputStream var2 = getContext().getResources().openRawResource(iconRes);
        Bitmap var3 = BitmapFactory.decodeStream(var2);
        Bitmap scaledBM = Bitmap.createScaledBitmap(var3, destWidth, destHeight, false);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBM);
        return getMap().addMarker(new MarkerOptions().icon(bitmapDescriptor).position(latLng));
    }

    public void setMapCameraPos(LatLng latLng) {
        getMap().moveCamera(
            CameraUpdateFactory
                .newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), ZOOM_LEVEL_AFTER_LOCATION));

    }

    private static final int USER_ICON_MASRKER_Z_INDEX = 0;
    private static final int LOC_ICON_MASRKER_Z_INDEX = 1;

    public void showStartMarker(LatLng latlng) {
        //如果latlng为null，则移除marker
        if (latlng == null && mStartLocMarker != null) {
            mStartLocMarker.setVisible(false);
            return;
        }

        double lat = latlng.latitude;
        double lng = latlng.longitude;
        if (mStartLocMarker == null) {
            int startMarkerW = (int)getContext().getResources().getDimension(R.dimen.start_loc_marker_w);
            int startMarkerH = (int)getContext().getResources().getDimension(R.dimen.start_loc_marker_h);
            mStartLocMarker = addLocMarker(R.mipmap.start_marker_icon, startMarkerW, startMarkerH,
                new LatLng(lat, lng));
            mStartLocMarker.setZIndex(LOC_ICON_MASRKER_Z_INDEX);
        }

        mStartLocMarker.setVisible(true);
        mStartLocMarker.setPosition(new LatLng(lat, lng));
    }

    public void showDestMarker(LatLng latlng) {
        if (latlng == null && mDestLocMarker != null) {
            mDestLocMarker.setVisible(false);
            return;
        }

        double lat = latlng.latitude;
        double lng = latlng.longitude;
        if (mDestLocMarker == null) {
            int startMarkerW = (int)getContext().getResources().getDimension(R.dimen.start_loc_marker_w);
            int startMarkerH = (int)getContext().getResources().getDimension(R.dimen.start_loc_marker_h);
            mDestLocMarker = addLocMarker(R.mipmap.dest_marker_icon, startMarkerW, startMarkerH, new LatLng(lat, lng));
            mDestLocMarker.setZIndex(LOC_ICON_MASRKER_Z_INDEX);
        }

        mDestLocMarker.setVisible(true);
        mDestLocMarker.setPosition(new LatLng(lat, lng));
    }

    public void showPoiRes(LatLng startLL, LatLng destLL) {
        showStartMarker(startLL);
        showDestMarker(destLL);

        LatLngBounds latLngBounds = new LatLngBounds.Builder().include(startLL).include(destLL).build();
        int mapPadding = (int)getContext().getResources().getDimension(R.dimen.map_padding);
        getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, mapPadding));
    }

    private OnCameraChangeListener mOnCameraChangeListener = new OnCameraChangeListener() {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            if (mParentWidget == null) {
                return;
            }
            mParentWidget.onCameraChange(cameraPosition);
        }
    };

    public void removeStartAndDestMarkers() {
        if (mStartLocMarker != null) {
            mStartLocMarker.setVisible(false);
        }

        if (mDestLocMarker != null) {
            mDestLocMarker.setVisible(false);
        }
    }

    private ParentWidget mParentWidget;

    public void setParentWidget(ParentWidget mParentWidget) {
        this.mParentWidget = mParentWidget;
    }

    public interface ParentWidget {
        public void onCameraChange(CameraPosition cameraPosition);
    }

}
