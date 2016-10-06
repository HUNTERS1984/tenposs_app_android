package jp.tenposs.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.AbstractFragment;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 8/29/16.
 */

public class ProductTitle extends LinearLayout {
    TextView mProductCategoryLabel;
    TextView mProductNameLabel;
    TextView mProductPriceLabel;

    Button mPurchaseButton;

    ItemsInfo.Item mItemData;


    public ProductTitle(Context context) {
        super(context);
    }

    public ProductTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reloadData(Serializable serializable, final int position, final OnCommonItemClickListener clickListener) {

        this.mItemData = (ItemsInfo.Item) serializable;
        this.mProductCategoryLabel = (TextView) findViewById(R.id.product_category_label);
        this.mProductNameLabel = (TextView) findViewById(R.id.product_name_label);
        this.mProductPriceLabel = (TextView) findViewById(R.id.product_price_label);
        this.mPurchaseButton = (Button) findViewById(R.id.purchase_button);

        this.mProductNameLabel.setText(mItemData.title);
        this.mProductPriceLabel.setText(mItemData.getPrice());
        this.mPurchaseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt(RecyclerItemWrapper.ITEM_ID, ProductTitle.this.mItemData.id);
                extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_PURCHASE_SCREEN);
                extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, ProductTitle.this.mItemData);
                clickListener.onCommonItemClick(position, extras);
            }
        });
    }
}
