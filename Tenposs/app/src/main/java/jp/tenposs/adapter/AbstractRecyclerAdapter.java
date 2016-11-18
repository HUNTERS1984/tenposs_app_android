package jp.tenposs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import jp.tenposs.listener.OnCommonItemClickListener;

/**
 * Created by ambient on 11/9/16.
 */

public abstract class AbstractRecyclerAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected OnCommonItemClickListener mClickListener;
    private RecyclerDataSource mDataSource;
    protected LayoutInflater mInflater;

    public AbstractRecyclerAdapter(Context context, RecyclerDataSource data, OnCommonItemClickListener listener) {
        this.mContext = context;
        this.mDataSource = data;
        this.mClickListener = listener;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public RecyclerItemWrapper getItemData(int position) {
        return this.mDataSource.getItemData(position);
    }

    @Override
    public int getItemCount() {
        if (this.mDataSource != null) {
            return mDataSource.getItemCount();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        RecyclerItemWrapper itemData = getItemData(position);
        if (itemData != null) {
            return itemData.itemType.ordinal();
        } else {
            return RecyclerItemType.RecyclerItemTypeNone.ordinal();
        }
    }
}
