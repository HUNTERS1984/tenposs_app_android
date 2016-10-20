package jp.tenposs.staffapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.ScreenDataStatus;

/**
 * Created by ambient on 10/14/16.
 */

public class FragmentCouponDetail extends AbstractFragment {

    CouponInfo.Coupon mScreenData;
    ImageView mQRImage;

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.coupon_detail);
        mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void clearScreenData() {

    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        try {
            generateQRCode_general(this.mScreenData.title);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coupon_detail, null);
        this.mQRImage = (ImageView) root.findViewById(R.id.qr_image);
        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (CouponInfo.Coupon) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putSerializable(SCREEN_DATA, this.mScreenData);
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return false;
    }

    private void generateQRCode_general(String data) throws WriterException {
        com.google.zxing.Writer writer = new QRCodeWriter();
        String finaldata = Uri.encode(data, "utf-8");

        int size = 300;
        BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, size, size);
        Bitmap ImageBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < size; i++) {//width
            for (int j = 0; j < size; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }

        if (ImageBitmap != null) {
            mQRImage.setImageBitmap(ImageBitmap);
        } else {
            mQRImage.setImageBitmap(null);
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.userInputError),
            //Toast.LENGTH_SHORT).show();
        }
    }
}
