package jp.tenposs.adapter;

/**
 * Created by ambient on 7/29/16.
 */
public enum RecyclerItemType {
    RecyclerItemTypeNone,
    RecyclerItemTypeTopImage,
    RecyclerItemTypeTopItem,

    RecyclerItemTypeHeader,
    RecyclerItemTypeItemList,
    RecyclerItemTypeItemStore,
    RecyclerItemTypeItemGrid,
    RecyclerItemTypeItemGridImageOnly,

    ///Product
    RecyclerItemTypeProductImage,
    RecyclerItemTypeProductTitle,
    RecyclerItemTypeProductDescription,

    RecyclerItemTypeFooter;


    public static RecyclerItemType fromInt(int recyclerItemType) {
        for (RecyclerItemType type : RecyclerItemType.values()) {
            if (type.ordinal() == recyclerItemType) {
                return type;
            }
        }
        return RecyclerItemTypeNone;

    }
}
