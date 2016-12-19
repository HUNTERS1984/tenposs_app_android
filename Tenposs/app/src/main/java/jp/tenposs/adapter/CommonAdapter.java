package jp.tenposs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import junit.framework.Assert;

import java.util.ArrayList;

import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.ProductDescription;
import jp.tenposs.view.ProductTitle;

/**
 * Created by ambient on 7/29/16.
 */
//public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.CommonViewHolder> {
public class CommonAdapter extends AbstractRecyclerAdapter<CommonAdapter.CommonViewHolder> {

    public CommonAdapter(Context context, RecyclerDataSource data, OnCommonItemClickListener listener) {
        super(context, data, listener);
    }


    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRow = null;

        RecyclerItemType itemType = RecyclerItemType.fromInt(viewType);
        switch (itemType) {
            case RecyclerItemTypeTop: {
                mRow = mInflater.inflate(R.layout.common_film_strip_layout, parent, false);
            }
            break;
            case RecyclerItemTypeHeader: {
                if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                    mRow = mInflater.inflate(R.layout.restaurant_item_header, parent, false);
                } else {
                    mRow = mInflater.inflate(R.layout.common_item_header, parent, false);
                }
            }
            break;

            case RecyclerItemTypeList: {
                mRow = mInflater.inflate(R.layout.common_item_list, parent, false);
            }
            break;

            case RecyclerItemTypeStore: {
                mRow = mInflater.inflate(R.layout.common_item_map, parent, false);
            }
            break;

            case RecyclerItemTypeGridImage: {
                mRow = mInflater.inflate(R.layout.common_item_grid_image, parent, false);
            }
            break;

            case RecyclerItemTypeGridItem: {
                mRow = mInflater.inflate(R.layout.common_item_grid_item, parent, false);
            }
            break;

            case RecyclerItemTypeGridStaff: {
                mRow = mInflater.inflate(R.layout.common_item_grid_image, parent, false);
            }
            break;

            case RecyclerItemTypeProductImage: {
                mRow = mInflater.inflate(R.layout.product_item_image, parent, false);

            }
            break;

            case RecyclerItemTypeProductTitle: {
                mRow = mInflater.inflate(R.layout.product_item_title, parent, false);

            }
            break;

            case RecyclerItemTypeProductDescription: {
                mRow = mInflater.inflate(R.layout.product_item_description, parent, false);
            }
            break;

