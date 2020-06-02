package com.liudukun.dkchat.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.liudukun.dkchat.R;
import com.liudukun.dkchat.manager.DKAppManager;

public class DKPopupDialog extends Dialog {
    //声明xml文件里的组件
    public static int Style_Normal = 0;
    public static final int Style_Input = 1;
    public static final int Style_Input_Two = 2;
    public static final int Style_Items = 3;

    public static final int Item_Cancel = -2;
    public static final int Item_OK = -1;

    public TextView tv_title, tv_message;
    public Button bt_cancel, bt_confirm;
    public EditText et_one, et_two;

    public Button[] items;
    public LinearLayout itemsContainer, bottomLayout;

    private String title, message;
    private int style;
    private String[] itemStrings;

    ActionButtonListener listener;


    void updateStyle(int style) {
        if (style == Style_Normal) {
            et_one.setVisibility(View.GONE);
            et_two.setVisibility(View.GONE);
            itemsContainer.setVisibility(View.GONE);
        }
        if (style == Style_Input) {
            et_one.setVisibility(View.VISIBLE);
            et_two.setVisibility(View.GONE);
            itemsContainer.setVisibility(View.GONE);
        }
        if (style == Style_Input_Two) {
            et_one.setVisibility(View.VISIBLE);
            et_two.setVisibility(View.VISIBLE);
            itemsContainer.setVisibility(View.GONE);
        }
        if (style == Style_Items) {
            itemsContainer.setVisibility(View.VISIBLE);
            et_one.setVisibility(View.GONE);
            et_two.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        }
    }


    public String getInputText(int pos) {
        if (pos == 0) {
            return et_one.getText().toString();
        } else {
            return et_two.getText().toString();
        }
    }


    //CustomDialog类的构造方法
    public DKPopupDialog(@NonNull Context context, String title, String message, ActionButtonListener onClickListener) {
        super(context);
        this.style = 0;
        this.listener = onClickListener;
        this.title = title;
        this.message = message;
    }

    //CustomDialog类的构造方法
    public DKPopupDialog(@NonNull Context context, String title, String message, int style_input, ActionButtonListener onClickListener) {
        super(context);
        this.style = style_input;
        this.listener = onClickListener;
        this.title = title;
        this.message = message;
        this.itemStrings = itemStrings;
    }

    //CustomDialog类的构造方法
    public DKPopupDialog(@NonNull Context context, String title, String message, String[] itemStrings, ActionButtonListener onClickListener) {
        super(context);
        this.style = 3;
        this.listener = onClickListener;
        this.title = title;
        this.message = message;
        this.itemStrings = itemStrings;
    }

    //在app上以对象的形式把xml里面的东西呈现出来的方法！
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //为了锁定app界面的东西是来自哪个xml文件
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dk_dialog);
        //设置弹窗的宽度
        WindowManager m = getWindow().getWindowManager();

        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x * 0.8);//是dialog的宽度为app界面的80%
        getWindow().setAttributes(p);

        //找到组件
        tv_title = findViewById(R.id.tv_title);
        tv_message = findViewById(R.id.tv_message);
        bt_cancel = findViewById(R.id.bt_cancel);
        bt_confirm = findViewById(R.id.bt_confirm);
        et_one = findViewById(R.id.inputET);
        et_two = findViewById(R.id.inputET2);
        itemsContainer = findViewById(R.id.itemContainer);
        bottomLayout = findViewById(R.id.bottom_layout);

        updateStyle(style);

        int color = Color.parseColor("#11000000");

        if (itemStrings == null) {
            itemStrings = new String[0];
        }
        int i = 0;
        items = new Button[itemStrings.length];
        for (String string : itemStrings) {
            Button item = new Button(getContext());
            item.setText(string);
            item.setTag(i);
            item.setTextSize(14);
            item.setAllCaps(false);
            item.setBackground(ShapeUtil.shape(getContext(), ScreenUtil.getPxByDp(4), color));
            item.setOnClickListener(this::itemClick);
            items[i] = item;
            itemsContainer.addView(item);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) item.getLayoutParams();
            params.bottomMargin = ScreenUtil.getPxByDp(8);
            item.setLayoutParams(params);

            i++;
        }
        et_one.setBackground(ShapeUtil.shape(getContext(), ScreenUtil.getPxByDp(4), color));
        et_two.setBackground(ShapeUtil.shape(getContext(), ScreenUtil.getPxByDp(4), color));


        //设置组件对象的text参数
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            title = "温馨提示";
            tv_title.setText(title);
        }
        if (!TextUtils.isEmpty(message)) {
            tv_message.setText(message);
            tv_message.setVisibility(View.VISIBLE);
        } else {
            tv_message.setVisibility(View.GONE);
        }

        //为两个按钮添加点击事件
        bt_confirm.setOnClickListener(this::onClick);
        bt_cancel.setOnClickListener(this::onClick);
    }

    //重写onClick方法
    public void itemClick(View view) {
        int tag = (int) view.getTag();
        listener.actionButton(this, tag);
        dismiss();
    }

    //重写onClick方法
    public void onClick(View view) {
        if (view == bt_cancel) {
            listener.actionButton(this, Item_Cancel);
        }
        if (view == bt_confirm) {
            listener.actionButton(this, Item_OK);
        }
        dismiss();
    }

    public void setItemSelected(int itemIndex){
        Button item = items[itemIndex];
        String newString = item.getText() + "   √";
        item.setText(newString);
    }


    public interface ActionButtonListener {
        void actionButton(DKPopupDialog dialog, int which);
    }


    public static DKPopupDialog showDialog(String title, String message, ActionButtonListener onClickListener) {
        DKPopupDialog dialog = new DKPopupDialog(DKAppManager.shareInstance().currentActivity(), title, message, onClickListener);
        DKAppManager.shareInstance().currentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        return dialog;
    }

    public static DKPopupDialog showDialog(String title, String message, int style_input, ActionButtonListener onClickListener) {
        DKPopupDialog dialog = new DKPopupDialog(DKAppManager.shareInstance().currentActivity(), title, message, style_input, onClickListener);
        DKAppManager.shareInstance().currentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        return dialog;
    }

    public static DKPopupDialog showDialog(String title, String message, String[] itemStrings, ActionButtonListener onClickListener) {
        DKPopupDialog dialog = new DKPopupDialog(DKAppManager.shareInstance().currentActivity(), title, message, itemStrings, onClickListener);
        DKAppManager.shareInstance().currentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        return dialog;
    }


}