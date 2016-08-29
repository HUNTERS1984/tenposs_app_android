package jp.tenposs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import junit.framework.Assert;

import java.util.ArrayList;

import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.ThemifyIcon;
import jp.tenposs.view.NewsTitle;
import jp.tenposs.view.ProductDescription;
import jp.tenposs.view.ProductTitle;

/**
 * Created by ambient on 7/29/16.
 */
public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.CommonViewHolder> {

    public interface CommonDataSource {
        int getItemCount();

        RecyclerItemWrapper getItemData(int position);
    }

    public static class MarginDecoration extends RecyclerView.ItemDecoration {
        private int margin;

        public MarginDecoration(Context context) {
            margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
        }

        public boolean needDecoration(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            if (holder instanceof CommonViewHolder) {
                CommonViewHolder parentHolder = (CommonViewHolder) holder;
                if (parentHolder.itemType == RecyclerItemType.RecyclerItemTypeTopItem ||
                        parentHolder.itemType == RecyclerItemType.RecyclerItemTypeItemStore ||
                        parentHolder.itemType == RecyclerItemType.RecyclerItemTypeProductImage) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (needDecoration(view, parent)) {
                GridLayoutManager.LayoutParams layoutParams
                        = (GridLayoutManager.LayoutParams) view.getLayoutParams();

                GridLayoutManager parentLayoutManager = (GridLayoutManager) parent.getLayoutManager();

                int left, top, right, bottom;
                int itemSpanIndex = layoutParams.getSpanIndex();
                int itemSpanSize = layoutParams.getSpanSize();

                /*if (itemSpanIndex == 0) {
                    //Start
                    left = margin;
                    right = margin / 2;
                }else if (itemSpanIndex == 4){
                //} else if (itemSpanIndex + itemSpanSize == parentLayoutManager.getSpanCount()) {
                    //End
                    left = margin / 2;
                    right = margin;

                } else {
                    //Middle
                    left = margin / 2;
                    right = margin / 2;
                }*/
                left = margin / 2;
                right = margin / 2;
                top = margin / 2;
                bottom = margin / 2;

                System.out.println(itemSpanIndex + " " + left + "," + right + "," + top + "," + bottom);

                outRect.set(left, top, right, bottom);

            } else {
                outRect.set(0, 0, 0, 0);
            }
        }
    }

    public static class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        CommonAdapter mAdapter;

        public GridSpanSizeLookup(CommonAdapter adapter) {
            super();
            mAdapter = adapter;
        }

        @Override
        public int getSpanSize(int position) {
            RecyclerItemWrapper itemDataWrapper = mAdapter.getItemData(position);
            return itemDataWrapper.itemSpan;
        }
    }

    public class CommonViewHolder
            extends
            RecyclerView.ViewHolder implements ViewPager.OnPageChangeListener {
        Context mContext;
        OnCommonItemClickListener mClickListener;
        View mRow;
        RecyclerItemType itemType;
        boolean needDecoration;

        //Grid
        LinearLayout itemInfoLayout;
        ImageView itemImage;
        TextView itemTitleLabel;
        TextView itemDescriptionLabel;
        TextView itemMoreDescriptionLabel;

        //Store
        ImageView mapImage;
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

        //News
        NewsTitle newsTitle;
        TextView newsDescription;


        public CommonViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
            super(v);
            this.mRow = v;
            this.mContext = context;
            this.mClickListener = l;
            this.itemType = itemType;
            setUpView();
        }

        public void setUpView() {
            switch (this.itemType) {
                case RecyclerItemTypeTopItem: {
                    this.topItemViewPager = (ViewPager) this.mRow.findViewById(R.id.view_pager);
                    this.pager_indicator = (LinearLayout) mRow.findViewById(R.id.viewPagerCountDots);
                }
                break;

                case RecyclerItemTypeHeader: {
                    headerTitle = (TextView) this.mRow.findViewById(R.id.header_label);
                }
                break;

                case RecyclerItemTypeItemStore: {
                    mapImage = (ImageView) this.mRow.findViewById(R.id.map_image);

                    locationIcon = (ImageView) this.mRow.findViewById(R.id.location_icon);
                    locationLabel = (TextView) this.mRow.findViewById(R.id.location_label);

                    timeIcon = (ImageView) this.mRow.findViewById(R.id.time_icon);
                    timeLabel = (TextView) this.mRow.findViewById(R.id.time_label);

                    phoneIcon = (ImageView) this.mRow.findViewById(R.id.phone_icon);
                    phoneLabel = (TextView) this.mRow.findViewById(R.id.phone_label);
                }
                break;

                case RecyclerItemTypeItemList:
                case RecyclerItemTypeItemGrid:
                case RecyclerItemTypeItemGridImageOnly: {
                    itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                    itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                    itemTitleLabel = (TextView) this.mRow.findViewById(R.id.item_title_label);
                    itemDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_description_label);
                    itemMoreDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_more_description_label);
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


                case RecyclerItemTypeNewsImage: {
                    itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                }
                break;

                case RecyclerItemTypeNewsTitle: {
                    newsTitle = (NewsTitle) this.mRow.findViewById(R.id.product_title);

                }
                break;

                case RecyclerItemTypeNewsDescription: {
                    newsDescription = (TextView) this.mRow.findViewById(R.id.product_description);
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

        int dotsCount;
        ImageView[] dots;
        LinearLayout pager_indicator;

        private void setUiPageViewController() {
            pager_indicator.removeAllViews();
            dotsCount = this.topItemViewPager.getAdapter().getCount();
            dots = new ImageView[dotsCount];
            Drawable selected = null;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                selected = mContext.getResources().getDrawable(R.drawable.selecteditem_dot);
            } else {
                selected = mContext.getResources().getDrawable(R.drawable.selecteditem_dot, null);
            }
            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this.mContext);
                dots[i].setImageDrawable(mContext.getResources().getDrawable(R.drawable.nonselecteditem_dot));

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

        public void configureCell(final int itemPosition, final RecyclerItemWrapper itemDataWrapper) {
            switch (this.itemType) {
                case RecyclerItemTypeTopItem: {
                    ArrayList<TopInfo.Image> topItems = (ArrayList<TopInfo.Image>)
                            itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);


                    FilmstripAdapter adapter = new FilmstripAdapter(mContext, (ArrayList<?>) topItems, new OnCommonItemClickListener() {
                        @Override
                        public void onCommonItemClick(int position, Bundle extraData) {
                            extraData.putInt(RecyclerItemType.class.getName(), itemType.ordinal());
                            //mClickListener.onCommonItemClick(itemPosition, extraData);
                        }
                    });
                    this.topItemViewPager.setAdapter(adapter);
                    this.topItemViewPager.setOnPageChangeListener(this);
                    setUiPageViewController();
                }
                break;

                case RecyclerItemTypeHeader: {
                    headerTitle.setText(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_TITLE));
                }
                break;

                case RecyclerItemTypeItemStore: {
                    TopInfo.Contact contact = (TopInfo.Contact)
                            itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT);
                    Picasso ps = Picasso.with(mContext);
                    String url = "http://maps.google.com/maps/api/staticmap?center=" +
                            contact.latitude + "," +
                            contact.longitude + "&zoom=15&size=500x200&sensor=false";

                    ps.load(url)
                            .resize(640, 360)
                            .centerCrop()
                            .into(mapImage);
                    locationIcon.setImageBitmap(ThemifyIcon.fromThemifyIcon(mContext.getAssets(),
                            "ti-location-pin",
                            60,
                            Color.argb(0, 0, 0, 0),
                            Color.argb(255, 128, 128, 128)
                    ));

                    timeIcon.setImageBitmap(ThemifyIcon.fromThemifyIcon(mContext.getAssets(),
                            "ti-time",
                            60,
                            Color.argb(0, 0, 0, 0),
                            Color.argb(255, 128, 128, 128)
                    ));

                    String time = contact.start_time + " - " + contact.end_time;
                    timeLabel.setText(time);

                    phoneLabel.setText(contact.tel);
                }
                break;

                case RecyclerItemTypeItemList:
                case RecyclerItemTypeItemGrid: {

                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .resize(320, 320)
                            .centerCrop()
                            .into(itemImage);

                    itemInfoLayout.setVisibility(View.VISIBLE);
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
                    if (itemMoreDescriptionLabel != null) {
                        //if (item.moreDescription != null) {
                        //  itemMoreDescriptionLabel.setText(item.moreDescription);
                        //} else {
                        itemMoreDescriptionLabel.setVisibility(View.GONE);
                        //}
                    }
                }
                break;

                case RecyclerItemTypeItemGridImageOnly: {
                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .resize(320, 320)
                            .centerCrop()
                            .into(itemImage);

                    itemInfoLayout.setVisibility(View.GONE);
                }
                break;

                case RecyclerItemTypeProductImage: {
                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .resize(320, 320)
                            .centerCrop()
                            .into(itemImage);
                }
                break;

                case RecyclerItemTypeProductTitle: {
                    productTitle.reloadData(itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT));
                }
                break;

                case RecyclerItemTypeProductDescription: {
                    productDescription.reloadData(itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT));
                }
                break;

                case RecyclerItemTypeNewsImage: {
                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .resize(320, 320)
                            .centerCrop()
                            .into(itemImage);
                }
                break;
                case RecyclerItemTypeNewsTitle: {

                }
                break;
                case RecyclerItemTypeNewsDescription: {

                }
                break;

                case RecyclerItemTypeFooter: {
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
                default:
                    break;
            }
        }
    }


    CommonDataSource dataSource;
    Context mContext;
    LayoutInflater mInflater;
    protected OnCommonItemClickListener mClickListener;

    public CommonAdapter(Context context, CommonDataSource data, OnCommonItemClickListener listener) {
        this.mContext = context;
        this.dataSource = data;
        this.mClickListener = listener;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRow = null;

        RecyclerItemType itemType = RecyclerItemType.fromInt(viewType);
        switch (itemType) {
            case RecyclerItemTypeTopImage: {
                mRow = mInflater.inflate(R.layout.film_strip_layout, parent, false);
            }
            break;
            case RecyclerItemTypeTopItem: {
                mRow = mInflater.inflate(R.layout.film_strip_layout, parent, false);
            }
            break;
            case RecyclerItemTypeHeader: {
                mRow = mInflater.inflate(R.layout.common_item_header, parent, false);
            }
            break;

            case RecyclerItemTypeItemList: {
                mRow = mInflater.inflate(R.layout.common_item_list, parent, false);
            }
            break;

            case RecyclerItemTypeItemStore: {
                mRow = mInflater.inflate(R.layout.common_item_map, parent, false);
            }
            break;

            case RecyclerItemTypeItemGrid:
            case RecyclerItemTypeItemGridImageOnly: {
                mRow = mInflater.inflate(R.layout.common_item_grid, parent, false);
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

            case RecyclerItemTypeNewsImage: {
                mRow = mInflater.inflate(R.layout.news_item_image, parent, false);
            }
            break;
            case RecyclerItemTypeNewsTitle: {
                mRow = mInflater.inflate(R.layout.product_item_title, parent, false);
            }
            break;
            case RecyclerItemTypeNewsDescription: {
                mRow = mInflater.inflate(R.layout.news_item_description, parent, false);
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
        final RecyclerItemWrapper itemData = (RecyclerItemWrapper) this.dataSource.getItemData(position);

        final int itemPosition = position;

        holder.configureCell(itemPosition, itemData);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                System.out.println("onLongClick at " + itemPosition);
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

    public RecyclerItemWrapper getItemData(int position) {
        return (RecyclerItemWrapper) this.dataSource.getItemData(position);
    }

    @Override
    public int getItemCount() {
        if (this.dataSource != null) {
            return dataSource.getItemCount();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        RecyclerItemWrapper itemData = getItemData(position);
        return itemData.itemType.ordinal();
    }
}