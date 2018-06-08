package com.example.ibulgakov.testapp;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;


/**
 * Service that will be responsible for the call
 * We will start it, bind to it provide it with the call session.
 * If it already has one session, it will hang all the other sessions
 * It will be also responsible for overlay ? may be
 */
public class OverlayService extends Service implements View.OnTouchListener {

    private static final String TAG = OverlayService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 2018;

    private WindowManager mWindowsManager;
    private FrameLayout mOverlay;

    private ValueAnimator mSnapAnimator;
    private Point screenSizes;

    private int originalXPos;
    private int originalYPos;

    private int leftSideScreenPos;
    private int rightSideScreenPos;

    private long startTouchTime;

    private boolean showOverlay = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        showOverlay = true;
        screenSizes = new Point();
        mWindowsManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowsManager.getDefaultDisplay().getSize(screenSizes);

        leftSideScreenPos = -screenSizes.x / 2;
        rightSideScreenPos = screenSizes.x / 2;

        initRemoteSurface();

        Log.i(TAG,"google.com".split("@")[0]);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public boolean onTouch(final View view, MotionEvent event) {

        final WindowManager.LayoutParams params = (WindowManager.LayoutParams) mOverlay.getLayoutParams();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                startTouchTime = System.currentTimeMillis();

                originalXPos = (int)(event.getRawX() - params.x);
                originalYPos = (int)(event.getRawY() - params.y);

                return true;
            case MotionEvent.ACTION_MOVE:

                params.x = Math.round(event.getRawX() - originalXPos);
                params.y = Math.round(event.getRawY() - originalYPos);

                mWindowsManager.updateViewLayout(mOverlay, params);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                long diff = System.currentTimeMillis() - startTouchTime;
                if(diff < 300){
                    Toast.makeText(this,"Click",Toast.LENGTH_LONG).show();
                }

                int curX = Math.round(event.getRawX() - originalXPos);

                if (curX < 0) {
                    snap(curX, leftSideScreenPos);
                } else {
                    snap(curX, rightSideScreenPos);
                }

                return true;
        }

        return false;
    }

    private void snap(final int fromX, final int toX) {

        mSnapAnimator = ValueAnimator.ofFloat(fromX, toX);
        mSnapAnimator.addUpdateListener(animation -> {
            Float val = parseFloat(animation.getAnimatedValue());
            if (val != null){
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) mOverlay.getLayoutParams();
                params.x = Math.round(val);
                mWindowsManager.updateViewLayout(mOverlay, params);
            }
        });
        mSnapAnimator.start();
    }

    private Float parseFloat(Object value){
        try {
            return value != null ? ((Number) value).floatValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Float.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    return null;
                }
            }
        }
        return null;
    }

    private boolean isOverlayShown() {
        return mOverlay != null;
    }

    private void initRemoteSurface() {
        Log.d(TAG, "initRemoteSurface");
        if (!showOverlay) {
            destroyRemoteSurface();
            return;
        }

        if (mOverlay != null) {
            return;
        }

        // Create overlay video
        createOverlay(mOverlay != null);
    }

    /**
     * Create video overlay
     *
     * @param isCreated
     */
    private void createOverlay(boolean isCreated) {
        if (isCreated) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(getApplicationContext())) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            mOverlay = (FrameLayout) inflater.inflate(R.layout.layout_overflow_surface, null);
            mOverlay.setOnTouchListener(this);

            // Create System overlay video
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_PHONE : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.CENTER;
            params.x = screenSizes.x / 2 - 180;
            params.y = -(screenSizes.y / 2 - 260);

            mWindowsManager.addView(mOverlay, params);
        } else {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PackageManager pm = getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            } else {

            }
        }
    }

    private void destroyRemoteSurface(){
        if (mOverlay != null) {
            mOverlay.setOnTouchListener(null);

            mWindowsManager.removeView(mOverlay);
            mOverlay = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        // Remove view from WindowManager
        destroyRemoteSurface();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


}

