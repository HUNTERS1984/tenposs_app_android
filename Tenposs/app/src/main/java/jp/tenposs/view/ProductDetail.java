package jp.tenposs.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/29/16.
 */

public class ProductDetail
        extends
        LinearLayout
        implements
        View.OnClickListener {

    TextView mHeaderLabel;
    TextView mDetailLabel;
    Button mMoreButton;
    int mShowMore = -1;

    boolean showDescription = true;

    public ProductDetail(Context context) {
        super(context);
    }

    public ProductDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductDetail(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reloadData(Bundle extras) {

        mHeaderLabel = (TextView) findViewById(R.id.header_label);
        mDetailLabel = (TextView) findViewById(R.id.detail_label);
        mMoreButton = (Button) findViewById(R.id.more_button);

        mHeaderLabel.setText(extras.getString(RecyclerItemWrapper.ITEM_CATEGORY));
        Utils.setTextViewHTML(mDetailLabel, extras.getString(RecyclerItemWrapper.ITEM_DESCRIPTION), null);


        if (mShowMore == -1) {
            //Moi init, chua xac
            mMoreButton.setVisibility(GONE);
            showOrHideMoreButton(100);

        } else {
            mMoreButton.setVisibility(VISIBLE);
            if (mShowMore == 1) {
                //Da show more
                mMoreButton.setText(R.string.collapse);
                mDetailLabel.setMaxLines(1000);
            } else {
                //chua show more
                mMoreButton.setText(R.string.more);
                mDetailLabel.setMaxLines(3);

            }
        }
        mMoreButton.setOnClickListener(this);
    }

    private void showOrHideMoreButton(final long delays) {
        Handler mainHandler = new Handler(getContext().getMainLooper());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Layout l = mDetailLabel.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    if (lines > 0) {
                        if (l.getEllipsisCount(lines - 1) > 0) {
                            mMoreButton.setText(R.string.more);
                            mMoreButton.setVisibility(VISIBLE);
                            mShowMore = 0;
                        } else {
                            mMoreButton.setVisibility(GONE);
                        }
                    }
                } else {
                    showOrHideMoreButton(delays);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (mShowMore == 0) {
            mShowMore = 1;
            mDetailLabel.setMaxLines(1000);
            mMoreButton.setText(R.string.collapse);
        } else {
            mShowMore = 0;
            mDetailLabel.setMaxLines(3);
            mMoreButton.setText(R.string.more);
        }

//        if (v == mProductDetailButton) {
//            showDescription = true;
//        } else if (v == mProductSizeButton) {
//            showDescription = false;
//        }
//        showDescriptionOrSize();
    }
}
