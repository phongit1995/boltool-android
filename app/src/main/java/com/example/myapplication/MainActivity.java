package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    APIInterface apiInterface;
    private RecyclerView recyclerView;
    private IconAppAdapter adapter;
    private GridLayoutManager gridLayoutManager;

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
}