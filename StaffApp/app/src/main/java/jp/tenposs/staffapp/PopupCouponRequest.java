package jp.tenposs.staffapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

import jp.tenposs.communicator.CouponAcceptCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CouponAcceptInfo;
import jp.tenposs.datamodel.CouponRequestInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/29/16.
 */

public class PopupCouponRequest implements View.OnClickListener {
    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected View contentView;

    CouponRequestInfo.RequestInfo popupData;

    ImageView mCouponImage;
    Context mContext;
    TextView mTitleLabel;
    TextView mContentLabel;
    Button mAcceptButton;
    Button mRejectButton;

    public int fullImageSize = 640;


    public PopupCouponRequest(Context context) {
        mContext = context;
    }

    public void setData(Serializable extras) {
        popupData = (CouponRequestInfo.RequestInfo) extras;
    }

    public void show() {
        alertBuilder = new AlertDialog.Builder(this.mContext, R.style.CustomDialog);

        contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_coupon_request, null);
        mCouponImage = (ImageView) contentView.findViewById(R.id.coupon_image);

        mTitleLabel = (TextView) contentView.findViewById(R.id.title_label);
        mContentLabel = (TextView) contentView.findViewById(R.id.content_label);
        mAcceptButton = (Button) contentView.findViewById(R.id.accept_button);
        mRejectButton = (Button) contentView.findViewById(R.id.reject_button);

        alertBuilder.setView(contentView);
        alertBuilder.setCancelable(false);
        contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alert = alertBuilder.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mContentLabel.setText(popupData.name + " " + mContext.getString(R.string.msg_use_request_use_coupon) + " " + popupData.title);
        mAcceptButton.setOnClickListener(this);
        mRejectButton.setOnClickListener(this);

        Picasso ps = Picasso.with(mContext);
        ps.load(popupData.getImageUrl())
                .resize(fullImageSize, fullImageSize)
                .centerInside()
                .into(mCouponImage);
    }

    @Override
    public void onClick(View v) {
        if (v == mAcceptButton) {
            processCoupon("approve");
        } else if (v == mRejectButton) {
            processCoupon("reject");
        }

    }

    void processCoupon(String status) {
        CouponAcceptInfo.Request request = new CouponAcceptInfo.Request();
        request.coupon_id = this.popupData.coupon_id;
        request.action = status;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, request);
        params.putString(Key.TokenKey, Utils.getPrefString(this.mContext, Key.TokenKey));

        Utils.showProgress(mContext, mContext.getString(R.string.msg_processing));
        CouponAcceptCommunicator communicator = new CouponAcceptCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        Utils.hideProgress();
                        alert.dismiss();
                    }
                });
        communicator.execute(params);
    }
}
