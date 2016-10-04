package jp.tenposs.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ambient on 10/4/16.
 */

public interface TableDataSource {

    public final static int IGNORE_ITEM_VIEW_TYPE = -1;

    void registerDataSetObserver(DataSetObserver observer);

    void unregisterDataSetObserver(DataSetObserver observer);

    public int getRowCount();

    public int getColumnCount();

    public View getView(int row, int column, View convertView, ViewGroup parent);

    public int getWidth(int column);

    public int getHeight(int row);

    public int getItemViewType(int row, int column);

    public int getViewTypeCount();

}