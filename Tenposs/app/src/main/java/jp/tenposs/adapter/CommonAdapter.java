package jp.tenposs.adapter;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.ProgressBar;
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

        Object getItemData(int position);
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
        RecyclerItemType recyclerItemType;

        //Grid
        LinearLayout itemInfoLayout;
        ImageView itemImage;
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

        ImageView headerIcon;
        TextView headerTitle;

        //Footer
        Button footerButton;
        TextView footerShowAll;
        ImageView footerLoadMore;
        ProgressBar footerLoading;

        public CommonViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
            super(v);
            this.mRow = v;
            this.mContext = context;
            this.mClickListener = l;
            this.recyclerItemType = itemType;
            setUpView(itemType);
        }

        public void setUpView(RecyclerItemType itemType) {
            switch (itemType) {
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
                case RecyclerItemTypeItemGridImageOnly:{
                    itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                    itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                    itemTitleLabel = (TextView) this.mRow.findViewById(R.id.item_title_label);
                    itemDescriptionLabel = (TextView) this.mRow.findViewById(R.id.item_description_label);
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

        public void configureCell(final int itemPosition, RecyclerItemWrapper itemDataWrapper, boolean isLandscape, boolean isShowGrid) {
            switch (this.recyclerItemType) {
                case RecyclerItemTypeTopItem: {
                    List<TopInfo.Response.ResponseData.Image> topItems = (List<TopInfo.Response.ResponseData.Image>) itemDataWrapper.itemData;
                    FilmstripAdapter adapter = new FilmstripAdapter(mContext, topItems, new OnCommonItemClickListener() {
                        @Override
                        public void onCommonItemClick(int position, Bundle extraData) {
                            extraData.putInt(RecyclerItemType.class.getName(), recyclerItemType.ordinal());
                            mClickListener.onCommonItemClick(itemPosition, extraData);
                        }
                    });
                    this.topItemViewPager.setAdapter(adapter);
                }
                break;

                case RecyclerItemTypeHeader: {
                    headerTitle.setText((String) itemDataWrapper.itemData);
                }
                break;

                case RecyclerItemTypeItemStore: {
                    Picasso ps = Picasso.with(mContext);
                    String latEiffelTower = "48.858235";
                    String lngEiffelTower = "2.294571";
                    String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=15&size=500x200&sensor=false";

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
                }
                break;

                case RecyclerItemTypeItemList:
                case RecyclerItemTypeItemGrid: {
                    RecyclerItemWrapper.RecyclerItemObject item = (RecyclerItemWrapper.RecyclerItemObject) itemDataWrapper.itemData;
                    Picasso ps = Picasso.with(mContext);
                    ps.load(item.image)
                            .resize(640, 360)
                            .centerInside()
                            .into(itemImage);

                    itemInfoLayout.setVisibility(View.VISIBLE);
                    itemTitleLabel.setText(item.title);
                    itemDescriptionLabel.setText(item.description);
                }
                break;

                case RecyclerItemTypeItemGridImageOnly: {
                    RecyclerItemWrapper.RecyclerItemObject item = (RecyclerItemWrapper.RecyclerItemObject) itemDataWrapper.itemData;
                    Picasso ps = Picasso.with(mContext);
                    ps.load(item.image)
                            .resize(640, 360)
                            .centerInside()
                            .into(itemImage);

                    itemInfoLayout.setVisibility(View.GONE);
                }
                break;

                case RecyclerItemTypeFooter: {
                    footerButton.setText((String) itemDataWrapper.itemData);
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

        holder.configureCell(itemPosition, itemData, false, false);

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