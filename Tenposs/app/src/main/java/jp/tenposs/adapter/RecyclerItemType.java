package jp.tenposs.adapter;

/**
 * Created by ambient on 7/29/16.
 */
public enum RecyclerItemType {
    RecyclerItemTypeStart,

    //DECORATION_NONE
    RecyclerItemTypeTop,

    RecyclerItemTypeHeader,
    RecyclerItemTypeFooter,

    RecyclerItemTypeGridImage,
    RecyclerItemTypeGridItem,
    RecyclerItemTypeGridStaff,

    RecyclerItemTypeList,
    RecyclerItemTypeListDivider,

    RecyclerItemTypeStore,

    ///Product
    RecyclerItemTypeProductImage,
    RecyclerItemTypeProductTitle,
    RecyclerItemTypeProductDescription,

    /**
     * Restaurant Template
     */
    RecyclerItemTypeRestaurantNewsTop,

    RecyclerItemTypeRestaurantProductInfo,
    RecyclerItemTypeRestaurantProductDetail,

    RecyclerItemTypeRestaurantGridImage,
    RecyclerItemTypeRestaurantGridItem,
    RecyclerItemTypeRestaurantGridStaff,

    RecyclerItemTypeRestaurantRecyclerHorizontal,
    RecyclerItemTypeRestaurantRecyclerVertical,

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
