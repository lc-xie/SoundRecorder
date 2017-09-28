package com.example.stephen.soundrecorder.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import com.example.stephen.soundrecorder.R;

/**
 * Created by stephen on 17-9-27.
 */

public class MultipleDeletePopup extends PopupWindow {

    public MultipleDeletePopup(Context context, View.OnClickListener onClickListener) {
        super(context);
        Button cancel,ok;
        View popupView;

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView=inflater.inflate(R.layout.bottom_dialog_multiple,null);
        ok =(Button)popupView.findViewById(R.id.delete_multiple_recording_file);
        cancel =(Button)popupView.findViewById(R.id.cancel_multiple_recording_file);
        ok.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        this.setContentView(popupView);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }
}
