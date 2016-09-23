package jp.tenposs.tenposs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

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

        closeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                }
        );
    }
}
