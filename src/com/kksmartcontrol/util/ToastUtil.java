package com.kksmartcontrol.util;

import android.content.Context;
import android.view.View;

import com.github.johnpersano.supertoasts.SuperToast;

/**
 * 优化TOAST，使得当多次点击显示toast时不会按序延迟显示，而是直接忽略中间的toast
 * @author BBBoy
 */
public class ToastUtil {
    public static final int LENGTH_LONG = 2000;
    public static final int LENGTH_SHORT = 1000;
    public static final int LENGTH_MEDIUM = 1500;
    private static SuperToast toast;
    private static CharSequence delay_Text;
    private static int delay_duration;

    public static void showToast(Context context, CharSequence text,
                                 int duration) {
        if (toast != null && toast.isShowing()) {
            delay_Text = text;
            delay_duration = duration;
        } else {
            toast = SuperToast.create(context, text, duration);
            toast.setTextSize(14);
            toast.setOnDismissListener(new SuperToast.OnDismissListener() {
                @Override
                public void onDismiss(View view) {
                    if (delay_duration != 0) {
                        toast.setText(delay_Text);
                        toast.setDuration(delay_duration);
                        delay_Text = null;
                        delay_duration = 0;
                        toast.show();
                    } else {
                        toast = null;
                    }
                }
            });
            toast.show();
        }

    }

    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getResources().getText(resId), duration);
    }

}
