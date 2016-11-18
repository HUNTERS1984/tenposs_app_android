package jp.tenposs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.adapter.TableAdapter;
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
    TableView mProductSizeLayout;
    ItemsInfo.Item mViewData;
    SizeAdapter mAdapter;

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
        mViewData = (ItemsInfo.Item) serializable;

        mProductDetailButton = (Button) findViewById(R.id.product_detail_button);
        mProductSizeButton = (Button) findViewById(R.id.product_size_button);
        mProductDescriptionLabel = (TextView) findViewById(R.id.product_description_label);
        mProductSizeLayout = (TableView) findViewById(R.id.product_size_layout);

        mNoDataLabel = (TextView) findViewById(R.id.no_data_label);

        mProductDetailButton.setOnClickListener(this);
        mProductSizeButton.setOnClickListener(this);

        mProductDescriptionLabel.setText(mViewData.description);

        if (this.mViewData.hasSizes() == true) {
            if (mAdapter == null) {
                mAdapter = new SizeAdapter(this.getContext(), this);
                mProductSizeLayout.setAdapter(this.mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
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
            mProductSizeLayout.setVisibility(GONE);

            if (mViewData.description != null && mViewData.description.length() > 0) {
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
                mProductSizeLayout.setVisibility(VISIBLE);
                mNoDataLabel.setVisibility(GONE);
            } else {
                mProductSizeLayout.setVisibility(GONE);
                mNoDataLabel.setVisibility(VISIBLE);
            }
        }
    }

    public int numberOfRows() {
        return this.mViewData.numberOfRows();
    }

    public int numberOfColumns() {
        return this.mViewData.numberOfColumns();
    }

    public ArrayList<String> getTableHeaders() {
        return this.mViewData.getTableHeaders();
    }

    public ArrayList<String> getTableItems() {
        return this.mViewData.getTableItems();
    }


    public class SizeAdapter extends TableAdapter {

        final static int FIRST_HEADER = 0;
        final static int HEADER = 1;
        final static int FIRST_BODY = 2;
        final static int BODY = 3;

        Context mContext;
        private final float mDensity;
        LayoutInflater mInflater;
        ProductDescription mDataSource;

        public SizeAdapter(Context context, ProductDescription dataSource) {
            this.mContext = context;
            this.mDataSource = dataSource;
            this.mDensity = context.getResources().getDisplayMetrics().density;
            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getRowCount() {
            return this.mDataSource.numberOfRows();
        }

        @Override
        public int getColumnCount() {
            return this.mDataSource.numberOfColumns();
        }

        @Override
        public View getView(int row, int column, View convertView, ViewGroup parent) {
            final View view;
            switch (getItemViewType(row, column)) {
                case FIRST_HEADER:
                    view = getFirstHeader(row, column, convertView, parent);
                    break;

                case HEADER:
                    view = getHeader(row, column, convertView, parent);
                    break;

                case FIRST_BODY:
                    view = getFirstBody(row, column, convertView, parent);
                    break;

                case BODY:
                    view = getBody(row, column, convertView, parent);
                    break;

                default:
                    throw new RuntimeException("wtf?");
            }
            return view;
        }

        private View getFirstHeader(int row, int column, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.item_table_header_first, parent, false);
            }
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(mDataSource.getTableHeaders().get(0));
            return convertView;
        }

        private View getHeader(int row, int column, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.item_table_header, parent, false);
            }
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(mDataSource.getTableHeaders().get(column + 1));
            return convertView;
        }

        private View getFirstBody(int row, int column, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.item_table_first, parent, false);
            }
//            convertView.setBackgroundResource(row % 2 == 0 ? R.drawable.bg_table_color1 : R.drawable.bg_table_color2);
            convertView.setBackgroundResource(R.drawable.bg_table_color1);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(getBodyText(row, column));
            return convertView;
        }

        private View getBody(int row, int column, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.item_table, parent, false);
            }
//            convertView.setBackgroundResource(row % 2 == 0 ? R.drawable.bg_table_color1 : R.drawable.bg_table_color2);
            convertView.setBackgroundResource(R.drawable.bg_table_color1);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(getBodyText(row, column));
            return convertView;
        }

        public String getBodyText(int row, int column) {
            int index = 0;
            int realColumn = this.mDataSource.numberOfColumns() + 1;
            if (column == -1) {
                index = (row * realColumn);
            } else {
                index = (row * realColumn) + column + 1;
            }
            return this.mDataSource.getTableItems().get(index);
        }

        @Override
        public int getWidth(int column) {
            return Math.round(100 * mDensity);
        }

        @Override
        public int getHeight(int row) {
            final int height;
            if (row == -1) {
                height = 35;
            } else {
                height = 45;
            }
            return Math.round(height * mDensity);
        }

        @Override
        public int getItemViewType(int row, int column) {
            final int itemViewType;
            if (row == -1 && column == -1) {
                itemViewType = FIRST_HEADER;
            } else if (row == -1) {
                itemViewType = HEADER;
            } else if (column == -1) {
                itemViewType = FIRST_BODY;
            } else {
                itemViewType = BODY;
            }
            return itemViewType;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }
    }
}
