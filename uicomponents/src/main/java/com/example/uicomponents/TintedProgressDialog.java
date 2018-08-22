package com.example.uicomponents;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;



public class TintedProgressDialog extends Dialog {
    private int origin_x,origin_y,origin_w,origin_h;

    public TintedProgressDialog(Context context) {
        super(context,R.style.TintedProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = .4f;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.tintedprogressdialog);
    }

    private void setLocation(int x, int y, int w, int h){
            origin_x = x;
            origin_y = y;
            origin_w = w;
            origin_h = h;
        }
    }
