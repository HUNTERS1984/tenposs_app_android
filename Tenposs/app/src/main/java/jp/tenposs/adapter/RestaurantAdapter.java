package jp.tenposs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import jp.tenposs.adapter.widget.SpannableGridLayoutManager;
import jp.tenposs.datamodel.ItemsInfo;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.PhotoInfo;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.AbstractFragment;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.MenuIcon;
import jp.tenposs.view.ProductDescription;
import jp.tenposs.view.ProductDetail;
import jp.tenposs.view.ProductInfo;
import jp.tenposs.view.ProductTitle;

/**
 * Created by ambient on 11/1/16.
 */

public class RestaurantAdapter
        extends AbstractRecyclerAdapter<RestaurantAdapter.RestaurantViewHolder> {
    public RestaurantAdapter(Context context, RecyclerDataSource data, OnCommonItemClickListener listener) {
        super(context, data, listener);
    }

    @Override
    public RestaurantAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRow = null;

        RecyclerItemType itemType = RecyclerItemType.fromInt(viewType);
        switch (itemType) {
            case RecyclerItemTypeTop: {
                mRow = mInflater.inflate(R.layout.restaurant_item_top, parent, false);
            }
            break;

            case RecyclerItemTypeHeader: {
                mRow = mInflater.inflate(R.layout.restaurant_item_header, parent, false);
            }
            break;

            case RecyclerItemTypeFooter: {
                mRow = mInflater.inflate(R.layout.common_item_footer, parent, false);
            }
            break;

            case RecyclerItemTypeList: {
                mRow = mInflater.inflate(R.layout.restaurant_item_list_news, parent, false);
            }
            break;

            case RecyclerItemTypeStore: {
                mRow = mInflater.inflate(R.layout.common_item_map, parent, false);
            }
            break;

            case RecyclerItemTypeRestaurantGridItem: {
                mRow = mInflater.inflate(R.layout.restaurant_item_grid_item, parent, false);
            }
            break;

            case RecyclerItemTypeRestaurantGridStaff: {
                mRow = mInflater.inflate(R.layout.restaurant_item_grid_staff, parent, false);
            }
            break;

            case RecyclerItemTypeRestaurantGridImage: {
                mRow = mInflater.inflate(R.layout.restaurant_item_grid_image, parent, false);
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

            case RecyclerItemTypeRestaurantProductInfo: {
                mRow = mInflater.inflate(R.layout.restaurant_item_info, parent, false);
            }
            break;

            case RecyclerItemTypeRestaurantProductDetail: {
                mRow = mInflater.inflate(R.layout.restaurant_item_detail, parent, false);
            }
            break;

            //Restaurant Template
            case RecyclerItemTypeRestaurantNewsTop: {
                mRow = mInflater.inflate(R.layout.restaurant_news_pager, parent, false);
            }
            break;

            case RecyclerItemTypeRestaurantRecyclerHorizontal:
            case RecyclerItemTypeRestaurantRecyclerVertical: {
                mRow = mInflater.inflate(R.layout.common_item_recycler, parent, false);
            }
            break;

            default:
                Assert.assertFalse("Should never be here! " + itemType, false);
                break;
        }

        RestaurantAdapter.RestaurantViewHolder viewHolder = null;
        if (mRow != null) {
            viewHolder = new RestaurantAdapter.RestaurantViewHolder(mRow, itemType, mContext, mClickListener);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantAdapter.RestaurantViewHolder holder, int position) {
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
                            Bundle extraData = new Bundle();
                            extraData.putInt(RecyclerItemType.class.getName(), itemData.itemType.ordinal());
                            mClickListener.onCommonItemClick(itemPosition, extraData);
                        }
                    }
                });


        boolean spannableLayout = itemData.itemData.getInt(RecyclerItemWrapper.ITEM_LAYOUT_SPAN_ABLE) == 1;
        if (spannableLayout == true) {

            final View itemView = holder.itemView;
            final SpannableGridLayoutManager.LayoutParams lp =
                    (SpannableGridLayoutManager.LayoutParams) itemView.getLayoutParams();
//            final int itemId = position;
//            final int span1 = (itemId == 0 || itemId == 3 ? 2 : 1);
//            final int span2 = (itemId == 0 ? 2 : (itemId == 3 ? 3 : 1));
//
//            final int colSpan = (span2);
//            final int rowSpan = (span1);
//
//            if (lp.rowSpan != rowSpan || lp.colSpan != colSpan) {
//                lp.rowSpan = rowSpan;
//                lp.colSpan = colSpan;
//                itemView.setLayoutParams(lp);
//            }
//
//            Log.e("Spannable", itemPosition + " : " + rowSpan + "," + colSpan);
            lp.rowSpan = itemData.itemData.getInt(RecyclerItemWrapper.ITEM_ROW_SPAN);
            lp.colSpan = itemData.itemData.getInt(RecyclerItemWrapper.ITEM_COLUMN_SPAN);

        }
    }

    //View Holder
    public class RestaurantViewHolder
            extends AbstractRecyclerViewHolder
            implements ViewPager.OnPageChangeListener {

        //Grid
        LinearLayout itemInfoLayout;
        ImageView itemImage;

        TextView itemDescriptionLabel;
        TextView itemCategoryLabel;
        TextView itemTitleLabel;
        TextView itemPriceLabel;
        TextView itemCreateDateTimeLabel;

        //Store
        ImageView mapImage;
        Button mapButton;
        ImageView locationIcon;
        TextView locationLabel;

        ImageView timeIcon;
        TextView timeLabel;

        ImageView phoneIcon;
        TextView phoneLabel;


        Button topCouponButton;
        Button topChatButton;
        Button topPhoneButton;
        Button topMapButton;
        //List

        //Top Item
        ViewPager mViewPager;
        PagerAdapter mViewPagerAdapter;

        ImageView headerImage;
        TextView headerTitle;

        //Footer
        Button footerButton;

        //Product Title
        ProductTitle productTitle;

        //Product Description
        ProductDescription productDescription;


        ProductInfo productInfo;
        ProductDetail productDetail;

        AbstractRecyclerAdapter mRecyclerAdapter;
        RecyclerView mRecyclerView;
        RecyclerView.ItemDecoration mRecyclerDecoration;

        int mViewPagerDotCounts;
        ImageView[] mViewPagerDotItems;
        LinearLayout mViewPagerDotLayout;
        ArrayList<?> mViewPagerData;


        public RestaurantViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
            super(v, itemType, context, l);
        }

        @Override
        public void setUpView() {
            switch (this.itemType) {
                case RecyclerItemTypeStart:
                    break;
                case RecyclerItemTypeTop: {
                    this.mViewPager = (ViewPager) this.mRow.findViewById(R.id.view_pager);
                    this.mViewPagerDotLayout = (LinearLayout) mRow.findViewById(R.id.view_pager_dots_layout);
                    this.topCouponButton = (Button) mRow.findViewById(R.id.coupon_button);
                    this.topChatButton = (Button) mRow.findViewById(R.id.chat_button);
                    this.topPhoneButton = (Button) mRow.findViewById(R.id.phone_button);
                    this.topMapButton = (Button) mRow.findViewById(R.id.direction_button);

                }
                break;

                case RecyclerItemTypeHeader: {
                    headerImage = (ImageView) this.mRow.findViewById(R.id.header_image);
                    headerTitle = (TextView) this.mRow.findViewById(R.id.header_label);
                }
                break;

                case RecyclerItemTypeFooter: {
                    footerButton = (Button) this.mRow.findViewById(R.id.footer_button);
                }
                break;

                case RecyclerItemTypeListDivider:
                    break;
                case RecyclerItemTypeStore: {
                    this.mapImage = (ImageView) this.mRow.findViewById(R.id.map_image);
                    this.mapButton = (Button) this.mRow.findViewById(R.id.map_button);
                    this.locationIcon = (ImageView) this.mRow.findViewById(R.id.location_icon);
                    this.locationLabel = (TextView) this.mRow.findViewById(R.id.location_label);

                    this.timeIcon = (ImageView) this.mRow.findViewById(R.id.time_icon);
                    this.timeLabel = (TextView) this.mRow.findViewById(R.id.time_label);

                    this.phoneIcon = (ImageView) this.mRow.findViewById(R.id.phone_icon);
                    this.phoneLabel = (TextView) this.mRow.findViewById(R.id.phone_label);
                }
                break;

                case RecyclerItemTypeList:
                case RecyclerItemTypeRestaurantGridItem:
                case RecyclerItemTypeRestaurantGridStaff:
                case RecyclerItemTypeRestaurantGridImage: {
                    this.itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                    this.itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                    this.itemCategoryLabel = (TextView) this.mRow.findViewById(R.id.item_category_label);
                    this.itemTitleLabel = (TextView) this.mRow.findViewById(R.id.item_title_label);
                    this.itemDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_description_label);
                    this.itemPriceLabel = (TextView) this.mRow.findViewById(R.id.item_price_label);
                    this.itemCreateDateTimeLabel = (TextView) this.mRow.findViewById(R.id.item_create_date_time_label);
                }
                break;

                case RecyclerItemTypeProductImage: {
                    this.itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                }
                break;

                case RecyclerItemTypeProductTitle: {
                    this.productTitle = (ProductTitle) this.mRow.findViewById(R.id.product_title);

                }
                break;

                case RecyclerItemTypeProductDescription: {
                    this.productDescription = (ProductDescription) this.mRow.findViewById(R.id.product_description);
                }
                break;

                case RecyclerItemTypeRestaurantProductInfo: {
                    this.productInfo = (ProductInfo) this.mRow.findViewById(R.id.product_info);
                }
                break;

                case RecyclerItemTypeRestaurantProductDetail: {
                    this.productDetail = (ProductDetail) this.mRow.findViewById(R.id.product_detail);
                }
                break;


                //Restaurant Template
                case RecyclerItemTypeRestaurantNewsTop: {
                    //TODO:
                    this.mViewPager = (ViewPager) this.mRow.findViewById(R.id.view_pager);
                    this.mViewPagerDotLayout = (LinearLayout) mRow.findViewById(R.id.view_pager_dots_layout);

                    this.itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                    this.itemCategoryLabel = (TextView) this.mRow.findViewById(R.id.item_category_label);
                    this.itemTitleLabel = (TextView) this.mRow.findViewById(R.id.item_title_label);
                    this.itemDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_description_label);
                    this.itemCreateDateTimeLabel = (TextView) this.mRow.findViewById(R.id.item_create_date_label);
                }
                break;

                case RecyclerItemTypeRestaurantRecyclerHorizontal:
                case RecyclerItemTypeRestaurantRecyclerVertical: {
                    this.mRecyclerView = (RecyclerView) this.mRow.findViewById(R.id.recycler_view);
                }
                break;

                default:
                    Assert.assertFalse("Should never be here! " + itemType, false);
                    break;
            }
        }

        @Override
        public void configureCell(final int itemPosition, final RecyclerItemWrapper itemDataWrapper) {
            switch (this.itemType) {
                case RecyclerItemTypeTop: {
                    if (this.mViewPagerAdapter == null) {
                        this.mViewPagerData = (ArrayList<TopInfo.Image>) itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);

                        this.mViewPagerAdapter = new FilmstripAdapter(mContext, (ArrayList<?>) this.mViewPagerData,
                                new OnCommonItemClickListener() {
                                    @Override
                                    public void onCommonItemClick(int position, Bundle extraData) {
                                        extraData.putInt(RecyclerItemType.class.getName(), itemType.ordinal());
                                    }
                                });
                        this.mViewPager.setAdapter(this.mViewPagerAdapter);
                        this.mViewPager.removeOnPageChangeListener(this);
                        this.mViewPager.addOnPageChangeListener(this);
                        setUiPageViewController();
                    }

                    this.topChatButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle extraData = new Bundle();
                            extraData.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.CHAT_SCREEN);
                            mClickListener.onCommonItemClick(itemPosition, extraData);
                        }
                    });
                    this.topCouponButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle extraData = new Bundle();
                            extraData.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.COUPON_SCREEN);
                            mClickListener.onCommonItemClick(itemPosition, extraData);
                        }
                    });
                    this.topPhoneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Bundle extraData = new Bundle();
