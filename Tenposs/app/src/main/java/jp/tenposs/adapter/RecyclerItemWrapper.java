package jp.tenposs.adapter;

/**
 * Created by ambient on 8/2/16.
 */
public class RecyclerItemWrapper {
    public RecyclerItemType itemType;
    public int itemSpan;
    public Object itemData;

    public RecyclerItemWrapper(RecyclerItemType itemType,
                               int itemSpan,
                               Object itemData) {
        this.itemType = itemType;
        this.itemSpan = itemSpan;
        this.itemData = itemData;
    }
}