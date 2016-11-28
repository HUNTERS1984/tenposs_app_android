package jp.tenposs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.Utils;


/**
 * Created by ambient on 8/29/16.
 */

public class ProductDescription
        extends
        LinearLayout
        implements
        View.OnClickListener {

    Button mProductDetailButton;
    Button mProductSizeButton;
    TextView mNoDataLabel;
    TextView mProductDescriptionLabel;
    WebView mTableLayout;
    //    TableView mProductSizeLayout;
    ItemsInfo.Item mViewData;
//    SizeAdapter mAdapter;

    boolean showDescription = true;
    Context mContext;

    public ProductDescription(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProductDescription(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public ProductDescription(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void reloadData(Serializable serializable) {
        mViewData = (ItemsInfo.Item) serializable;

        mProductDetailButton = (Button) findViewById(R.id.product_detail_button);
        mProductSizeButton = (Button) findViewById(R.id.product_size_button);
        mProductDescriptionLabel = (TextView) findViewById(R.id.product_description_label);
//        mProductSizeLayout = (TableView) findViewById(R.id.product_size_layout);
        mTableLayout = (WebView) findViewById(R.id.web_view);

        mNoDataLabel = (TextView) findViewById(R.id.no_data_label);

        mProductDetailButton.setOnClickListener(this);
        mProductSizeButton.setOnClickListener(this);

        mProductDescriptionLabel.setText(mViewData.getDescription());

        if (this.mViewData.hasSizes() == true) {
//            if (mAdapter == null) {
//                mAdapter = new SizeAdapter(this.getContext(), this);
//                mProductSizeLayout.setAdapter(this.mAdapter);
//            } else {
//                mAdapter.notifyDataSetChanged();
//            }
            BuildTable();
        }
        showDescriptionOrSize();
    }

    @Override
    public void onClick(View v) {
        if (v == mProductDetailButton) {
            showDescription = true;
        } else if (v == mProductSizeButton) {
            showDescription = false;
        }
        showDescriptionOrSize();
    }

    void showDescriptionOrSize() {
        if (showDescription == true) {
            mProductDetailButton.setTextColor(Utils.getColorInt(getContext(), R.color.category_text_color));
            mProductDetailButton.setBackgroundResource(R.drawable.bg_tab_button);

            mProductSizeButton.setTextColor(Utils.getColorInt(getContext(), R.color.description_text_color));
            mProductSizeButton.setBackgroundResource(R.drawable.bg_tab_button_inactive);

            mProductDescriptionLabel.setVisibility(VISIBLE);
//            mProductSizeLayout.setVisibility(GONE);
            mTableLayout.setVisibility(GONE);
            if (mViewData.getDescription().length() > 0) {
                mProductDescriptionLabel.setVisibility(VISIBLE);
                mNoDataLabel.setVisibility(GONE);
            } else {
                mProductDescriptionLabel.setVisibility(GONE);
                mNoDataLabel.setVisibility(VISIBLE);
            }
        } else {
            mProductSizeButton.setTextColor(Utils.getColorInt(getContext(), R.color.category_text_color));
            mProductSizeButton.setBackgroundResource(R.drawable.bg_tab_button);

            mProductDetailButton.setTextColor(Utils.getColorInt(getContext(), R.color.description_text_color));
            mProductDetailButton.setBackgroundResource(R.drawable.bg_tab_button_inactive);

            mProductDescriptionLabel.setVisibility(GONE);

            if (this.mViewData.hasSizes() == true) {
                mTableLayout.setVisibility(VISIBLE);
                mNoDataLabel.setVisibility(GONE);
            } else {
                mTableLayout.setVisibility(GONE);
                mNoDataLabel.setVisibility(VISIBLE);
            }
        }
    }

    String encode(String input) {
        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return input;
        }
    }

    private void BuildTable() {
        int rows = mViewData.numberOfRows() + 1;
        int cols = mViewData.numberOfColumns() + 1;
        ArrayList<ArrayList<String>> data = mViewData.getTableData();
        String str = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "        <link href=\"https://m.ten-po.com/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "        <link href=\"https://m.ten-po.com/css/reset.css\" rel=\"stylesheet\">\n" +
                "        <body>\n" +
                "            <table class=\"table table-bordered\">\n" +
                "                <thead>\n" +
                "                    <tr>\n";
        //Table header
        ArrayList<String> header = data.get(0);
        for (String s : header) {
            str += "                        <th style=\"text-align: center;\">" + s + "</th>\n";
        }
        str += "                    </tr>\n" +
                "                </thead>\n" +
                "                <tbody>\n";
        //Table body
        for (int row = 1; row < rows; row++) {
            ArrayList<String> line = data.get(row);
            str += "                    <tr>\n";
            str += "                        <td style=\"text-align: center;\" class=\"col-md-2\">" + line.get(0) + "</td>\n";

            for (int column = 1; column < cols; column++) {
                str += "                        <td class=\"col-md-2\" style=\"text-align: center;\">" + line.get(column) + "</td>\n";
            }
            str += "                    </tr>\n";
        }
        str += "                </tbody>\n" +
                "            </table>\n" +
                "        </body>\n" +
                "</html>\n";
        WebSettings settings = mTableLayout.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        mTableLayout.loadData(str, "text/html; charset=utf-8", "utf-8");
    }
}
