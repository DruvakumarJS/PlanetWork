package com.netiapps.planetwork.dialogs;

import static com.netiapps.planetwork.utils.AlertDialogShow.DEFAULT;
import static com.netiapps.planetwork.utils.AlertDialogShow.FAILURE;
import static com.netiapps.planetwork.utils.AlertDialogShow.SUCCESS;
import static com.netiapps.planetwork.utils.AlertDialogShow.YES_OR_NO_ALERT;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.netiapps.planetwork.R;
import com.netiapps.planetwork.interfaces.OKClickListener;
import com.netiapps.planetwork.utils.Keys;

public class PlanetWorkDialogFragment extends DialogFragment {

    private ImageView ivInfo;
    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvCancel;

    private OKClickListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.qnet_simple_alert_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvTitle = dialog.findViewById(R.id.tv_title);
        tvMessage = dialog.findViewById(R.id.tv_message);
        ivInfo = dialog.findViewById(R.id.iv_info);
        tvCancel = dialog.findViewById(R.id.tv_cancel);

        Bundle mBundle = getArguments();
        String title = mBundle.getString(Keys.ALERT_TITLE_KEY);
        String message = mBundle.getString(Keys.ALERT_MESSAGE_KEY);
        int type = mBundle.getInt(Keys.ALERT_TYPE_KEY);
        int yesOrNoOrSimpleALertTypeKey = mBundle.getInt(Keys.YES_OR_NO_OR_SIMPLE_ALERT_KEY);
        final int requestCode = mBundle.getInt(Keys.ALERT_REQUEST_ID_KEY);

        if(yesOrNoOrSimpleALertTypeKey == YES_OR_NO_ALERT) {
            tvCancel.setVisibility(View.VISIBLE);
        }

        tvTitle.setText(title);
        tvMessage.setText(message);

        switch (type) {

            case SUCCESS:
                setBackground(R.drawable.tick, R.drawable.success_bg);
                break;

            case FAILURE:
                setBackground(R.drawable.close, R.drawable.failure_bg);
                break;

            case DEFAULT:
                break;
        }

        dialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.clickedOnOK(requestCode);
                }
                dialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.clickedONCancel(requestCode);
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private void setBackground(int imageSrc, int bg) {

        ivInfo.setImageResource(imageSrc);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ivInfo.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), bg) );
        } else {
            ivInfo.setBackground(ContextCompat.getDrawable(getActivity(), bg));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OKClickListener) context;
        } catch (ClassCastException e) {

        }
    }



}