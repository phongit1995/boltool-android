package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    APIInterface apiInterface;
    private RecyclerView recyclerView;
    private IconAppAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private VerifyCodeDialog dialog;
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<List<String>> call = apiInterface.doGetListIconApp();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.body() != null) {
                    adapter = new IconAppAdapter(MainActivity.this, response.body());
                    adapter.setMCallBack(new OnActionCallBack() {
                        @Override
                        public void callBack(@NonNull String key, @Nullable Object... obj) {
                            dialog = VerifyCodeDialog.newInstance(new VerifyCodeDialog.Callback() {
                                @Override
                                public void onConfirm(String code) {
                                    verifyCode(code);
                                }
                            });
                            dialog.show(getSupportFragmentManager(), null);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                call.cancel();
            }
        });

    }

    /*  start floating widget service  */
    public void createFloatingWidget() {
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        } else
            //If permission is granted start floating widget service
            startFloatingWidgetService();

    }

    private void startFloatingWidgetService() {
        startService(new Intent(MainActivity.this, FloatingWidgetService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK)
                //If permission granted start floating widget service
                startFloatingWidgetService();
            else
                //Permission is not available then display toast
                Toast.makeText(this,
                        getResources().getString(R.string.draw_other_app_permission_denied),
                        Toast.LENGTH_SHORT).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void verifyCode(String code) {
        Call<CodeResponse> call = apiInterface.verifyCode(code);
        call.enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    createFloatingWidget();
                    dialog.dismissAllowingStateLoss();
                } else {
                    Toast.makeText(MainActivity.this, "Mã phần mềm không tồn tại \n       Vui lòng liên hệ Hotline/Zalo : 0979.51.7777 để nhận mã phần mềm !", Toast.LENGTH_LONG).show();
                }
                dialog.hideLoading();
            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }
}