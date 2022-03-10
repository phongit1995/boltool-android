package com.example.myapplication;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.On;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by tuanthanhdev on 3/3/2022.
 */

public class FloatingWidgetService extends Service implements View.OnClickListener {
    private WindowManager mWindowManager;
    private View mFloatingWidgetView;
    private Point szWindow = new Point();
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Socket mSocket;
    private ImageView imgTai, imgXiu;
    private ObjectAnimator animTai, animXiu;
    String code;
    private Handler handle;
    boolean isRunningTai = false;
    boolean isRunningXiu = false;
    boolean isConnect = false;

    {
        try {
            mSocket = IO.socket("https://api-bot88.toolhackgamevip.com/?fbclid=IwAR0CuSSwJ4jNUsQvnxIl5TrSI42tq8L3QqQuEmUD_U3Mu446PB3kDFKG5b8");
        } catch (URISyntaxException e) {
        }
    }


    public FloatingWidgetService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        code = intent.getStringExtra("code");
        if (!isConnect) {
            connectSocket();
        }
        mSocket.emit("joinRoom", code);
        mSocket.on("notification", onNewMessage);
        return super.onStartCommand(intent, flags, startId);
    }

    private void connectSocket() {
        mSocket.connect();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        getWindowManagerDefaultDisplay();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        addFloatingWidgetView(inflater);
        implementClickListeners();
        implementTouchListenerToFloatingWidgetView();
        handle = new Handler(Looper.getMainLooper());
        connectSocket();
        blinkTai();
        blinkXiu();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... objects) {
            Log.e("huhu", objects[0].toString());
            switch (objects[0].toString()) {
                case "xanh":
                    if (isRunningTai) handle.removeCallbacks(runnableTai);
                    handle.post(runnableTai);
                    break;
                case "do":
                    if (isRunningXiu) handle.removeCallbacks(runnableXiu);
                    handle.post(runnableXiu);
                    break;
                default:
                    break;
            }

        }
    };

    private Runnable runnableTai = new Runnable() {
        @Override
        public void run() {
            try {
                isRunningTai = true;
                if (animTai != null && animTai.isRunning()) animTai.end();
                if (animXiu != null && animXiu.isRunning()) animXiu.end();
                blinkTai();
            } catch (Exception e) {
                e.printStackTrace();
                isRunningTai = false;
            }
        }
    };

    private Runnable runnableXiu = new Runnable() {
        @Override
        public void run() {
            try {
                isRunningXiu = true;
                if (animTai != null && animTai.isRunning()) animTai.end();
                if (animXiu != null && animXiu.isRunning()) animXiu.end();
                blinkXiu();
            } catch (Exception e) {
                e.printStackTrace();
                isRunningXiu = false;
            }

        }
    };

    @SuppressLint("WrongConstant")
    private void blinkXiu() {
        animXiu = ObjectAnimator.ofInt(imgXiu, "backgroundColor", Color.RED, Color.WHITE, Color.RED);
        animXiu.setDuration(1000);
        animXiu.setEvaluator(new ArgbEvaluator());
        animXiu.setRepeatMode(Animation.REVERSE);
        animXiu.setRepeatCount(5);
        animXiu.start();
    }

    @SuppressLint("WrongConstant")
    private void blinkTai() {
        animTai = ObjectAnimator.ofInt(imgTai, "backgroundColor", Color.GREEN, Color.WHITE, Color.GREEN);
        animTai.setDuration(1000);
        animTai.setEvaluator(new ArgbEvaluator());
        animTai.setRepeatMode(Animation.REVERSE);
        animTai.setRepeatCount(5);
        animTai.start();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnect = true;
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnect = false;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FloatingWidgetService.this, "Đã có lỗi trong quá trình kết nối . Vui lòng thử lại sau", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private void addFloatingWidgetView(LayoutInflater inflater) {
        mFloatingWidgetView = inflater.inflate(R.layout.floating_widget_layout, null);
        imgTai = mFloatingWidgetView.findViewById(R.id.imgTai);
        imgXiu = mFloatingWidgetView.findViewById(R.id.imgXiu);
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        mWindowManager.addView(mFloatingWidgetView, params);

    }

    private void getWindowManagerDefaultDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
            mWindowManager.getDefaultDisplay().getSize(szWindow);
        else {
            int w = mWindowManager.getDefaultDisplay().getWidth();
            int h = mWindowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }
    }


    private void implementTouchListenerToFloatingWidgetView() {
        mFloatingWidgetView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {

            long time_start = 0, time_end = 0;

            boolean isLongClick = false;//variable to judge if user click long press
            boolean inBounded = false;//variable to judge if floating view is bounded to remove view
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {
                @Override
                public void run() {
                    isLongClick = true;
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();

                        handler_longClick.postDelayed(runnable_longClick, 600);
                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        return true;
                    case MotionEvent.ACTION_UP:
                        isLongClick = false;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if (inBounded) {
                            stopSelf();
                            inBounded = false;
                            break;
                        }

                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis();
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int barHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (mFloatingWidgetView.getHeight() + barHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (mFloatingWidgetView.getHeight() + barHeight);
                        }

                        layoutParams.y = y_cord_Destination;

                        inBounded = false;

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if (isLongClick) {
                            int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

                            if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                inBounded = true;
                                mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
                                break;
                            } else {
                                inBounded = false;
                            }

                        }
                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
                        return true;
                }
                return false;
            }
        });
    }

    private void implementClickListeners() {
        mFloatingWidgetView.findViewById(R.id.close_floating_view).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_floating_view:
                stopSelf();
                break;
        }
    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindowManagerDefaultDisplay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingWidgetView != null)
            mWindowManager.removeView(mFloatingWidgetView);
        mSocket.disconnect();
    }

}
