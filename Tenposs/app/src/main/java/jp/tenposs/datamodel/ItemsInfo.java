package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/12/16.
 */
public class ItemsInfo {
    public static class Request extends CommonRequest {
        public int menu_id;
        public int pageindex;
        public int pagesize;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + menu_id + "" + privateKey;
//            return menu_id + "" + pageindex + "" + pagesize + "" + privateKey + "" + time;
        }
    }

    public class Response extends CommonResponse {

        public ResponseData data;

        public class ResponseData implements Serializable {
            public ArrayList<Item> items;


        }

        public int total_items;
    }

    public class Item extends UrlImageObject implements Serializable {
        public int id;
        public String title;
        String price;
        String image_url;
        public String description;
        public String created_at;
        public String updated_at;
        public String deleted_at;
        public String item_link;
        public String coupon_id;

        ArrayList<ItemSize> size;

        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");
        }

        public ArrayList<Item> rel_items;

        public String getPrice() {
            int priceInt = Utils.atoi(this.price);

            String itemPrice = Utils.iToCurrency(priceInt);
            return "¥ " + itemPrice;
        }

        public boolean hasSizes() {
            return (this.size != null && this.size.size() > 0);
        }

        ArrayList<String> getCategoryIds() {
            ArrayList<String> Ids = new ArrayList<>();
            for (ItemSize itemSize : size) {
                if (Ids.contains(itemSize.item_size_category_id) == false) {
                    Ids.add(itemSize.item_size_category_id);
                }
            }
            return Ids;
        }

        ArrayList<String> getCategoryNames() {
            ArrayList<String> Names = new ArrayList<>();
            for (ItemSize itemSize : size) {
                if (Names.contains(itemSize.item_size_category_name) == false) {
                    Names.add(itemSize.item_size_category_name);
                }
            }
            return Names;
        }

        ArrayList<ItemSize> getItemSizeByCategoryId(String category_id) {
            ArrayList<ItemSize> items = new ArrayList<>();
            for (ItemSize itemSize : size) {
                if (itemSize.item_size_category_id.equalsIgnoreCase(category_id)) {
                    items.add(itemSize);
                }
            }
            return items;
        }

        public int numberOfColumns() {
            ArrayList<String> colums = new ArrayList<>();
            for (ItemSize itemSize : size) {
                if (colums.contains(itemSize.item_size_type_id) == false) {
                    colums.add(itemSize.item_size_type_id);
                }
            }
            return colums.size();
        }

        public int numberOfRows() {
            ArrayList<String> rows = getCategoryIds();
            return rows.size();
        }

        public ArrayList<String> getTableHeaders() {
            ArrayList<String> headers = new ArrayList<>();
            headers.add("");
            for (ItemSize itemSize : size) {
                if (headers.contains(itemSize.item_size_type_name) == false) {
                    headers.add(itemSize.item_size_type_name);
                }
            }
            return headers;
        }

        public ArrayList<String> getTableItems() {
            ArrayList<String> Ids = getCategoryIds();
            ArrayList<String> Names = getCategoryNames();
            ArrayList<String> items = new ArrayList<>();
            int count = 0;
            for (String category_id : Ids) {
                items.add(Names.get(count));
                ArrayList<ItemSize> categoryItems = getItemSizeByCategoryId(category_id);
                for (ItemSize itemSize : categoryItems) {
                    items.add(itemSize.value);
                }
                count++;
            }
            return items;
        }
    }

    public class ItemSize implements Serializable {
        public String item_size_type_id;
        public String item_size_type_name;
        public String item_size_category_id;
        public String item_size_category_name;
        public String value;
    }
}
