package jp.tenposs.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.tenposs.listener.OnCommonItemClickListener;

/**
 * Created by ambient on 10/17/16.
 */
public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.CommonViewHolder> {

    public interface CommonDataSource {
        int getItemCount();

        RecyclerItemWrapper getItemData(int position);
    }

//    public static class MarginDecoration extends RecyclerView.ItemDecoration {
//        private int margin;
//
//        public MarginDecoration(Context context) {
//            margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
//        }
//
//        //0: ko can
//        //1: top
//        //2: all
//        public int needDecoration(View view, RecyclerView parent) {
//            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
//            if (holder instanceof CommonViewHolder) {
//                CommonViewHolder parentHolder = (CommonViewHolder) holder;
//
//
//                if (parentHolder.itemType == RecyclerItemType.RecyclerItemTypeTopItem) {
//                    return 0;
//                } else if (parentHolder.itemType == RecyclerItemType.RecyclerItemTypeItemStore ||
//                        parentHolder.itemType == RecyclerItemType.RecyclerItemTypeProductImage) {
//                    return 1;
//                } else {
//                    return 2;
//                }
//            }
//            return 0;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            if (needDecoration(view, parent) == 2) {
//                GridLayoutManager.LayoutParams layoutParams
//                        = (GridLayoutManager.LayoutParams) view.getLayoutParams();
//
//                GridLayoutManager parentLayoutManager = (GridLayoutManager) parent.getLayoutManager();
//
//                int left, top, right, bottom;
//                int itemSpanIndex = layoutParams.getSpanIndex();
//                int itemSpanSize = layoutParams.getSpanSize();
//
//                /*if (itemSpanIndex == 0) {
//                    //Start
//                    left = margin;
//                    right = margin / 2;
//                }else if (itemSpanIndex == 4){
//                //} else if (itemSpanIndex + itemSpanSize == parentLayoutManager.getSpanCount()) {
//                    //End
//                    left = margin / 2;
//                    right = margin;
//
//                } else {
//                    //Middle
//                    left = margin / 2;
//                    right = margin / 2;
//                }*/
//                left = margin / 2;
//                right = margin / 2;
//                top = margin / 2;
//                bottom = margin / 2;
//
//                outRect.set(left, top, right, bottom);
//            } else if (needDecoration(view, parent) == 1) {
//                outRect.set(0, margin, 0, margin);
//            } else {
//                outRect.set(0, 0, 0, 0);
//            }
//        }
//    }

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
        boolean needDecoration;

        public CommonViewHolder(View v, RecyclerItemType itemType, Context context, OnCommonItemClickListener l) {
            super(v);
            this.mRow = v;
            this.mContext = context;
            this.mClickListener = l;
            this.itemType = itemType;
            setUpView();
        }

        public void setUpView() {
        }


        public void configureCell(final int itemPosition, final RecyclerItemWrapper itemDataWrapper) {
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