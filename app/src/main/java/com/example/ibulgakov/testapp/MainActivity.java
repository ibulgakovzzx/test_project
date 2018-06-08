package com.example.ibulgakov.testapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Handler handler;
    AppCompatButton btnStartOverlay;
    RelativeLayout rlNotofication;
    Runnable removeRunable = this::hide;

    ChatsRecyclerView rvSimple;
    ConstraintLayout clRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        clRoot = findViewById(R.id.cl_root);

        handler = new Handler();

        List<String> data = new ArrayList<>();
        for(int i = 0; i < 50; i++)
            data.add(String.valueOf(i));

        rvSimple = findViewById(R.id.rv_simple);
        rvSimple.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSimple.setAdapter(new RecyclerViewAdapter(this, data));

        btnStartOverlay = findViewById(R.id.btn_open_gallery);
        btnStartOverlay.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, BottomNavActivity.class));
        });

        rlNotofication = findViewById(R.id.notification_inapp);
        rlNotofication.setOnTouchListener(new SwipeDismissTouchListener(rlNotofication, null, new SwipeDismissTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(Object token) {
                return true;
            }

            @Override
            public void onDismiss(View view, Object token) {
                handler.removeCallbacks(removeRunable);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext()))) {
                startService(new Intent(MainActivity.this, OverlayService.class));
            }
        }
    }

    public void show(){
        rlNotofication.setVisibility(View.VISIBLE);
        rlNotofication.animate().translationY(0).setDuration(500).withEndAction(() -> handler.postDelayed(removeRunable,4000));
    }

    public void hide() {
        rlNotofication.animate().translationY(-100f).setDuration(500).withEndAction(() -> {
            if(rlNotofication.getVisibility() == View.VISIBLE) {
                rlNotofication.setVisibility(View.INVISIBLE);
            }
        });
    }
}
