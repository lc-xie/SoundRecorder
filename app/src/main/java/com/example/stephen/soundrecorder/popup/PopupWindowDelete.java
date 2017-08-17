package com.example.stephen.soundrecorder.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.stephen.soundrecorder.R;

/**
 * Created by stephen on 17-8-17.
 */

public class PopupWindowDelete extends PopupWindow {

    public PopupWindowDelete(Context context,View.OnClickListener onClickListener,String file) {
        super(context);
        Button cancel,ok;
        TextView fileName;
        View popupView;

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView=inflater.inflate(R.layout.popup_delete,null);
        ok =(Button)popupView.findViewById(R.id.popup_delete_ok);
        cancel =(Button)popupView.findViewById(R.id.popup_delete_cancel);
        fileName=(TextView)popupView.findViewById(R.id.popup_delete_file_name);
        fileName.setText(file);
        ok.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        this.setContentView(popupView);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }
}
