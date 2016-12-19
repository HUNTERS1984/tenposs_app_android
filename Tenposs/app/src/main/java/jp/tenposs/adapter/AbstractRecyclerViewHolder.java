package jp.tenposs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 11/9/16.
 */

public abstract class AbstractRecyclerViewHolder extends RecyclerView.ViewHolder {
    protected RecyclerItemType itemType;
    protected Context mContext;
    protected OnCommonItemClickListener mClickListener;
    protected View mRow;

    protected int thumbImageSize;
    protected int fullImageSize;

    public AbstractRecyclerViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
        super(v);
        this.mRow = v;
        this.mContext = context;
        this.mClickListener = l;
        this.itemType = itemType;
        this.thumbImageSize = mContext.getResources().getInteger(R.integer.thumb_image_size);
        this.fullImageSize = mContext.getResources().getInteger(R.integer.full_image_size);
        setUpView();
    }

    public abstract void setUpView();

    public abstract void configureCell(final int itemPosition, final RecyclerItemWrapper itemDataWrapper);
}
