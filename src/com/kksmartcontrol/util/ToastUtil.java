package com.kksmartcontrol.util;

import android.content.Context;

import com.example.kksmartcontrol.R;
import com.github.johnpersano.supertoasts.SuperToast;

/**
 * @author BBBoy
 */
public class ToastUtil {

    public static void showToast(Context context, CharSequence text,
                                 int duration) {

        SuperToast toast = SuperToast.create(context, text, duration);
        toast.setBackground(R.color.transparent);
        toast.setTextColor(context.getResources().getColor(R.color.black));
        toast.setTextSize(14);
        toast.show();


    }

    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getResources().getText(resId), duration);
    }

}
