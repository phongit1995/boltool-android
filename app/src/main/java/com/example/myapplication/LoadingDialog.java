package com.example.myapplication;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class LoadingDialog extends DialogFragment implements OnProgressBarListener {

    private Callback callback;
    private Timer timer;
    private NumberProgressBar bnp;
    private TextView txtLoading;
    private String error;

    public static LoadingDialog newInstance(Callback callback, String error) {
        Bundle args = new Bundle();
        LoadingDialog fragment = new LoadingDialog();
        fragment.callback = callback;
        fragment.error = error;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_dialog, container, false);
        ButterKnife.bind(this, view);
        bnp = (NumberProgressBar) view.findViewById(R.id.numberbar1);
        txtLoading = view.findViewById(R.id.loading);
        bnp.setOnProgressBarListener(this);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bnp.incrementProgressBy(1);
                    }
                });
            }
        }, 10, 50);
        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
//		ft.add(this, tag);
        String tagName = this.getClass().getSimpleName();
        Fragment fragment = manager.findFragmentByTag(tagName);
        if (fragment != null && fragment.getClass().getSimpleName().equals(this.getClass()

                .getSimpleName()) && fragment.isVisible()) {
            return;
        }

        ft.add(this, tagName);
//		ft.addToBackStack(tagName);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public void dismiss() {
        try {
            dismissAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChange(int current, int max) {
        if (current == max) {
            if (!error.isEmpty()) {
                txtLoading.setText(error);
            } else {
                txtLoading.setText("Lấy dữ liệu cổng game thành công !!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDisMiss();
                        dismissAllowingStateLoss();
                    }
                }, 3000);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public interface Callback {
        void onDisMiss();
    }

}
