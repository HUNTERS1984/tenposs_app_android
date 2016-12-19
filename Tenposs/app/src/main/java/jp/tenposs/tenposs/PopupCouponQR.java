package jp.tenposs.tenposs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by ambient on 9/17/16.
 */
public class PopupCouponQR {
    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected Context mContext;
    protected View contentView;

    ImageView imageView;
    Bitmap popupData;
    ImageButton closeButton;

    public PopupCouponQR(Context context) {
        this.mContext = context;
    }

    public void setData(Bitmap extras) {
        popupData = extras;
    }

    public void show() {
        alertBuilder = new AlertDialog.Builder(this.mContext, R.style.CustomDialog);

        contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_qr_preview, null);
        imageView = (ImageView) contentView.findViewById(R.id.item_thumbnail);
        closeButton = (ImageButton) contentView.findViewById(R.id.close_button);

        alertBuilder.setView(contentView);
        alertBuilder.setCancelable(false);
        contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alert = alertBuilder.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        try {
            generateQRCode_general("Not yet defined");
        } catch (WriterException e) {
            e.printStackTrace();
        }
        closeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                }
        );
    }

    private void generateQRCode_general(String data) throws WriterException {
        com.google.zxing.Writer writer = new QRCodeWriter();
        String finaldata = Uri.encode(data, "utf-8");

        int size = 200;
        BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, size, size);
        Bitmap ImageBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < size; i++) {//width
            for (int j = 0; j < size; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }

        if (ImageBitmap != null) {
            imageView.setImageBitmap(ImageBitmap);
        } else {
            imageView.setImageBitmap(null);
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.userInputError),
            //Toast.LENGTH_SHORT).show();
        }
    }
}
