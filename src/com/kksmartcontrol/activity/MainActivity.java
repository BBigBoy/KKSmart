package com.kksmartcontrol.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kksmartcontrol.R;
import com.glh.montagecontrol.net.client.NetState;
import com.kksmartcontrol.adapter.MyFragmentPagerAdapter;
import com.kksmartcontrol.bean.KKSmartControlDataBean;
import com.kksmartcontrol.fragment.NetErrIndicateFragment;
import com.kksmartcontrol.fragment.PlusFragment;
import com.kksmartcontrol.fragment.VideoPreFragment;
import com.kksmartcontrol.fragment.util.FragmentUtil;
import com.kksmartcontrol.net.NetWorkObject;
import com.kksmartcontrol.preference.PreferencesUtils;
import com.kksmartcontrol.util.ToastUtil;
import com.kksmartcontrol.view.pagersliding.PagerSlidingTabStrip;
import com.kksmartcontrol.view.pjscreenview.PJScreenView;

public class MainActivity extends FragmentActivity implements
        PagerSlidingTabStrip.RefreshActivity {
    //public String TAG = this.getClass().getName();
    public PJScreenView pjScreenView;
    private Handler mHandler = new Handler();
    ImageView plusIv;
    private TextView titleText;
    ObjectAnimator titleTextAnimator;
    ObjectAnimator plusIvAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleText = (TextView) findViewById(R.id.titletext);
        titleTextAnimator = ObjectAnimator.ofFloat(titleText, "alpha", 0.3f,
                1.0f).setDuration(1300);
        NetWorkObject.context = this;
        Log.i("second", "onCreate");
        plusIv = (ImageView) findViewById(R.id.plus);
        plusIvAnimator = ObjectAnimator.ofFloat(plusIv, "rotation", 0, -45)
                .setDuration(600);
        pjScreenView = (PJScreenView) findViewById(R.id.pjscreenview);
        pjScreenView.setSplicesMode(KKSmartControlDataBean.getRowNum(),
                KKSmartControlDataBean.getColumnNum());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        tabs.setTabsValue(displayMetrics);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (NetWorkObject.getInstance().getNetStatus() != NetState.TCP_CONN_OPEN) {
            showNetErrIndicate();
        } else {
            ToastUtil.showToast(this, "已连接到服务器 ！", ToastUtil.LENGTH_MEDIUM);
        }
    }

    /**
     * 隐藏网络未连接提示
     */
    public void hideNetErrIndicate() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                FragmentUtil.hideFragmentByTag(MainActivity.this,
                        "NetErrIndicate");
            }
        });

    }

    /**
     * 显示网络未连接提示
     */
    public void showNetErrIndicate() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (FragmentUtil.hasFragmentSpecifiedByTag(MainActivity.this,
                        "NetErrIndicate")) {
                    FragmentUtil.showExistingFragmentByTag(MainActivity.this,
                            "NetErrIndicate");
                } else {
                    FragmentUtil.addFragmentWithTag(MainActivity.this,
                            NetErrIndicateFragment.class, R.id.displaylayout,
                            "NetErrIndicate", R.animator.neterrfragmententer);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        PreferencesUtils.putInt(this, "rowNum",
                KKSmartControlDataBean.getRowNum());
        PreferencesUtils.putInt(this, "columnNum",
                KKSmartControlDataBean.getColumnNum());
        Log.i("second", "onPause");
        super.onPause();
    }

    public void plusClick(View v) {
        if (!v.isSelected()) {
            v.setSelected(true);
            plusIvAnimator.start();
            FragmentUtil.addFragmentWithTag(this, PlusFragment.class,
                    R.id.listlayout, "plusFragment",
                    R.animator.plusfragmententer);
        } else {
            v.setSelected(false);
            plusIvAnimator.reverse();
            FragmentUtil.removeFragmentByTag(this, "plusFragment",
                    R.animator.plusfragmentexit);
        }
    }

    /**
     * 图像模式设置、背光、色温控制的fragment中的关闭按钮事件
     *
     * @param v 图像模式设置、背光、色温控制的fragment中的关闭按钮
     */
    public void onCloseBtnClick(View v) {
        FragmentUtil.removeVisibleFragmentByTag(this,
                getResources().getString(R.string.manualsetting),
                R.animator.fragmentexit);
    }

    @Override
    public void refreshDisplayLayout(final int position) {
        // TODO Auto-generated method stub
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (position == 0) {
                    FragmentUtil.removeFragmentByTag(MainActivity.this,
                            "VideoPreFragment", R.animator.fragmentexit);
                    FragmentUtil.removeFragmentByTag(MainActivity.this,
                            "VideoPlayFragment", R.animator.fragmentexit);
                } else {
                    FragmentUtil.addFragmentWithTag(MainActivity.this,
                            VideoPreFragment.class, R.id.displaylayout,
                            "VideoPreFragment", R.animator.plusfragmententer);
                }
            }
        });
    }

    @Override
    public void refreshTextAlpha(final float alpha) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                titleText.setAlpha(alpha);
            }
        });
    }

    @Override
    public void refreshTitle(final int position) {
        // TODO Auto-generated method stub
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (position == 1) {
                    titleText.setText("视频播放");
                    plusIv.setVisibility(View.GONE);
                } else {
                    titleText.setText("拼接控制");
                    plusIv.setVisibility(View.VISIBLE);
                }
                titleTextAnimator.start();
            }
        });
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        NetWorkObject.getInstance().setNetStatus(NetState.NET_STATUS_ERR);
        NetWorkObject.getInstance().unInitNetClient();
        NetWorkObject.context = null;
        Log.i("second", "onDestroy");
        super.onDestroy();
    }

    //记录首次按下返回键的时间
    private long mPressedTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (FragmentUtil
                    .removeVisibleFragmentByTag(this,
                            getResources().getString(R.string.manualsetting),
                            R.animator.fragmentexit))
                return false;
            if (plusIv.isSelected()) {
                plusIv.setSelected(false);
                plusIvAnimator.reverse();
                FragmentUtil.removeFragmentByTag(this, "plusFragment",
                        R.animator.plusfragmentexit);
                return false;
            }
            // displayDialog(context, R.layout.exitpopdialog);
            //new ExitDialog().show(getFragmentManager(), "Exit");
            long mNowTime = System.currentTimeMillis();//获取第一次按键时间
            if ((mNowTime - mPressedTime) > 800) {//比较两次按键时间差
                ToastUtil.showToast(this, "再按一次退出程序", 800);
                mPressedTime = mNowTime;
            } else {//退出程序
                this.finish();
            }
        }
        return false;
    }

}