//                            extraData.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.CHAT_SCREEN);
//                            mClickListener.onCommonItemClick(itemPosition, extraData);
                        }
                    });
//                    this.topMapButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Bundle extraData = new Bundle();
//                            extraData.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.CHAT_SCREEN);
//                            mClickListener.onCommonItemClick(itemPosition, extraData);
//                        }
//                    });
                }
                break;

                case RecyclerItemTypeHeader: {
                    headerImage.setImageDrawable(MenuIcon.getInstance().getHomeIconDrawableWithId(mContext, itemDataWrapper.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID)));
                    headerTitle.setText(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_TITLE));
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
                    footerButton.setTextColor(color);
                    footerButton.setText(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_TITLE));
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
                            .fit()
                            .centerCrop()
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
                                    //mActivityListener.showScreen(AbstractFragment.SIGNUP_SCREEN, null);
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
                case RecyclerItemTypeRestaurantGridItem:
                case RecyclerItemTypeRestaurantGridStaff:
                case RecyclerItemTypeRestaurantGridImage: {
                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .resize(thumbImageSize, thumbImageSize)
                            .placeholder(R.drawable.drop)
                            .centerCrop()
                            .into(itemImage);

                    if (itemInfoLayout != null) {
                        itemInfoLayout.setVisibility(View.VISIBLE);
                    }
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


                    if (itemCreateDateTimeLabel != null) {
                        String itemCreateDateTime = itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_CREATE_DATE_TIME);
                        if (itemCreateDateTime != null) {
                            itemCreateDateTimeLabel.setText(Utils.formatDateTime(itemCreateDateTime, "yyyy-MM-dd HH:mm:ss", "MM月dd日 HH時mm分"));
                        } else {
                            itemCreateDateTimeLabel.setVisibility(View.GONE);
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

                case RecyclerItemTypeRestaurantProductInfo: {
                    productInfo.reloadData(itemDataWrapper.itemData);

                }
                break;

                case RecyclerItemTypeRestaurantProductDetail: {
                    productDetail.reloadData(itemDataWrapper.itemData);
                }
                break;

                //Restaurant Template
                case RecyclerItemTypeRestaurantNewsTop: {
                    if (this.mViewPagerAdapter == null) {
                        this.mViewPagerData = (ArrayList<?>) itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                        this.mViewPagerAdapter = new RestaurantNewsAdapter(mContext, this.mViewPagerData);
                        this.mViewPager.setAdapter(this.mViewPagerAdapter);
                        this.mViewPager.removeOnPageChangeListener(this);
                        this.mViewPager.addOnPageChangeListener(this);
                        setUiPageViewController();

                        try {
                            NewsInfo.News news = (NewsInfo.News) this.mViewPagerData.get(0);
                            this.itemTitleLabel.setText(news.getTitle());
                            this.itemCategoryLabel.setText(news.getCategory());
//                            Utils.setTextViewHTML(this.itemDescriptionLabel, news.getDescription(), null);

                            this.itemCreateDateTimeLabel.setText(Utils.formatDateTime(news.getLastModifyDate(), "yyyy-MM-dd hh:mm:ss", "M月d日 hh時mm分"));
                        } catch (Exception ignored) {

                        }
                    }
                }
                break;

                case RecyclerItemTypeRestaurantRecyclerHorizontal: {
                    ArrayList data = (ArrayList) itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                    final ArrayList<RecyclerItemWrapper> dataItems = new ArrayList<>();

                    Class itemClass = (Class) itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_CLASS);
                    Bundle extras;

                    if (itemClass.equals(ItemsInfo.Item.class)) {
                        for (Object obj : data) {
                            ItemsInfo.Item item = (ItemsInfo.Item) obj;
                            extras = new Bundle();
                            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.ITEM_SCREEN);
                            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                            extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, item.getCategory());
                            extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.getTitle());
                            extras.putString(RecyclerItemWrapper.ITEM_PRICE, item.getPrice());
                            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);

                            dataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantGridItem, 1, extras));
                        }
                    } else if (itemClass.equals(PhotoInfo.Photo.class)) {
                        for (Object obj : data) {
                            PhotoInfo.Photo item = (PhotoInfo.Photo) obj;
                            extras = new Bundle();
                            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PHOTO_ITEM_SCREEN);
                            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                            extras.putString(RecyclerItemWrapper.ITEM_OBJECT, item.getImageUrl());

                            dataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeRestaurantGridImage, 1, extras));
                        }
                    }
                    RecyclerDataSource dataSource = new RecyclerDataSource() {
                        @Override
                        public int getItemCount() {
                            return dataItems.size();
                        }

                        @Override
                        public RecyclerItemWrapper getItemData(int position) {
                            return dataItems.get(position);
                        }
                    };
                    this.mRecyclerAdapter = new HorizontalAdapter(mContext, dataSource, mClickListener, itemPosition);
                    this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
                    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext, LinearLayoutManager.HORIZONTAL, false));
                }
                break;

                case RecyclerItemTypeRestaurantRecyclerVertical: {
                    ArrayList data = (ArrayList) itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                    final ArrayList<RecyclerItemWrapper> dataItems = new ArrayList<>();

                    int screenId = itemDataWrapper.itemData.getInt(RecyclerItemWrapper.ITEM_SCREEN_ID);
                    Class itemClass = (Class) itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_CLASS);
                    Bundle extras;

                    if (itemClass.equals(NewsInfo.News.class)) {
                        for (Object obj : data) {
                            NewsInfo.News item = (NewsInfo.News) obj;
                            extras = new Bundle();
                            extras.putInt(RecyclerItemWrapper.ITEM_ID, item.id);
                            extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, screenId);
                            extras.putString(RecyclerItemWrapper.ITEM_IMAGE, item.getImageUrl());
                            extras.putString(RecyclerItemWrapper.ITEM_CATEGORY, item.getCategory());
                            extras.putString(RecyclerItemWrapper.ITEM_TITLE, item.getTitle());
//                            extras.putString(RecyclerItemWrapper.ITEM_DESCRIPTION, item.getDescription());
                            extras.putSerializable(RecyclerItemWrapper.ITEM_CREATE_DATE_TIME, item.getLastModifyDate());
                            extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, item);

                            dataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeList, 1, extras));

                            dataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeListDivider, 1, new Bundle()));
                        }
                        //Remove last divider
                        int size = dataItems.size();
                        if (size > 1) {
                            dataItems.remove(size - 1);
                        }
                    }
                    RecyclerDataSource dataSource = new RecyclerDataSource() {
                        @Override
                        public int getItemCount() {
                            return dataItems.size();
                        }

                        @Override
                        public RecyclerItemWrapper getItemData(int position) {
                            return dataItems.get(position);
                        }
                    };
                    this.mRecyclerAdapter = new VerticalAdapter(mContext, dataSource, mClickListener, itemPosition);
                    this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
                    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext, LinearLayoutManager.VERTICAL, false) {
                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    });
                    if (this.mRecyclerDecoration == null) {
//                        mRecyclerDecoration = new DividerDecoration(mContext, R.dimen.restaurant_item_margin);
                        mRecyclerDecoration = new MarginDecoration(mContext, R.dimen.restaurant_item_spacing);
                        this.mRecyclerView.addItemDecoration(this.mRecyclerDecoration);
//                        this.mRecyclerView.addItemDecoration(new MarginDecoration(mContext, R.dimen.restaurant_item_margin));
                    }
                }
                break;

                default:
                    Assert.assertFalse("Should never be here! " + itemType, false);
                    break;
            }
        }

        private void setUiPageViewController() {
            mViewPagerDotLayout.removeAllViews();
            mViewPagerDotCounts = this.mViewPager.getAdapter().getCount();
            mViewPagerDotItems = new ImageView[mViewPagerDotCounts];
            Drawable selected;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                selected = mContext.getResources().getDrawable(R.drawable.restaurant_selected_item_dot);
            } else {
                selected = mContext.getResources().getDrawable(R.drawable.restaurant_selected_item_dot, null);
            }
            for (int i = 0; i < mViewPagerDotCounts; i++) {
                mViewPagerDotItems[i] = new ImageView(this.mContext);
                mViewPagerDotItems[i].setImageDrawable(mContext.getResources().getDrawable(R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(4, 0, 4, 0);

                mViewPagerDotLayout.addView(mViewPagerDotItems[i], params);
            }

            mViewPagerDotItems[0].setImageDrawable(selected);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Drawable nonSelected;
            Drawable selected;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                nonSelected = mContext.getResources().getDrawable(R.drawable.nonselecteditem_dot);
                selected = mContext.getResources().getDrawable(R.drawable.restaurant_selected_item_dot);
            } else {
                nonSelected = mContext.getResources().getDrawable(R.drawable.nonselecteditem_dot, null);
                selected = mContext.getResources().getDrawable(R.drawable.restaurant_selected_item_dot, null);
            }

            for (int i = 0; i < mViewPagerDotCounts; i++) {
                mViewPagerDotItems[i].setImageDrawable(nonSelected);
            }

            mViewPagerDotItems[position].setImageDrawable(selected);

            if (this.itemType == RecyclerItemType.RecyclerItemTypeRestaurantNewsTop) {
                NewsInfo.News news = (NewsInfo.News) this.mViewPagerData.get(position);

                this.itemTitleLabel.setText(news.getTitle());
                Utils.setTextViewHTML(this.itemDescriptionLabel, news.getDescription(), null);
                this.itemCreateDateTimeLabel.setText(Utils.formatDateTime(news.getLastModifyDate(), "yyyy-MM-dd hh:mm:ss", "M月d日 hh時mm分"));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
