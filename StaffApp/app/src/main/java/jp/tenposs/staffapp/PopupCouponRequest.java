package jp.tenposs.staffapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

import jp.tenposs.datamodel.CouponInfo;

/**
 * Created by ambient on 8/29/16.
 */

public class PopupCouponRequest implements View.OnClickListener {
    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected View contentView;

    ImageView imageView;
    //ArrayList<?> popupData;
    Context mContext;
    CouponInfo.Coupon popupData;
    Button acceptButton;
    Button rejectButton;

    public int fullImageSize = 640;


    public PopupCouponRequest(Context context) {
        mContext = context;
    }

    public void setData(Serializable extras) {
        popupData = (CouponInfo.Coupon) extras;
    }

    public void show() {
        alertBuilder = new AlertDialog.Builder(this.mContext, R.style.CustomDialog);

        contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_coupon_request, null);
        imageView = (ImageView) contentView.findViewById(R.id.coupon_image);
        acceptButton = (Button) contentView.findViewById(R.id.accept_button);
        rejectButton = (Button) contentView.findViewById(R.id.reject_button);

        alertBuilder.setView(contentView);
        alertBuilder.setCancelable(false);
        contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alert = alertBuilder.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        acceptButton.setOnClickListener(this);
        rejectButton.setOnClickListener(this);

        Picasso ps = Picasso.with(mContext);
        ps.load(popupData.getImageUrl())
                .resize(fullImageSize, fullImageSize)
                .centerInside()
                .into(imageView);
    }

    @Override
    public void onClick(View v) {
        if (v == acceptButton) {

        } else if (v == rejectButton) {

        }
        alert.dismiss();
    }
}
