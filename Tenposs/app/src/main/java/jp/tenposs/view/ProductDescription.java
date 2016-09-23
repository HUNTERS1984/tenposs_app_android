package jp.tenposs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/29/16.
 */

public class ProductDescription extends LinearLayout implements View.OnClickListener {

    Button productDetailButton;
    Button productSizeButton;
    TextView productDescriptionLabel;
    ItemsInfo.Item screenData;

    boolean showDescription = true;

    public ProductDescription(Context context) {
        super(context);
    }

    public ProductDescription(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductDescription(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reloadData(Serializable serializable) {
        screenData = (ItemsInfo.Item) serializable;

        productDetailButton = (Button) findViewById(R.id.product_detail_button);
        productSizeButton = (Button) findViewById(R.id.product_size_button);
        productDescriptionLabel = (TextView) findViewById(R.id.product_description_label);

        productDetailButton.setOnClickListener(this);
        productSizeButton.setOnClickListener(this);

        productDescriptionLabel.setText(screenData.description);
        showDescriptionOrSize();
    }

    @Override
    public void onClick(View v) {
        if (v == productDetailButton) {
            showDescription = true;
        } else if (v == productSizeButton) {
            showDescription = false;
        }
        showDescriptionOrSize();
    }

    void showDescriptionOrSize() {
        if (showDescription == true) {
            productDetailButton.setTextColor(Utils.getColorInt(getContext(), R.color.category_text_color));
            productDetailButton.setBackgroundResource(R.drawable.bg_tab_button);

            productSizeButton.setTextColor(Utils.getColorInt(getContext(), R.color.description_text_color));
            productSizeButton.setBackgroundResource(R.drawable.bg_tab_button_inactive);
        } else {
            productSizeButton.setTextColor(Utils.getColorInt(getContext(), R.color.category_text_color));
            productSizeButton.setBackgroundResource(R.drawable.bg_tab_button);

            productDetailButton.setTextColor(Utils.getColorInt(getContext(), R.color.description_text_color));
            productDetailButton.setBackgroundResource(R.drawable.bg_tab_button_inactive);
        }
    }
}
