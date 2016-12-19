package jp.tenposs.adapter;

/**
 * Created by ambient on 7/29/16.
 */
public enum RecyclerItemType {
    RecyclerItemTypeStart,

    RecyclerItemTypeHeader,
    RecyclerItemTypeFooter,

    RecyclerItemTypeEnd;


    public static RecyclerItemType fromInt(int recyclerItemType) {
        for (RecyclerItemType type : RecyclerItemType.values()) {
            if (type.ordinal() == recyclerItemType) {
                return type;
            }
        }
        return RecyclerItemTypeStart;
    }
}
