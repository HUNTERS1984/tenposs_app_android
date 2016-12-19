package jp.tenposs.adapter;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by ambient on 11/9/16.
 */

public class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    AbstractRecyclerAdapter mAdapter;

    public GridSpanSizeLookup(AbstractRecyclerAdapter adapter) {
        super();
        mAdapter = adapter;
    }

    @Override
    public int getSpanSize(int position) {
        RecyclerItemWrapper itemDataWrapper = mAdapter.getItemData(position);
        return itemDataWrapper.itemSpan;
    }
}