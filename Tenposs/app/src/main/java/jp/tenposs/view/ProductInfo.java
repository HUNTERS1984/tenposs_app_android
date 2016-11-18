package jp.tenposs.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 8/29/16.
 */

public class ProductInfo extends LinearLayout {

    ImageView mItemImage;
    TextView mTitleLabel;
    TextView mBrandLabel;
    TextView mPriceLabel;

    Context mContext;

    public ProductInfo(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProductInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public ProductInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void reloadData(Bundle extras) {
        try {
            mItemImage = (ImageView) findViewById(R.id.item_image);
            mTitleLabel = (TextView) findViewById(R.id.item_title_label);
            mBrandLabel = (TextView) findViewById(R.id.item_brand_label);
            mPriceLabel = (TextView) findViewById(R.id.item_price_label);

            mBrandLabel.setText(extras.getString(RecyclerItemWrapper.ITEM_BRAND));
            mTitleLabel.setText(extras.getString(RecyclerItemWrapper.ITEM_DESCRIPTION));
            mPriceLabel.setText(extras.getString(RecyclerItemWrapper.ITEM_PRICE));

            Picasso ps = Picasso.with(mContext);
            ps.load(extras.getString(RecyclerItemWrapper.ITEM_IMAGE))
                    .centerCrop()
                    .into(mItemImage);
        } catch (Exception ignored) {

        }
    }
}
