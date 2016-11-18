package jp.tenposs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 11/12/16.
 */

public class VerticalAdapter extends AbstractRecyclerAdapter<VerticalAdapter.VerticalViewHolder> {

    int mPosition;

    public VerticalAdapter(Context context, RecyclerDataSource data, OnCommonItemClickListener listener, int position) {
        super(context, data, listener);
        this.mPosition = position;
    }

    @Override
    public VerticalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRow;
        VerticalViewHolder viewHolder = null;
        RecyclerItemType itemType = RecyclerItemType.fromInt(viewType);

        if (itemType == RecyclerItemType.RecyclerItemTypeList) {
            mRow = this.mInflater.inflate(R.layout.common_item_list, parent, false);
        } else {
            mRow = this.mInflater.inflate(R.layout.common_item_list_divider, parent, false);
        }
        if (mRow != null) {
            viewHolder = new VerticalViewHolder(mRow, itemType, mContext, mClickListener);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VerticalViewHolder holder, int position) {

        final RecyclerItemWrapper itemData = getItemData(position);

        final int itemPosition = position;

        holder.configureCell(itemPosition, itemData);

        holder.itemView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.onCommonItemClick(mPosition, itemData.itemData);
                        }
                    }
                });
    }


    public class VerticalViewHolder extends AbstractRecyclerViewHolder {

        private ImageView itemImage;
        private LinearLayout itemInfoLayout;
        private TextView itemDescriptionLabel;
        private TextView itemBrandLabel;
        private TextView itemPriceLabel;

        public VerticalViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
            super(v, itemType, context, l);

        }

        @Override
        public void setUpView() {
            if (this.itemType == RecyclerItemType.RecyclerItemTypeList) {
                itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                itemDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_description_label);
                itemBrandLabel = (TextView) this.mRow.findViewById(R.id.item_brand_label);
                itemPriceLabel = (TextView) this.mRow.findViewById(R.id.item_price_label);
            }
        }

        @Override
        public void configureCell(int itemPosition, RecyclerItemWrapper itemDataWrapper) {
            if (itemDataWrapper.itemType == RecyclerItemType.RecyclerItemTypeList) {
                Picasso ps = Picasso.with(mContext);
                ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                        .placeholder(R.drawable.drop)
                        .into(itemImage);

                itemInfoLayout.setVisibility(View.VISIBLE);

                if (itemBrandLabel != null) {
                    String itemCategory = itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_BRAND);
                    if (itemCategory != null) {
                        itemBrandLabel.setText(itemCategory);
                    } else {
                        itemBrandLabel.setVisibility(View.GONE);
                    }
                }

                if (itemDescriptionLabel != null) {
                    String itemDescription = itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_DESCRIPTION);
                    if (itemDescription != null) {
                        itemDescriptionLabel.setText(itemDescription);
                    } else {
                        itemDescriptionLabel.setVisibility(View.GONE);
                    }
                }

                if (itemPriceLabel != null) {
                    String itemPrice = itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_PRICE);
                    if (itemPrice != null) {
                        itemPriceLabel.setText(itemPrice);
                    } else {
                        itemPriceLabel.setVisibility(View.GONE);
                    }
                }
            } else {

            }
        }
    }
}
