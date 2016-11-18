package jp.tenposs.adapter;

/**
 * Created by ambient on 7/29/16.
 */
public enum RecyclerItemType {
    RecyclerItemTypeNone,

    //DECORATION_NONE
    RecyclerItemTypeTop,

    RecyclerItemTypeHeader,
    RecyclerItemTypeFooter,

    RecyclerItemTypeGrid,


    RecyclerItemTypeList,
    RecyclerItemTypeListDivider,

    RecyclerItemTypeStore,

    ///Product
    RecyclerItemTypeProductImage,
    RecyclerItemTypeProductTitle,
    RecyclerItemTypeProductDescription,

    //Restaurant Template
    RecyclerItemTypeProductInfo,
    RecyclerItemTypeProductDetail,

    RecyclerItemTypeGridImage,

    //DECORATION_MULTI_COLUMN

    RecyclerItemTypeNewsTop,

    RecyclerItemTypeRecyclerHorizontal,
    RecyclerItemTypeRecyclerVertical,;


    public static RecyclerItemType fromInt(int recyclerItemType) {
        for (RecyclerItemType type : RecyclerItemType.values()) {
            if (type.ordinal() == recyclerItemType) {
                return type;
            }
        }
        return RecyclerItemTypeNone;

    }
}
