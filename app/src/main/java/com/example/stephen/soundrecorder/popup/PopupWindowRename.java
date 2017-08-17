package com.example.stephen.soundrecorder.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.example.stephen.soundrecorder.R;

/**
 * Created by stephen on 17-8-16.
 */

public class PopupWindowRename extends PopupWindow {
    private View popupView;
    private Button ok, cancel;
    private EditText editText;

    public PopupWindowRename(Context context, View.OnClickListener onClickListener, String editTextStr) {
        super(context);
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView=inflater.inflate(R.layout.popup_rename,null);
        ok =(Button)popupView.findViewById(R.id.popup_rename_ok);
        cancel =(Button)popupView.findViewById(R.id.popup_rename_cancel);
        editText=(EditText)popupView.findViewById(R.id.popup_rename_edit_text);
        editText.setText(editTextStr);
        editText.selectAll();
        ok.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);

        this.setContentView(popupView);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
//        this.setAnimationStyle(R.style.DialogAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    public String getEditText(){
        return editText.getText().toString();
    }
}
