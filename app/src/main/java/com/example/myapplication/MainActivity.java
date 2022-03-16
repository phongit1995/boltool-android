package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
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
    private String codeRoom;
    private TextView otherGame;
    private List<String> listAppIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        otherGame = findViewById(R.id.txtOtherGame);

        otherGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = VerifyCodeDialog.newInstance(new VerifyCodeDialog.Callback() {
                    @Override
                    public void onConfirm(String code) {
                        codeRoom = code;
                        verifyCode(code);
                    }
                });
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        listAppIcon = Arrays.asList(
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665092/bot88-image/1b0d7192ddec12b24bfd4_eewfjq.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665092/bot88-image/05f4c1686d16a248fb073_yzcg3s.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665092/bot88-image/3aa40e20a25e6d00344f13_y9nvn8.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665092/bot88-image/3b3c4123aa5d65033c4c25_r4dn5k.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665091/bot88-image/1ac25d9e95e05abe03f1_edaall.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665092/bot88-image/f4f9e3110c6fc3319a7e_cq43zv.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665091/bot88-image/0a57b4ca18b4d7ea8ea52_wbr41d.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665090/bot88-image/c5e9b88669f8a6a6ffe9_eb98wj.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665090/bot88-image/be136c8cc0f20fac56e36_yozucq.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665091/bot88-image/c5e9b88669f8a6a6ffe928_s3vpsl.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665090/bot88-image/af827c04d07a1f24466b16_ytczcc.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665089/bot88-image/71687febd3951ccb45848_rsy4la.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665090/bot88-image/bc53da440b3ac4649d2b_mxtxfx.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665089/bot88-image/961be7720c0cc3529a1d21_vsvfd7.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665089/bot88-image/989b821c2e62e13cb87315_iq9kka.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665090/bot88-image/205982ca45b48aead3a5_tkzzxc.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665091/bot88-image/d9155d88f1f63ea867e71_bxsrkv.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665089/bot88-image/95a35221fe5f3101684e10_s8ywfe.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665090/bot88-image/ad794ee6e2982dc674895_bjzzfv.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665089/bot88-image/414ffaca56b499eac0a511_vucr6u.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665089/bot88-image/79397fb1d3cf1c9145de20_ioshhb.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665088/bot88-image/85cc1a48b6367968202714_cglphs.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665088/bot88-image/8f0267128c6c43321a7d22_ewgahb.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665088/bot88-image/6d78a1634a1d8543dc0c24_eizngc.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665088/bot88-image/6d78a1634a1d8543dc0c24_eizngc.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665088/bot88-image/63bd82352e4be115b85a18_sgkhzj.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665089/bot88-image/92f6e0e00b9ec4c09d8f23_ocruuf.jpg",
                "https://res.cloudinary.com/no-company-name/image/upload/v1646665088/bot88-image/5c9694a57fdbb085e9ca27_otssxy.jpg");

        Call<List<String>> call = apiInterface.doGetListIconApp();

        adapter = new IconAppAdapter(MainActivity.this, listAppIcon);
        adapter.setMCallBack(new OnActionCallBack() {
            @Override
            public void callBack(@NonNull String key, @Nullable Object... obj) {
                dialog = VerifyCodeDialog.newInstance(new VerifyCodeDialog.Callback() {
                    @Override
                    public void onConfirm(String code) {
                        codeRoom = code;
                        verifyCode(code);
                    }
                });
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        recyclerView.setAdapter(adapter);
        registerReceiver(broadcastReceiver, new IntentFilter("CLOSE_APP"));

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finishAndRemoveTask();
        }
    };

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
        Intent intent = new Intent(MainActivity.this, FloatingWidgetService.class);
        intent.putExtra("code", codeRoom);
        startService(intent);
        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void verifyCode(String code) {
        Call<CodeResponse> call = apiInterface.verifyCode(code);
        call.enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    LoadingDialog dialogg = LoadingDialog.newInstance(new LoadingDialog.Callback() {
                        @Override
                        public void onDisMiss() {
                            createFloatingWidget();
                            dialog.dismissAllowingStateLoss();
                        }
                    }, "");
                    dialogg.show(getSupportFragmentManager(), null);
                } else if (response.code() == 400) {
                    APIError message = new Gson().fromJson(response.errorBody().charStream(), APIError.class);
                    LoadingDialog dialogg = LoadingDialog.newInstance(new LoadingDialog.Callback() {
                        @Override
                        public void onDisMiss() {
                        }
                    }, message.getMessage());
                    dialogg.show(getSupportFragmentManager(), null);
                } else if (response.code() == 404) {
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