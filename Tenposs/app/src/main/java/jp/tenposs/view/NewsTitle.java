package jp.tenposs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 8/29/16.
 */

public class NewsTitle extends LinearLayout {
    TextView unknown_label;
    TextView news_title_label;
    TextView news_created_label;

    NewsInfo.News itemData;

    public NewsTitle(Context context) {
        super(context);
    }

    public NewsTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reloadData(Serializable serializable) {
        itemData = (NewsInfo.News) serializable;
        unknown_label = (TextView) findViewById(R.id.unknown_label);
        news_title_label = (TextView) findViewById(R.id.product_name_label);
        news_created_label = (TextView) findViewById(R.id.product_price_label);

        news_title_label.setText(itemData.title);
        news_created_label.setText(itemData.getCreatedDate());

    }
}
