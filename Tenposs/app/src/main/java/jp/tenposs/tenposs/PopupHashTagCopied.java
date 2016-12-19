package jp.tenposs.tenposs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by ambient on 8/29/16.
 */

public class PopupHashTagCopied {
    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected View contentView;

    ImageButton closeButton;
    Context mContext;


    public PopupHashTagCopied(Context context) {
        this.mContext = context;
    }

    public void show() {
        alertBuilder = new AlertDialog.Builder(this.mContext, R.style.CustomDialog);

        contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_coupon_copied, null);
        closeButton = (ImageButton) contentView.findViewById(R.id.close_button);

        alertBuilder.setView(contentView);
        alertBuilder.setCancelable(false);
        contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alert = alertBuilder.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


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
