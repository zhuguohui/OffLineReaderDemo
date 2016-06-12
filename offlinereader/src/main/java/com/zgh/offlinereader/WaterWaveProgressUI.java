package com.zgh.offlinereader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.zgh.offlinereader.server.OfflineReaderServer;
import com.zgh.offlinereader.ui.OffLineProgressUI;
import com.zgh.offlinereader.util.AppUtil;
import com.zgh.offlinereader.view.water_wave_progress.WaterWaveProgress;


/**
 * Created by yuelin on 2016/5/24.
 */
public class WaterWaveProgressUI implements View.OnTouchListener, View.OnClickListener ,OffLineProgressUI {
    private static final int MSG_ON_FINISH = 1;
    private Context mContext;
    private final WindowManager mManager;
    private final View mView;
    private final WindowManager.LayoutParams mParams;
    private final WaterWaveProgress mWaterWaveProgress;
    private TextView tv_title;
    private int rightEdgeX = 1000;
    private boolean isAttached = false;
    Handler mhandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ON_FINISH:
                    onfinish();
                    break;
            }
        }
    };

    private void onfinish() {
        if (isAttached) {
            mManager.removeView(mView);
            isAttached = false;
        }
    }

    public WaterWaveProgressUI(Context context) {
        mContext = context;
        mManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mView = View.inflate(context, R.layout.view_offline_progress, null);
        mView.setOnTouchListener(this);
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.width = AppUtil.dip2px(mContext, 55);
        mParams.height = AppUtil.dip2px(mContext, 55);
        mParams.x = 0;
        mParams.y = 300;
        mParams.windowAnimations = R.style.offline_anim_style;
        mWaterWaveProgress = (WaterWaveProgress) mView.findViewById(R.id.waterWaveProgress);
        tv_title = (TextView) mView.findViewById(R.id.tv_title);
        //获取屏幕宽度计算右边界限坐标
        int width = mManager.getDefaultDisplay().getWidth();
        rightEdgeX = width - mParams.width / 2;
    }

    @Override
    public void showProgress() {
        if (!isAttached) {
            tv_title.setText("正在离线");
            mWaterWaveProgress.setMaxProgress(100);
            mWaterWaveProgress.setProgress(0);
            mWaterWaveProgress.animateWave();
            mWaterWaveProgress.setShowProgress(false);
            mManager.addView(mView, mParams);
            isAttached = true;
        }
    }

    @Override
    public void closeProgress() {
        tv_title.setText("离线完成");
        mhandler.sendEmptyMessageDelayed(MSG_ON_FINISH, 1000);
        mContext.stopService(new Intent(mContext, OfflineReaderServer.class));
    }

    public void updateProgress(int progress) {
        mWaterWaveProgress.setProgress(progress);
    }



    int downX, downY;
    int lasetMoveX, lastMoveY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isAnimatting) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                lasetMoveX = (int) event.getRawX();
                lastMoveY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getRawX() - lasetMoveX);
                int dy = (int) (event.getRawY() - lastMoveY);
                updateViewPosition(dx, dy);
                lasetMoveX = (int) event.getRawX();
                lastMoveY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                dx = (int) (event.getRawX() - downX);
                dy = (int) (event.getRawY() - downY);
                if (Math.abs(dx) <= 5 && Math.abs(dy) <= 5) {
                    onClick(null);
                } else {
                    autoBack();
                }
                break;

        }
        return true;
    }

    boolean isAnimatting = false;

    private void autoBack() {
        int x = mParams.x;
        //判断距离左边近还是右边近
        int finalX = 0;
        if (Math.abs(x) > Math.abs(x - rightEdgeX)) {
            finalX = rightEdgeX;
        }
        ValueAnimator animator = ValueAnimator.ofInt(x, finalX);
        animator.setDuration(500);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isAttached) {
                    int x = (int) animation.getAnimatedValue();
                    mParams.x = x;
                    mManager.updateViewLayout(mView, mParams);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimatting = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isAnimatting = true;
            }
        });
        animator.start();
    }

    private void updateViewPosition(int dx, int dy) {
        if (isAttached) {
            mParams.x = mParams.x + dx;
            mParams.y = mParams.y + dy;
            mManager.updateViewLayout(mView, mParams);
        }
    }

    @Override
    public void onClick(View v) {
        tv_title.setText("正在取消");
        mhandler.sendEmptyMessageDelayed(MSG_ON_FINISH, 1000);
        mContext.stopService(new Intent(mContext, OfflineReaderServer.class));
    }


}
