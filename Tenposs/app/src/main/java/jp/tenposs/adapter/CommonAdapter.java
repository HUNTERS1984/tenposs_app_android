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
import android.widget.LinearLayout;

import java.util.ArrayList;


import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 7/29/16.
 */
public class CommonAdapter extends RecyclerView.Adapter<CommonViewHolder> {

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

            case RecyclerItemTypeItemMap: {
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