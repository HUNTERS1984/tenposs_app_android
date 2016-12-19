package jp.tenposs.adapter;

import android.os.Bundle;

/**
 * Created by ambient on 8/2/16.
 */
public class RecyclerItemWrapper {
    public final static String ITEM_ID = "ITEM_ID";
    public final static String ITEM_INDEX = "ITEM_INDEX";
    public final static String ITEM_TYPE = "ITEM_TYPE";

    public final static String ITEM_CATEGORY = "ITEM_CATEGORY";
    public final static String ITEM_TITLE = "ITEM_TITLE";

    public final static String ITEM_BACKGROUND = "ITEM_BACKGROUND";
    public final static String ITEM_TEXT_COLOR = "ITEM_TEXT_COLOR";
    public final static String ITEM_PRICE = "ITEM_PRICE";
    public final static String ITEM_DESCRIPTION = "ITEM_DESCRIPTION";
    public final static String ITEM_IMAGE = "ITEM_IMAGE";
    public final static String ITEM_SCREEN_ID = "ITEM_SCREEN_ID";
    public final static String ITEM_OBJECT = "ITEM_OBJECT";
    public final static String ITEM_CLASS = "ITEM_CLASS";
    public final static String ITEM_ROW = "ITEM_ROW";
    public final static String ITEM_ROW_SPAN = "ITEM_ROW_SPAN";
    public final static String ITEM_COLUMN_SPAN = "ITEM_COLUMN_SPAN";
    public final static String ITEM_LAYOUT_SPAN_ABLE = "ITEM_LAYOUT_SPAN_ABLE";


    public RecyclerItemType itemType;
    public int itemSpan;
    public Bundle itemData;

    public RecyclerItemWrapper(RecyclerItemType itemType,
                               int itemSpan,
                               Bundle itemData) {
        this.itemType = itemType;
        this.itemSpan = itemSpan;
        this.itemData = itemData;
    }
}