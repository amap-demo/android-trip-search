package com.amap.tripmodule;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amap.tripmodule.ITripHostModule.IDelegate;

/**
 * Created by liangchao_suxun on 2017/5/9.
 */

public class TitleBarWidget extends RelativeLayout implements View.OnClickListener{

    public TitleBarWidget(Context context) {
        super(context);
        init();
    }

    public TitleBarWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleBarWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ImageView mUserIconIV;
    private ImageView mMsgIV;

    private TextView mCityNameTV;
    private View mCityNameView;

    private ImageView mBackIV;
    private TextView mTitleTV;

    private void init(){
        int padding = (int)getContext().getResources().getDimension(R.dimen.padding);
        setPadding(padding, 0, padding, 0);

        LayoutInflater.from(getContext()).inflate(R.layout.widget_titlebar, this);

        mUserIconIV = (ImageView)findViewById(R.id.user_icon);
        mMsgIV = (ImageView)findViewById(R.id.msg_icon);
        mCityNameTV = (TextView)findViewById(R.id.cityname_tv);
        mCityNameView = findViewById(R.id.cityname_ll);

        mBackIV = (ImageView)findViewById(R.id.back_iv);
        mTitleTV = (TextView)findViewById(R.id.title_tv);

        mUserIconIV.setOnClickListener(this);
        mMsgIV.setOnClickListener(this);
        mCityNameView.setOnClickListener(this);
        mBackIV.setOnClickListener(this);
        mTitleTV.setOnClickListener(this);
    }

    private int mMode = 0;
    public void setMode(int mode) {
        mMode = mode;

        if (mMode == IDelegate.INPUT_MODE) {
            mBackIV.setVisibility(View.GONE);
            mTitleTV.setVisibility(View.GONE);

            mUserIconIV.setVisibility(View.VISIBLE);
            mCityNameView.setVisibility(View.VISIBLE);
        } else if (mMode == IDelegate.SHOW_RES_MODE) {
            mBackIV.setVisibility(View.VISIBLE);
            mTitleTV.setVisibility(View.VISIBLE);

            mUserIconIV.setVisibility(View.GONE);
            mCityNameView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (mParentWidget == null) {
            return;
        }

        if (R.id.user_icon == v.getId()) {
            mParentWidget.onClickUserIcon();
        } else if (R.id.msg_icon == v.getId()) {
            mParentWidget.onClickMsg();
        } else if (R.id.cityname_ll == v.getId()) {
            mParentWidget.onClickChooseCity();
        } else if (R.id.back_iv == v.getId()) {
            mParentWidget.onBack();
        }
    }

    private ParentWidget mParentWidget;

    public void setCityName(String cityName) {
        if (TextUtils.isEmpty(cityName)) {
            return;
        }

        mCityNameTV.setText(cityName);
    }

    public void setParentWidget(ParentWidget mParentWidget) {
        this.mParentWidget = mParentWidget;
    }

    public interface ParentWidget{
        public void onClickUserIcon();
        public void onClickMsg();
        public void onClickChooseCity();
        public void onBack();
    }
}
