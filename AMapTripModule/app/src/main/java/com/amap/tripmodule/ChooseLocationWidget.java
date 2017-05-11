package com.amap.tripmodule;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amap.tripmodule.ITripHostModule.IDelegate;

/**
 * Created by liangchao_suxun on 2017/5/9.
 */

public class ChooseLocationWidget extends LinearLayout implements View.OnClickListener {

    public ChooseLocationWidget(Context context) {
        super(context);
        init();
    }

    public ChooseLocationWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChooseLocationWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ChooseLocationItemWidget mSourceItem;
    private ChooseLocationItemWidget mDestItem;

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundResource(R.color.common_bg);

        LayoutInflater.from(getContext()).inflate(R.layout.widget_choose_location, this);
        mSourceItem = (ChooseLocationItemWidget)findViewById(R.id.choose_source_item_widget);
        mDestItem = (ChooseLocationItemWidget)findViewById(R.id.choose_dest_item_widget);

        mSourceItem.setOnClickListener(this);
        mDestItem.setOnClickListener(this);

        mSourceItem.setType(ChooseLocationItemWidget.SOURCE_TYPE);
        mDestItem.setType(ChooseLocationItemWidget.DEST_TYPE);
    }

    private int mMode = 0;
    public void setMode(int mode) {
        mMode = mode;

        if (mMode == IDelegate.INPUT_MODE) {
            mSourceItem.setClickable(true);
            mDestItem.setClickable(true);
        } else if (mMode == IDelegate.SHOW_RES_MODE) {
            mSourceItem.setClickable(false);
            mDestItem.setClickable(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (mParentWidget == null) {
            return;
        }

        if (R.id.choose_dest_item_widget == v.getId()) {
            mParentWidget.onChooseDestLoc();
        }

        if (R.id.choose_source_item_widget == v.getId()) {
            mParentWidget.onChooseStartLoc();
        }
    }

    public void setStartLocation(String location) {
        mSourceItem.setLocation(location);
    }

    public void setDestLocation(String location) {
        mDestItem.setLocation(location);
    }

    private IParentWidget mParentWidget;

    public void setParentWidget(IParentWidget mParentWidget) {
        this.mParentWidget = mParentWidget;
    }

    public static interface IParentWidget{
        public void onChooseDestLoc();
        public void onChooseStartLoc();
    }

    public static class ChooseLocationItemWidget extends RelativeLayout {
        public ChooseLocationItemWidget(Context context) {
            super(context);
            init();
        }

        public ChooseLocationItemWidget(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ChooseLocationItemWidget(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        public static final int SOURCE_TYPE = 0;
        public static final int DEST_TYPE = 1;

        private ImageView mTypeIV;
        private TextView mInputET;
        private View mDivider;

        private void init(){
            LayoutInflater.from(getContext()).inflate(R.layout.widget_choose_item_location, this);

            mTypeIV = (ImageView)findViewById(R.id.type_icon);
            mInputET = (TextView)findViewById(R.id.input_location_et);

            mDivider = findViewById(R.id.divider);
        }

        public void setLocation(String location) {
            if (location == null) {
                location = "";
            }

            mInputET.setText(location);
        }

        public void setType(int type) {
            if (SOURCE_TYPE == type) {
                mTypeIV.setImageResource(R.mipmap.source);
                mInputET.setHint("");
                mDivider.setVisibility(View.VISIBLE);
            } else {
                mTypeIV.setImageResource(R.mipmap.dest_icon);
                mInputET.setHint("你要去哪儿");
                mDivider.setVisibility(View.GONE);
            }
        }
    }

}
