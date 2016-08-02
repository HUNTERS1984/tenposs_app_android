package jp.tenposs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.tenposs.datamodel.ProductObject;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.ThemifyIcon;

/**
 * Created by ambient on 7/29/16.
 */
public class CommonViewHolder
        extends
        RecyclerView.ViewHolder {
    Context mContext;
    OnCommonItemClickListener mClickListener;
    View mRow;
    RecyclerItemType recyclerItemType;

    //General
    ImageView itemImage;
    LinearLayout itemInfoLayout;
    TextView itemInfoLabel;
    TextView itemPriceLabel;

    //Map
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
            case RecyclerItemTypeItemList: {
                itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                itemInfoLabel = (TextView) this.mRow.findViewById(R.id.item_info_label);
                itemPriceLabel = (TextView) this.mRow.findViewById(R.id.item_price_label);
            }
            break;

            case RecyclerItemTypeItemMap: {
                mapImage = (ImageView) this.mRow.findViewById(R.id.map_image);

                locationIcon = (ImageView) this.mRow.findViewById(R.id.location_icon);
                locationLabel = (TextView) this.mRow.findViewById(R.id.location_label);

                timeIcon = (ImageView) this.mRow.findViewById(R.id.time_icon);
                timeLabel = (TextView) this.mRow.findViewById(R.id.time_label);

                phoneIcon = (ImageView) this.mRow.findViewById(R.id.phone_icon);
                phoneLabel = (TextView) this.mRow.findViewById(R.id.phone_label);
            }
            break;

            case RecyclerItemTypeItemGrid: {
                itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                itemInfoLabel = (TextView) this.mRow.findViewById(R.id.item_info_label);
                itemPriceLabel = (TextView) this.mRow.findViewById(R.id.item_price_label);
                itemInfoLayout.setVisibility(View.VISIBLE);
            }
            break;

            case RecyclerItemTypeItemGridImageOnly: {
                itemImage = (ImageView) this.mRow.findViewById(R.id.item_image);
                itemInfoLayout = (LinearLayout) this.mRow.findViewById(R.id.item_info_layout);
                itemInfoLayout.setVisibility(View.GONE);
            }
            break;
            case RecyclerItemTypeFooter: {
                footerButton = (Button) this.mRow.findViewById(R.id.footer_button);
            }
            break;
            default:
                break;
        }
    }

    public void configureCell(final int itemPosition, RecyclerItemWrapper itemDataWrapper, boolean isLandscape, boolean isShowGrid) {
        if (this.recyclerItemType == RecyclerItemType.RecyclerItemTypeTopItem) {
            List<ProductObject> topItems = (List<ProductObject>) itemDataWrapper.itemData;
            FilmstripAdapter adapter = new FilmstripAdapter(mContext, topItems, new OnCommonItemClickListener() {
                @Override
                public void onCommonItemClick(int position, Bundle extraData) {
                    extraData.putInt(RecyclerItemType.class.getName(), recyclerItemType.ordinal());
                    mClickListener.onCommonItemClick(itemPosition, extraData);
                }
            });
            this.topItemViewPager.setAdapter(adapter);
        } else if (this.recyclerItemType == RecyclerItemType.RecyclerItemTypeHeader) {
            headerTitle.setText((String) itemDataWrapper.itemData);

        } else if (this.recyclerItemType == RecyclerItemType.RecyclerItemTypeItemList) {
        } else if (this.recyclerItemType == RecyclerItemType.RecyclerItemTypeItemMap) {
            Picasso ps = Picasso.with(mContext);
            String latEiffelTower = "48.858235";
            String lngEiffelTower = "2.294571";
            String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=15&size=500x200&sensor=false";

            ps.load(url)
                    .resize(640, 360)
                    .centerInside()
                    .into(mapImage);

//            ti-location-pin
//            ti-time

//            ImageView locationIcon;
//            TextView locationLabel;
//
//            ImageView timeIcon;
//            TextView timeLabel;
//
//            ImageView phoneIcon;


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
        } else if (this.recyclerItemType == RecyclerItemType.RecyclerItemTypeItemGrid) {
        } else if (this.recyclerItemType == RecyclerItemType.RecyclerItemTypeItemGridImageOnly) {
        } else if (this.recyclerItemType == RecyclerItemType.RecyclerItemTypeFooter) {
            footerButton.setText((String) itemDataWrapper.itemData);
        }
    }
}

