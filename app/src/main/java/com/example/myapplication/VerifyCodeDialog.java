package com.example.myapplication;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyCodeDialog extends DialogFragment {

    @BindView(R.id.edtCode)
    EditText edtCode;
    @BindView(R.id.confirm)
    RelativeLayout confirm;
    @BindView(R.id.txtConfirm)
    TextView txtConfirm;
    @BindView(R.id.cancel)
    LinearLayout cancel;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Callback callback;

    public static VerifyCodeDialog newInstance(Callback callback) {
        Bundle args = new Bundle();
        VerifyCodeDialog fragment = new VerifyCodeDialog();
        fragment.callback = callback;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verify_dialog, container, false);
        ButterKnife.bind(this, view);
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


    @OnClick({R.id.confirm, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismissAllowingStateLoss();
                break;
            case R.id.confirm:
                callback.onConfirm(edtCode.getText().toString());
                showLoading();
                break;
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        txtConfirm.setVisibility(View.GONE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        txtConfirm.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismiss() {
        try {
            dismissAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void onConfirm(String code);
    }

}
