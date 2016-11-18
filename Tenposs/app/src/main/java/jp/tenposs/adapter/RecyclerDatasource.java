package jp.tenposs.adapter;

/**
 * Created by ambient on 11/1/16.
 */

public interface RecyclerDataSource {
    int getItemCount();

    RecyclerItemWrapper getItemData(int position);
}
