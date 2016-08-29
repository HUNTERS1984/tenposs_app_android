package jp.tenposs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.io.Serializable;

/**
 * Created by ambient on 8/29/16.
 */

public class ProductDescription extends LinearLayout {

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
    }
}
