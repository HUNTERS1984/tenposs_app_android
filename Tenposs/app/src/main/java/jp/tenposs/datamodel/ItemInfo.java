package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 8/12/16.
 */
public class ItemInfo {
    public static class Request extends CommonRequest {
        public int menu_id;
        public int pageindex;
        public int pagesize;

        @Override
        String sigInput() {
            return menu_id + "" + pageindex + "" + pagesize + "" + privateKey + "" + time;
        }
    }

    public class Response extends CommonResponse {

        public ResponseData data;

        public class ResponseData {
            public ArrayList<Item> items;

            public class Item implements Serializable {
                public int id;
                public String title;
                public String price;
                public String image_url;
                public String description;
                public String created_at;
                public String updated_at;
                public String deleted_at;
                public String coupon_id;
                public Pivot pivot;

                public class Pivot implements Serializable{
                    public int menu_id;
                    public int item_id;
                }

                public ArrayList<RelateItem> rel_items;

                public class RelateItem implements Serializable{
                    public int id;
                    public String price;
                    public String image_url;
                    public String description;
                    public String title;
                    public Pivot pivot;

                    public class Pivot implements Serializable{
                        public int item_id;
                        public int related_id;
                    }
                }
            }
        }

        public int total_items;
    }
}
