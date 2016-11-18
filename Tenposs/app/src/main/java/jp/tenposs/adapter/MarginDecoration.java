package jp.tenposs.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ambient on 11/9/16.
 */

public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    public static final int DECORATION_NONE = 0;
    public static final int DECORATION_ALL = 1;
    public static final int DECORATION_ALL_BUT_TOP = 2;
    public static final int DECORATION_ALL_MULTI_COLUMN = 3;
    public static final int DECORATION_TOP_BOTTOM = 4;
    public static final int DECORATION_LEFT_RIGHT = 5;
    public static final int DECORATION_LEFT = 6;
    public static final int DECORATION_RIGHT = 7;
    public static final int DECORATION_TOP = 8;
    public static final int DECORATION_BOTTOM = 9;


    public MarginDecoration(Context context, int marginId) {
        margin = context.getResources().getDimensionPixelSize(marginId);
    }

    //0: ko can
    //1: top
    //2: all
    public int needDecoration(View view, RecyclerView parent) {
        RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
        if (holder instanceof AbstractRecyclerViewHolder) {
            AbstractRecyclerViewHolder parentHolder = (AbstractRecyclerViewHolder) holder;

            switch (parentHolder.itemType) {

                case RecyclerItemTypeStore:
                case RecyclerItemTypeNewsTop:
                case RecyclerItemTypeRecyclerHorizontal:
                case RecyclerItemTypeRecyclerVertical:
                case RecyclerItemTypeProductDetail:
                case RecyclerItemTypeProductInfo:
                    return DECORATION_BOTTOM;

                case RecyclerItemTypeGrid:
                    return DECORATION_ALL_MULTI_COLUMN;

                case RecyclerItemTypeList:
                    return DECORATION_ALL;

                case RecyclerItemTypeListDivider:
                    return DECORATION_LEFT_RIGHT;

                default:
                    return DECORATION_NONE;
            }
        }
        return DECORATION_NONE;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int needDeco = needDecoration(view, parent);
        int itemPosition = parent.getChildLayoutPosition(view);
        switch (needDeco) {
            case DECORATION_ALL: {
                outRect.top = (int) (0.5 * margin);
                outRect.left = (int) (0.5 * margin);
                outRect.right = (int) (0.5 * margin);
                outRect.bottom = (int) (0.5 * margin);
            }
            break;

            case DECORATION_ALL_BUT_TOP: {
                if (itemPosition == 0) {
                    outRect.top = (int) (0.5 * margin);
                } else {
                    outRect.top = 0;
                }
                outRect.left = (int) (0.5 * margin);
                outRect.right = (int) (0.5 * margin);
                outRect.bottom = (int) (0.5 * margin);
            }
            break;

            case DECORATION_ALL_MULTI_COLUMN: {
                GridLayoutManager.LayoutParams layoutParams
                        = (GridLayoutManager.LayoutParams) view.getLayoutParams();

                GridLayoutManager parentLayoutManager = (GridLayoutManager) parent.getLayoutManager();

                int itemSpanIndex = layoutParams.getSpanIndex();
                int itemSpanSize = layoutParams.getSpanSize();
                int spanCount = parentLayoutManager.getSpanCount();
                int column = (itemSpanIndex + itemSpanSize) / itemSpanSize;
                int columnCount = spanCount / itemSpanSize;
                if (column == 1 && columnCount == 1) {
                    outRect.left = (int) (0.50 * margin);
                    outRect.right = (int) (0.50 * margin);
                    outRect.top = (int) (0.5 * margin);
                    outRect.bottom = (int) (0.5 * margin);
                } else if (column == 1) {
                    outRect.left = (int) (0.50 * margin);
                    outRect.right = (int) (0.25 * margin);
                } else if (column == columnCount) {
                    outRect.left = (int) (0.25 * margin);
                    outRect.right = (int) (0.50 * margin);
                } else {
                    outRect.left = (int) (0.375 * margin);
                    outRect.right = (int) (0.375 * margin);
                }
                AbstractRecyclerAdapter adapter = (AbstractRecyclerAdapter) parent.getAdapter();
                if (adapter != null) {
                    RecyclerItemWrapper item = adapter.getItemData(itemPosition);
                    if (item.itemData.getInt(RecyclerItemWrapper.ITEM_ROW, -1) == 0) {
                        outRect.top = (int) (0.5 * margin);
                    } else {
                        outRect.top = 0;//(int) (0.25 * margin);
                    }
                } else {
                    outRect.top = 0;//(int) (0.25 * margin);
                }
                outRect.bottom = (int) (0.5 * margin);
            }
            break;

            case DECORATION_TOP_BOTTOM: {
                outRect.top = (int) (0.5 * margin);
                outRect.bottom = (int) (0.5 * margin);
                outRect.left = 0;
                outRect.right = 0;
            }
            break;

            case DECORATION_LEFT_RIGHT: {
                outRect.left = (int) (0.5 * margin);
                outRect.right = (int) (0.5 * margin);
                outRect.top = 0;
                outRect.bottom = 0;
            }
            break;

            case DECORATION_LEFT: {
                outRect.left = (int) (0.5 * margin);
                outRect.right = 0;
                outRect.top = 0;
                outRect.bottom = 0;
            }
            break;

            case DECORATION_RIGHT: {
                outRect.left = 0;
                outRect.right = (int) (0.5 * margin);
                outRect.top = 0;
                outRect.bottom = 0;
            }
            break;

            case DECORATION_TOP: {
                outRect.top = (int) (0.5 * margin);
                outRect.bottom = 0;
                outRect.left = 0;
                outRect.right = 0;
            }
            break;

            case DECORATION_BOTTOM: {
                outRect.top = 0;
                outRect.bottom = (int) (0.5 * margin);
                outRect.left = 0;
                outRect.right = 0;
            }
            break;
            default: {
                outRect.set(0, 0, 0, 0);
            }
            break;
        }
    }
}