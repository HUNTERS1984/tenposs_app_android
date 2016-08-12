package jp.tenposs.adapter;

/**
 * Created by ambient on 8/2/16.
 */
public class RecyclerItemWrapper {
    public static class RecyclerItemObject {
        public int id;
        public String title;
        public String description;
        public String image;
        public String moreDescription;

    }

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

    public static RecyclerItemObject createItem(int id,
                                                String title,
                                                String description,
                                                String moreDescription,
                                                String image
    ) {
        RecyclerItemObject obj = new RecyclerItemObject();
        obj.id = id;
        obj.title = title;
        obj.description = description;
        obj.moreDescription = moreDescription;
        obj.image = image;
        return obj;
    }
}