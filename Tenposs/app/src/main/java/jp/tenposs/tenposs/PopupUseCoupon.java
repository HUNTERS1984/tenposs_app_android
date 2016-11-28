package jp.tenposs.tenposs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.Serializable;

import jp.tenposs.communicator.StaffInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.listener.BSSelectionListener;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 9/17/16.
 */
public class PopupUseCoupon implements BSSelectionListener {
    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected Context mContext;
    protected View contentView;

    ImageView imageView;
    CouponInfo.Coupon popupData;
    Button useButton;

    StaffInfo.Response mStaffs;

    public PopupUseCoupon(Context context) {
        this.mContext = context;
    }

    public void setData(CouponInfo.Coupon extras) {
        popupData = extras;
    }

    public void show() {
        alertBuilder = new AlertDialog.Builder(this.mContext, R.style.CustomDialog);

        contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_use_coupon, null);
        imageView = (ImageView) contentView.findViewById(R.id.coupon_qr_image);
        useButton = (Button) contentView.findViewById(R.id.use_button);

        alertBuilder.setView(contentView);
        alertBuilder.setCancelable(true);
        contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alert = alertBuilder.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            generateQRCode_general(popupData.getTitle());
        } catch (WriterException e) {
            e.printStackTrace();
        }
        useButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getStaffsIfNeeded();
                    }
                }
        );
    }

    private void getStaffsIfNeeded() {
        if (Utils.isSignedIn(mContext) == false) {
            Utils.showAlert(mContext,
                    mContext.getString(R.string.info),
                    mContext.getString(R.string.msg_not_sign_in),
                    mContext.getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        if (this.mStaffs == null) {
            Utils.showProgress(mContext, mContext.getString(R.string.msg_loading));
            StaffInfo.Request requestParams = new StaffInfo.Request();
            requestParams.category_id = 0;
            requestParams.pageindex = 1;
            requestParams.pagesize = 10000;

            Bundle params = new Bundle();
            params.putSerializable(Key.RequestObject, requestParams);
            StaffInfoCommunicator communicator = new StaffInfoCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            Utils.hideProgress();
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    mStaffs = (StaffInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    getStaffsIfNeeded();
                                } else {
                                    String strMessage = responseParams.getString(Key.ResponseMessage);
                                    errorWithMessage(responseParams, strMessage);
                                }
                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage);
                            }
                        }
                    });
            communicator.execute(params);
        } else {
            CommonBottomSheetStaffSelection bottomSheet = CommonBottomSheetStaffSelection.newInstance(this.mStaffs, this);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        }
    }

    private void errorWithMessage(Bundle responseParams, String strMessage) {

    }

    private FragmentManager getSupportFragmentManager() {
        return MainApplication.getSupportFragmentManager();
    }

    private void generateQRCode_general(String data) throws WriterException {
        com.google.zxing.Writer writer = new QRCodeWriter();
        String finaldata = Uri.encode(data, "utf-8");

        int size = 300;
        BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, size, size);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < size; i++) {//width
            for (int j = 0; j < size; j++) {//height
                bitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(null);
        }
    }

    @Override
    public void onItemSelect(int position, Serializable extras) {
        //TODO
        //show waiting popup

        //PopupCouponAccepted popupCouponAccepted = new PopupCouponAccepted(this.mContext);
        //popupCouponAccepted.show();

        this.alert.dismiss();
    }

    @Override
    public void onCancel() {
        //DO nothing
        //PopupCouponRejected popupCouponRejected = new PopupCouponRejected(this.mContext);
        //popupCouponRejected.show();
        this.alert.dismiss();
    }
}
