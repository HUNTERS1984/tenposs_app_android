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
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 11/12/16.
 */

public class HorizontalAdapter
        extends AbstractRecyclerAdapter<HorizontalAdapter.HorizontalViewHolder> {

    int mPosition;

    public HorizontalAdapter(Context context, RecyclerDataSource data, OnCommonItemClickListener listener, int position) {
        super(context, data, listener);
        this.mPosition = position;
    }

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRow = this.mInflater.inflate(R.layout.restaurant_item_grid_horizontal, parent, false);
        HorizontalViewHolder viewHolder = null;
        RecyclerItemType itemType = RecyclerItemType.fromInt(viewType);
        if (mRow != null) {
            viewHolder = new HorizontalViewHolder(mRow, itemType, mContext, mClickListener);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {

        final RecyclerItemWrapper itemData = getItemData(position);

        holder.configureCell(position, itemData);

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
                            //Bundle extraData = new Bundle();
                            //extraData.putInt(RecyclerItemType.class.getName(), itemData.itemType.ordinal());
                            //extraData.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, itemData.itemData.getInt());
                            mClickListener.onCommonItemClick(mPosition, itemData.itemData);
                        }
                    }
                });
    }


    public class HorizontalViewHolder extends AbstractRecyclerViewHolder {

        private ImageView itemImage;
        private LinearLayout itemInfoLayout;
        private TextView itemDescriptionLabel;
        private TextView itemCategoryLabel;
        private TextView itemTitleLabel;
        private TextView itemPriceLabel;

        public HorizontalViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
            super(v, itemType, context, l);

        }

        @Override
        public void setUpView() {
            itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
            itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
            itemDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_description_label);
            itemCategoryLabel = (TextView) this.mRow.findViewById(R.id.item_category_label);
            itemTitleLabel = (TextView) this.mRow.findViewById(R.id.item_title_label);
            itemPriceLabel = (TextView) this.mRow.findViewById(R.id.item_price_label);
        }

        @Override
        public void configureCell(int itemPosition, RecyclerItemWrapper itemDataWrapper) {
            Picasso ps = Picasso.with(mContext);
            ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
//                            .resize(thumbImageSize, thumbImageSize)
                    .placeholder(R.drawable.drop)
//                            .centerCrop()
                    .into(itemImage);

            itemInfoLayout.setVisibility(View.VISIBLE);

            if (itemCategoryLabel != null) {
                String itemCategory = itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_CATEGORY);
                if (itemCategory != null) {
                    itemCategoryLabel.setText(itemCategory);
                } else {
                    itemCategoryLabel.setVisibility(View.GONE);
                }
            }

            if (itemTitleLabel != null) {
                String itemTitle = itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_TITLE);
                if (itemTitle != null) {
                    itemTitleLabel.setText(itemTitle);
                } else {
                    itemTitleLabel.setVisibility(View.GONE);
                }
            }

            if (itemDescriptionLabel != null) {
                String itemDescription = itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_DESCRIPTION);
                if (itemDescription != null) {
                    Utils.setTextViewHTML(itemDescriptionLabel, itemDescription, null);
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
        }
    }
}
