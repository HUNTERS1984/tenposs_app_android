package jp.tenposs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

import jp.tenposs.datamodel.ItemInfo;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 8/29/16.
 */

public class ProductTitle extends LinearLayout {
    TextView product_category_label;
    TextView product_name_label;
    TextView product_price_label;

    ItemInfo.Item itemData;

    public ProductTitle(Context context) {
        super(context);
    }

    public ProductTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reloadData(Serializable serializable) {
        itemData = (ItemInfo.Item) serializable;
        product_category_label = (TextView) findViewById(R.id.product_category_label);
        product_name_label = (TextView) findViewById(R.id.product_name_label);
        product_price_label = (TextView) findViewById(R.id.product_price_label);

        product_name_label.setText(itemData.title);
        product_price_label.setText(itemData.price);
    }
}
