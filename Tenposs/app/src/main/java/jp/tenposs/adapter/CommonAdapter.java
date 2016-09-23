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
import android.text.TextPaint;
import android.text.style.ClickableSpan;
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
import jp.tenposs.utils.FlatIcon;
import jp.tenposs.utils.Utils;
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

        //0: ko can
        //1: top
        //2: all
        public int needDecoration(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            if (holder instanceof CommonViewHolder) {
                CommonViewHolder parentHolder = (CommonViewHolder) holder;


                if (parentHolder.itemType == RecyclerItemType.RecyclerItemTypeTopItem) {
                    return 0;
                } else if (parentHolder.itemType == RecyclerItemType.RecyclerItemTypeItemStore ||
                        parentHolder.itemType == RecyclerItemType.RecyclerItemTypeProductImage) {
                    return 1;
                } else {
                    return 2;
                }
            }
            return 0;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (needDecoration(view, parent) == 2) {
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

                outRect.set(left, top, right, bottom);
            } else if (needDecoration(view, parent) == 1) {
                outRect.set(0, margin, 0, margin);
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

        TextView itemCategoryLabel;
        TextView itemTitleLabel;
        TextView itemDescriptionLabel;

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
        TextView newsDescription;

        int thumbImageSize;
        int fullImageSize;

        public CommonViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
            super(v);
            this.mRow = v;
            this.mContext = context;
            this.mClickListener = l;
            this.itemType = itemType;
            this.thumbImageSize = mContext.getResources().getInteger(R.integer.thumb_image_size);
            this.fullImageSize = mContext.getResources().getInteger(R.integer.full_image_size);
            setUpView();
        }

        public void setUpView() {
            switch (this.itemType) {
                case RecyclerItemTypeTopItem: {
                    this.topItemViewPager = (ViewPager) this.mRow.findViewById(R.id.view_pager);
                    this.pager_indicator = (LinearLayout) mRow.findViewById(R.id.view_pager_dots_layout);
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
                    itemCategoryLabel = (TextView) this.mRow.findViewById(R.id.item_category_label);
                    itemTitleLabel = (TextView) this.mRow.findViewById(R.id.item_title_label);
                    itemDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_description_label);
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

                case RecyclerItemTypeItemStore: {
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
                            .resize(fullImageSize, fullImageSize)
                            .centerInside()
                            .placeholder(R.drawable.drop)
                            .into(mapImage);
                    locationIcon.setImageBitmap(FlatIcon.fromFlatIcon(mContext.getAssets(),
                            "flaticon-placeholder",
                            40,
                            Color.argb(0, 0, 0, 0),
                            Color.argb(255, 128, 128, 128)
                    ));

                    timeIcon.setImageBitmap(FlatIcon.fromFlatIcon(mContext.getAssets(),
                            "flaticon-clock",
                            40,
                            Color.argb(0, 0, 0, 0),
                            Color.argb(255, 128, 128, 128)
                    ));

                    locationLabel.setText(contact.title);

                    String startTime = Utils.timeStringFromDate(Utils.dateFromString(contact.start_time));
                    String endTime = Utils.timeStringFromDate(Utils.dateFromString(contact.end_time));
                    String time = startTime + " - " + endTime;
                    timeLabel.setText(time);

                    phoneIcon.setImageBitmap(FlatIcon.fromFlatIcon(mContext.getAssets(),
                            "flaticon-phone",
                            40,
                            Color.argb(0, 0, 0, 0),
                            Color.argb(255, 128, 128, 128)
                    ));
                    Utils.setTextViewHTML(phoneLabel, "<a href='about:blank'><u>" + contact.tel + "</u></a>",
                            new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    //mActivityListener.showScreen(AbstractFragment.SIGNUP_SCREEN, null);
                                    mClickListener.onCommonItemClick(itemPosition, itemDataWrapper.itemData);
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    ds.setColor(Utils.getColorInt(mContext, R.color.phone_text_color));
                                    ds.setUnderlineText(true);
                                }
                            });
                }
                break;

                case RecyclerItemTypeItemList:
                case RecyclerItemTypeItemGrid: {

                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .resize(thumbImageSize, thumbImageSize)
                            .placeholder(R.drawable.drop)
                            .centerCrop()
                            .into(itemImage);

                    itemInfoLayout.setVisibility(View.VISIBLE);
                    if (itemCategoryLabel != null) {
                        String itemTitle = itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_CATEGORY);
                        if (itemTitle != null) {
                            itemCategoryLabel.setText(itemTitle);
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
                }
                break;

                case RecyclerItemTypeItemGridImageOnly: {
                    Picasso ps = Picasso.with(mContext);
                    ps.load(itemDataWrapper.itemData.getString(RecyclerItemWrapper.ITEM_IMAGE))
                            .resize(thumbImageSize, thumbImageSize)
                            .placeholder(R.drawable.drop)
                            .centerCrop()
                            .into(itemImage);

                    itemInfoLayout.setVisibility(View.GONE);
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
                    productTitle.reloadData(itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT));
                }
                break;

                case RecyclerItemTypeProductDescription: {
                    productDescription.reloadData(itemDataWrapper.itemData.getSerializable(RecyclerItemWrapper.ITEM_OBJECT));
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