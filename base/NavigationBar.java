package com.liudukun.dkchat.base;

import com.liudukun.dkchat.R;
import com.liudukun.dkchat.view.SlideBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.drawable.DrawableCompat;

public class NavigationBar extends ConstraintLayout {
    public ImageView backImage;
    public TextView badge;
    public TextView backTitle;
    public TextView titleView;
    public TextView moreButton;
    public ConstraintLayout backLayout;
    public OnClickListener backListener;
    public OnClickListener moreListener;
    public SlideBar slideBar;

    public NavigationBar(Context context) {
        super(context);
        init();
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.navigation_bar, this);
        backImage = findViewById(R.id.backImage);
        badge = findViewById(R.id.badgeView);
        backTitle = findViewById(R.id.backTitle);
        titleView = findViewById(R.id.titleView);
        moreButton = findViewById(R.id.moreButton);
        backLayout = findViewById(R.id.backLayout);
        slideBar = findViewById(R.id.slideBar);

        backLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backListener.onClick(v);
            }
        });
        moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moreListener.onClick(v);
            }
        });

        setLeftStyleNormal();
        setRightStyleNormal();
    }

    public void setTitle(String title){
        if (title!=null){
            titleView.setText(title);
        }
    }

    public void setRightStyleText(String title){
        moreButton.setText("提交");
        moreButton.setTextColor(getResources().getColor(R.color.color_VI));
        moreButton.setCompoundDrawablesRelative(null,null,null,null);
    }

    public void setRightStyleNormal(){
        moreButton.setText("");
        Drawable drawable = getResources().getDrawable(R.drawable.more);
        moreButton.setCompoundDrawablesRelative(null,null,drawable,null);
    }

    public void setLeftStyleBadge(){
        backTitle.setVisibility(INVISIBLE);
        badge.setVisibility(VISIBLE);
    }

    public void setLeftStyleNormal(){
        backTitle.setVisibility(VISIBLE);
        badge.setVisibility(INVISIBLE);
    }

    public void setTitleStyleSlider(){
        titleView.setVisibility(GONE);
        slideBar.setVisibility(VISIBLE);
    }


}
