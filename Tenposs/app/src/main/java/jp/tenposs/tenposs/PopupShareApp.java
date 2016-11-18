package jp.tenposs.tenposs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import jp.tenposs.datamodel.AppData;

/**
 * Created by ambient on 8/29/16.
 */

public class PopupShareApp implements View.OnClickListener {
    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected View contentView;

    TextView mHashTagLabel;
    Button mCopyHashTagButton;

    ShareButton fbShareButton;
    ImageButton facebookButton;
    ImageButton twitterButton;
    ImageButton instagramButton;

    Button closeButton;

    Context mContext;


    public PopupShareApp(Context context) {
        this.mContext = context;
    }

    public void show() {
        alertBuilder = new AlertDialog.Builder(this.mContext, R.style.CustomDialog);

        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            contentView = LayoutInflater.from(this.mContext).inflate(R.layout.restaurant_popup_share_app, null);
        } else {
            contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_share_app, null);
        }

        mHashTagLabel = (TextView) contentView.findViewById(R.id.hash_tag_label);
        mCopyHashTagButton = (Button) contentView.findViewById(R.id.copy_hash_tag_button);
        fbShareButton = (ShareButton) contentView.findViewById(R.id.fb_share_button);
        facebookButton = (ImageButton) contentView.findViewById(R.id.facebook_button);
        twitterButton = (ImageButton) contentView.findViewById(R.id.twitter_button);
        instagramButton = (ImageButton) contentView.findViewById(R.id.instagram_button);

        closeButton = (Button) contentView.findViewById(R.id.close_button);


        facebookButton.setOnClickListener(this);
        twitterButton.setOnClickListener(this);
        instagramButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        alertBuilder.setView(contentView);
        alertBuilder.setCancelable(false);
        contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alert = alertBuilder.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        if (v == facebookButton) {
            //Share FaceBook
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://videofly.vn"))
                    .setContentDescription("Please install this app using my code ")
                    .build();
            fbShareButton.setShareContent(content);

            fbShareButton.performClick();
        } else if (v == twitterButton) {
            //Share Twitter
            TweetComposer.Builder builder = new TweetComposer.Builder(this.mContext)
                    .text("just setting up my Fabric.");
//                    .image(myImageUri);
            builder.show();

        } else if (v == instagramButton) {
            //share Instagram
        } else if (v == closeButton) {
            alert.dismiss();
        }
    }
}
