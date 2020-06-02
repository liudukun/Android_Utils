package com.liudukun.dkchat.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liudukun.dkchat.R;
import com.liudukun.dkchat.adapter.FaceListAdapter;
import com.liudukun.dkchat.manager.DKThemeManager;
import com.liudukun.dkchat.utils.ScreenUtil;
import com.liudukun.dkchat.utils.StringUtil;

import java.util.List;

public class SlideBar extends ConstraintLayout {
    TextView item01,item02;
    ConstraintLayout sliderItem;
    public SlideBarActionCallback cb;
    float x1,x2;
    boolean canMove;


    public SlideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideBar(Context context) {
        super(context);
        init();
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        View.inflate(getContext(), R.layout.slide_bar,this);
        sliderItem  = findViewById(R.id.slideBar);
        item01 = findViewById(R.id.button01);
        item02 = findViewById(R.id.button02);
        ConstraintLayout layout = findViewById(R.id.slide_bar);

        item01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(layout);
                ConstraintSet set = new ConstraintSet();
                set.clone(layout);
                set.clear(sliderItem.getId());
                set.connect(sliderItem.getId(),ConstraintSet.START,item01.getId(),ConstraintSet.START);
                set.connect(sliderItem.getId(),ConstraintSet.END,item01.getId(),ConstraintSet.END);
                set.connect(sliderItem.getId(),ConstraintSet.TOP,item01.getId(),ConstraintSet.TOP);
                set.connect(sliderItem.getId(),ConstraintSet.BOTTOM,item01.getId(),ConstraintSet.BOTTOM);
                set.applyTo(layout);

                cb.selectedPosition(0);

            }
        });

        item02.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(layout);
                ConstraintSet set = new ConstraintSet();
                set.clone(layout);
                set.clear(sliderItem.getId());
                set.connect(sliderItem.getId(),ConstraintSet.START,item02.getId(),ConstraintSet.START);
                set.connect(sliderItem.getId(),ConstraintSet.END,item02.getId(),ConstraintSet.END);
                set.connect(sliderItem.getId(),ConstraintSet.TOP,item02.getId(),ConstraintSet.TOP);
                set.connect(sliderItem.getId(),ConstraintSet.BOTTOM,item02.getId(),ConstraintSet.BOTTOM);
                set.applyTo(layout);
                cb.selectedPosition(1);
            }
        });


//        layout.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                    //当手指按下的时候
//                    x1 = event.getX();
//                }
//                if(event.getAction() == MotionEvent.ACTION_UP) {
//                    //当手指离开的时候
//                    x2 = event.getX();
//                    if(x1 - x2 > 10) {
//                        item02.callOnClick();
//
//                    } else if(x2 - x1 > 10) {
//                        item01.callOnClick();
//                    }
//                }
//
//                return false;
//            }
//        });
    }




    public void setItemStrings(String[] itemStrings) {
        item01.setText(itemStrings[0]);
        item02.setText(itemStrings[1]);
    }

    public interface SlideBarActionCallback{
        void selectedPosition(int pos);
    }

}
