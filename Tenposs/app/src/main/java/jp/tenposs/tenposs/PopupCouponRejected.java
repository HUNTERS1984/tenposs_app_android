package jp.tenposs.tenposs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by ambient on 9/17/16.
 */
public class PopupCouponRejected {
    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected Context mContext;
    protected View contentView;

    Button mDoneButton;


    public PopupCouponRejected(Context context) {
        this.mContext = context;
    }

    public void setData() {
    }

    public void show() {
        alertBuilder = new AlertDialog.Builder(this.mContext, R.style.CustomDialog);

        contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_coupon_rejected, null);
        mDoneButton = (Button) contentView.findViewById(R.id.done_button);
        alertBuilder.setView(contentView);
        alertBuilder.setCancelable(true);
        contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alert = alertBuilder.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }
}
