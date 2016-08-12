package jp.tenposs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
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

import java.util.List;

import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.ThemifyIcon;

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
                        parentHolder.itemType == RecyclerItemType.RecyclerItemTypeItemStore) {
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

                int itemPosition = parent.getChildLayoutPosition(view);
                if (itemSpanIndex == 0) {
                    left = margin;
                    right = margin / 2;

                } else if (itemSpanIndex + itemSpanSize == parentLayoutManager.getSpanCount()) {
                    left = margin / 2;
                    right = margin;

                } else {
                    left = margin / 2;
                    right = margin / 2;
                }

                top = margin / 2;
                bottom = margin / 2;

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
            RecyclerView.ViewHolder {
        Context mContext;
        OnCommonItemClickListener mClickListener;
        View mRow;
        RecyclerItemType itemType;

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

                case RecyclerItemTypeFooter: {
                    footerButton = (Button) this.mRow.findViewById(R.id.footer_button);
                }
                break;
                default:
                    Assert.assertFalse("Should never be here!", false);
                    break;
            }
        }

        public void configureCell(final int itemPosition, RecyclerItemWrapper itemDataWrapper) {
            switch (this.itemType) {
                case RecyclerItemTypeTopItem: {
                    List<TopInfo.Response.ResponseData.Image> topItems = (List<TopInfo.Response.ResponseData.Image>) itemDataWrapper.itemData;
                    FilmstripAdapter adapter = new FilmstripAdapter(mContext, topItems, new OnCommonItemClickListener() {
                        @Override
                        public void onCommonItemClick(int position, Bundle extraData) {
                            extraData.putInt(RecyclerItemType.class.getName(), itemType.ordinal());
                            mClickListener.onCommonItemClick(itemPosition, extraData);
                        }
                    });
                    this.topItemViewPager.setAdapter(adapter);
                }
                break;

                case RecyclerItemTypeHeader: {
                    RecyclerItemWrapper.RecyclerItemObject object = (RecyclerItemWrapper.RecyclerItemObject) itemDataWrapper.itemData;
                    headerTitle.setText(object.title);
                }
                break;

                case RecyclerItemTypeItemStore: {
                    //AppInfo.Response.ResponseData.Info storeInfo = (AppInfo.Response.ResponseData.Info) itemDataWrapper.itemData;
                    Picasso ps = Picasso.with(mContext);
                    String lat = "35.6585848";//storeInfo.latitude
                    String lng = "139.7432496";//storeInfo.longitude
                    String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=500x200&sensor=false";

                    ps.load(url)
                            .resize(640, 360)
                            .centerInside()
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

                    //String time = storeInfo.start_time + " - " + storeInfo.end_time;
                    //timeLabel.setText(time);

                    //phoneLabel.setText(storeInfo.tel);
                }
                break;

                case RecyclerItemTypeItemList:
                case RecyclerItemTypeItemGrid: {
                    RecyclerItemWrapper.RecyclerItemObject item = (RecyclerItemWrapper.RecyclerItemObject) itemDataWrapper.itemData;
                    Picasso ps = Picasso.with(mContext);
                    //ps.load(item.image)
                    ps.load("http://media.foody.vn/images/blogs/s320x320/1(74).jpg")
                            .resize(320, 320)
                            .centerInside()
                            .into(itemImage);

                    itemInfoLayout.setVisibility(View.VISIBLE);
                    if (itemTitleLabel != null) {
                        if (item.title != null) {
                            itemTitleLabel.setText(item.title);
                        } else {
                            itemTitleLabel.setVisibility(View.GONE);
                        }
                    }
                    if (itemDescriptionLabel != null) {
                        if (item.description != null) {
                            itemDescriptionLabel.setText(item.description);
                        } else {
                            itemDescriptionLabel.setVisibility(View.GONE);
                        }
                    }
                    if (itemMoreDescriptionLabel != null) {
                        if (item.moreDescription != null) {
                            itemMoreDescriptionLabel.setText(item.moreDescription);
                        } else {
                            itemMoreDescriptionLabel.setVisibility(View.GONE);
                        }
                    }
                }
                break;

                case RecyclerItemTypeItemGridImageOnly: {
                    RecyclerItemWrapper.RecyclerItemObject item = (RecyclerItemWrapper.RecyclerItemObject) itemDataWrapper.itemData;
                    Picasso ps = Picasso.with(mContext);
                    //ps.load(item.image)
                    ps.load("http://media.foody.vn/images/blogs/s320x320/1(74).jpg")
                            .resize(320, 320)
                            .centerInside()
                            .into(itemImage);

                    itemInfoLayout.setVisibility(View.GONE);
                }
                break;

                case RecyclerItemTypeFooter: {
                    RecyclerItemWrapper.RecyclerItemObject object = (RecyclerItemWrapper.RecyclerItemObject) itemDataWrapper.itemData;
                    footerButton.setText(object.title);
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
            case RecyclerItemTypeTopItem: {
                mRow = mInflater.inflate(R.layout.film_strip_layout, parent, false);

            }
            break;
            case RecyclerItemTypeHeader: {
                mRow = mInflater.inflate(R.layout.home_item_header, parent, false);
            }
            break;

            case RecyclerItemTypeItemList: {
                mRow = mInflater.inflate(R.layout.home_item_list, parent, false);
            }
            break;

            case RecyclerItemTypeItemStore: {
                mRow = mInflater.inflate(R.layout.home_item_map, parent, false);
            }
            break;

            case RecyclerItemTypeItemGrid:
            case RecyclerItemTypeItemGridImageOnly: {
                mRow = mInflater.inflate(R.layout.home_item_grid, parent, false);
            }
            break;

            case RecyclerItemTypeFooter: {
                mRow = mInflater.inflate(R.layout.home_item_footer, parent, false);
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