            case RecyclerItemTypeFooter: {
                mRow = mInflater.inflate(R.layout.common_item_footer, parent, false);
            }
            break;
        }

        CommonViewHolder viewHolder = null;
        if (mRow != null) {
            viewHolder = new CommonViewHolder(mRow, itemType, mContext, mClickListener);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        final RecyclerItemWrapper itemData = getItemData(position);

        final int itemPosition = position;

        holder.configureCell(itemPosition, itemData);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    Bundle extraData = new Bundle();
                    extraData.putInt(RecyclerItemType.class.getName(), itemData.itemType.ordinal());
                    mClickListener.onCommonItemClick(itemPosition, extraData);
                }
            }
        });
    }


    //View Holder
    public class CommonViewHolder
            extends AbstractRecyclerViewHolder
            implements ViewPager.OnPageChangeListener {

        //Grid
        LinearLayout itemInfoLayout;
        ImageView itemImage;

        TextView itemCategoryLabel;
        TextView itemTitleLabel;
        TextView itemDescriptionLabel;
        TextView itemPriceLabel;

        //Store
        ImageView mapImage;
        Button mapButton;
        ImageView locationIcon;
        TextView locationLabel;

        ImageView timeIcon;
        TextView timeLabel;

        ImageView phoneIcon;
        TextView phoneLabel;

        //List

        //Top Item
        ViewPager topItemViewPager;

        TextView headerTitle;

        //Footer
        Button footerButton;

        //Product Title
        ProductTitle productTitle;

        //Product Description
        ProductDescription productDescription;

        int dotsCount;
        ImageView[] dots;
        LinearLayout pager_indicator;


        public CommonViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
            super(v, itemType, context, l);
        }

        @Override
        public void setUpView() {
            switch (this.itemType) {
                case RecyclerItemTypeTop: {
                    this.topItemViewPager = (ViewPager) this.mRow.findViewById(R.id.view_pager);
                    this.pager_indicator = (LinearLayout) mRow.findViewById(R.id.view_pager_dots_layout);
                }
                break;

                case RecyclerItemTypeHeader: {
                    headerTitle = (TextView) this.mRow.findViewById(R.id.header_label);
                }
                break;

                case RecyclerItemTypeStore: {
                    mapImage = (ImageView) this.mRow.findViewById(R.id.map_image);
                    mapButton = (Button) this.mRow.findViewById(R.id.map_button);
                    locationIcon = (ImageView) this.mRow.findViewById(R.id.location_icon);
                    locationLabel = (TextView) this.mRow.findViewById(R.id.location_label);

                    timeIcon = (ImageView) this.mRow.findViewById(R.id.time_icon);
                    timeLabel = (TextView) this.mRow.findViewById(R.id.time_label);

                    phoneIcon = (ImageView) this.mRow.findViewById(R.id.phone_icon);
                    phoneLabel = (TextView) this.mRow.findViewById(R.id.phone_label);

                }
                break;

                case RecyclerItemTypeList:
                case RecyclerItemTypeGridImage:
                case RecyclerItemTypeGridItem:
                case RecyclerItemTypeGridStaff: {
                    itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                    itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                    itemCategoryLabel = (TextView) this.mRow.findViewById(R.id.item_category_label);
                    itemTitleLabel = (TextView) this.mRow.findViewById(R.id.item_title_label);
                    itemDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_description_label);
                    itemPriceLabel = (TextView) this.mRow.findViewById(R.id.item_price_label);
                }
                break;

                case RecyclerItemTypeProductImage: {
                    itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                }
                break;

                case RecyclerItemTypeProductTitle: {
                    productTitle = (ProductTitle) this.mRow.findViewById(R.id.product_title);

                }
                break;

                case RecyclerItemTypeProductDescription: {
                    productDescription = (ProductDescription) this.mRow.findViewById(R.id.product_description);
                }
                break;

                case RecyclerItemTypeFooter: {
                    footerButton = (Button) this.mRow.findViewById(R.id.footer_button);
                }
                break;
                default:
                    Assert.assertFalse("Should never be here!", false);
                    break;
            }
        }

        @Override
        public void configureCell(final int itemPosition, final RecyclerItemWrapper itemDataWrapper) {
            switch (this.itemType) {
                case RecyclerItemTypeTop: {
                    ArrayList<TopInfo.Image> topItems = (ArrayList<TopInfo.Image>)
                            itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);

                    FilmstripAdapter adapter = new FilmstripAdapter(mContext, topItems,
                            new OnCommonItemClickListener() {
                                @Override
                                public void onCommonItemClick(int position, Bundle extraData) {
                                    extraData.putInt(RecyclerItemType.class.getName(), itemType.ordinal());
                                }
                            });
                    this.topItemViewPager.setAdapter(adapter);
                    this.topItemViewPager.removeOnPageChangeListener(this);
                    this.topItemViewPager.addOnPageChangeListener(this);
                    setUiPageViewController();
                }
                break;

                case RecyclerItemTypeHeader: {
                    headerTitle.setText(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_TITLE));
                }
                break;

                case RecyclerItemTypeStore: {
                    TopInfo.Contact contact = (TopInfo.Contact)
                            itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                    Picasso ps = Picasso.with(mContext);
                    String url = "http://maps.googleapis.com/maps/api/staticmap?center=" + contact.getLocation() +
                            "&zoom=10" +
                            "&scale=1" +
                            "&size=500x200" +
                            "&maptype=roadmap" +
                            "&format=png" +
                            "&visual_refresh=true" +
                            "&markers=size:mid%7Ccolor:0xff0000%7Clabel:%7C" + contact.getLocation();

                    ps.load(url)
                            .placeholder(R.drawable.drop)
                            .into(mapImage);

                    mapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle extra = new Bundle();
                            extra.putString(RecyclerItemWrapper.ITEM_TYPE, "show_map");
                            mClickListener.onCommonItemClick(itemPosition, extra);
                        }
                    });
                    locationIcon.setImageBitmap(FontIcon.imageForFontIdentifier(mContext.getAssets(),
                            "flaticon-placeholder",
                            Utils.NavIconSize,
                            Color.argb(0, 0, 0, 0),
                            Color.argb(255, 128, 128, 128),
                            FontIcon.FLATICON
                    ));

                    timeIcon.setImageBitmap(FontIcon.imageForFontIdentifier(mContext.getAssets(),
                            "flaticon-clock",
                            Utils.NavIconSize,
                            Color.argb(0, 0, 0, 0),
                            Color.argb(255, 128, 128, 128),
                            FontIcon.FLATICON
                    ));

                    locationLabel.setText(contact.getTitle());

                    String startTime = Utils.timeStringFromDate(Utils.dateFromString(contact.start_time), "a hh:mm");
                    String endTime = Utils.timeStringFromDate(Utils.dateFromString(contact.end_time), "a hh:mm");
                    String time = startTime + " - " + endTime;
                    timeLabel.setText(time);

                    phoneIcon.setImageBitmap(FontIcon.imageForFontIdentifier(mContext.getAssets(),
                            "flaticon-phone",
                            Utils.NavIconSize,
                            Color.argb(0, 0, 0, 0),
                            Color.argb(255, 128, 128, 128),
                            FontIcon.FLATICON
                    ));
                    Utils.setTextViewHTML(phoneLabel, "<a href='about:blank'><u>" + contact.tel + "</u></a>",
                            new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    Bundle extra = new Bundle();
                                    extra.putString(RecyclerItemWrapper.ITEM_TYPE, "call_phone");
                                    mClickListener.onCommonItemClick(itemPosition, extra);
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    ds.setColor(Utils.getColorInt(mContext, R.color.phone_text_color));
                                    ds.setUnderlineText(true);
                                }
                            });
                }
                break;

                case RecyclerItemTypeList:
                case RecyclerItemTypeGridImage:
                case RecyclerItemTypeGridItem:
                case RecyclerItemTypeGridStaff: {

                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .placeholder(R.drawable.drop)
                            .into(itemImage);

                    if (itemInfoLayout != null) {
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
                    }
                }
                break;

                case RecyclerItemTypeProductImage: {
                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .resize(thumbImageSize, thumbImageSize)
                            .placeholder(R.drawable.drop)
                            .centerCrop()
                            .into(itemImage);
                }
                break;

                case RecyclerItemTypeProductTitle: {
                    productTitle.reloadData(itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT), itemPosition, mClickListener);
                }
                break;

                case RecyclerItemTypeProductDescription: {
                    productDescription.reloadData(itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT));
                }
                break;

                case RecyclerItemTypeFooter: {
                    footerButton.setBackgroundResource(itemDataWrapper.itemData.getInt(RecyclerItemWrapper.ITEM_BACKGROUND));
                    int color;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        color = mContext.getColor(itemDataWrapper.itemData.getInt(RecyclerItemWrapper.ITEM_TEXT_COLOR));
                    } else {
                        color = mContext.getResources().getColor(itemDataWrapper.itemData.getInt(RecyclerItemWrapper.ITEM_TEXT_COLOR));
                    }
                    footerButton.setText(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_TITLE));
                    footerButton.setTextColor(color);
                    footerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle extraData = new Bundle();
                            extraData.putInt(RecyclerItemType.class.getName(), itemDataWrapper.itemType.ordinal());
                            mClickListener.onCommonItemClick(itemPosition, itemDataWrapper.itemData);
                        }
                    });
                }
                break;

                default:
                    break;
            }
        }

        private void setUiPageViewController() {
            pager_indicator.removeAllViews();
            dotsCount = this.topItemViewPager.getAdapter().getCount();
            dots = new ImageView[dotsCount];
            Drawable selected;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                selected = mContext.getResources().getDrawable(R.drawable.selecteditem_dot);
            } else {
                selected = mContext.getResources().getDrawable(R.drawable.selecteditem_dot, null);
            }
            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this.mContext);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dots[i].setImageDrawable(mContext.getResources().getDrawable(R.drawable.nonselecteditem_dot, null));
                } else {
                    dots[i].setImageDrawable(mContext.getResources().getDrawable(R.drawable.nonselecteditem_dot));
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(4, 0, 4, 0);

                pager_indicator.addView(dots[i], params);
            }

            dots[0].setImageDrawable(selected);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            Drawable nonSelected = null;
            Drawable selected = null;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                nonSelected = mContext.getResources().getDrawable(R.drawable.nonselecteditem_dot);
                selected = mContext.getResources().getDrawable(R.drawable.selecteditem_dot);
            } else {
                nonSelected = mContext.getResources().getDrawable(R.drawable.nonselecteditem_dot, null);
                selected = mContext.getResources().getDrawable(R.drawable.selecteditem_dot, null);
            }
            for (int i = 0; i < dotsCount; i++) {
                dots[i].setImageDrawable(nonSelected);
            }

            dots[position].setImageDrawable(selected);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}