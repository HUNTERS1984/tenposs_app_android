package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.tenposs.BuildConfig;
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

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }
    }

    public class Response extends CommonResponse {

        public ResponseData data;

        public class ResponseData implements Serializable {
            public ArrayList<Item> items;


        }

        public int total_items;
    }

    public class Item extends CommonItem implements Serializable {
        public int id;
        String title;
        String price;
        String image_url;
        String description;
        public String created_at;
        public String updated_at;
        public String deleted_at;
        public String item_link;
        public String coupon_id;
        public String menu_name;

        ArrayList<ItemSize> size;


        public boolean hasSizes() {
            return (this.size != null && this.size.size() > 0);
        }

        String getSize(String typeId, String categoryId) {
            for (ItemSize itemSize : size) {
                if (itemSize.item_size_type_id.compareToIgnoreCase(typeId) == 0 &&
                        itemSize.item_size_category_id.compareToIgnoreCase(categoryId) == 0) {
                    return itemSize.value;
                }
            }
            return "";
        }

        ArrayList<String> getTypeIds() {
            ArrayList<String> Ids = new ArrayList<>();
            for (ItemSize itemSize : size) {
                if (Ids.contains(itemSize.item_size_type_id) == false) {
                    Ids.add(itemSize.item_size_type_id);
                }
            }
            return Ids;
        }

        ArrayList<String> getTypeNames() {
            ArrayList<String> Names = new ArrayList<>();
            for (ItemSize itemSize : size) {
                if (Names.contains(itemSize.item_size_type_name) == false) {
                    Names.add(itemSize.item_size_type_name);
                }
            }
            return Names;
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

        public int numberOfRows() {
            ArrayList<String> rows = new ArrayList<>();
            for (ItemSize itemSize : size) {
                if (rows.contains(itemSize.item_size_type_id) == false) {
                    rows.add(itemSize.item_size_type_id);
                }
            }
            return rows.size();
        }

        public int numberOfColumns() {
            ArrayList<String> columns = getCategoryIds();
            return columns.size();
        }

        public ArrayList<ArrayList<String>> getTableData() {
            ArrayList<ArrayList<String>> data = new ArrayList<>();
            ArrayList<String> header = new ArrayList<>();

            ArrayList<String> typeIds = getTypeIds();
            ArrayList<String> typeNames = getTypeNames();
            ArrayList<String> categoryIds = getCategoryIds();
            ArrayList<String> categoryNames = getCategoryNames();

            header.add("#");
            header.addAll(categoryNames);
            data.add(header);

            String typeId;
            String categoryId;

            for (int row = 0; row < numberOfRows(); row++) {
                ArrayList<String> line = new ArrayList<>();
                line.add(typeNames.get(row));
                for (int colum = 0; colum < numberOfColumns(); colum++) {
                    typeId = typeIds.get(row);
                    categoryId = categoryIds.get(colum);
                    line.add(getSize(typeId, categoryId));
                }
                data.add(line);
            }
            return data;
        }


        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");
        }

        public String getCategory() {
            if (menu_name != null)
                return menu_name;
            else {
                if (BuildConfig.DEBUG) {
                    return "会席料理コース";
                } else {
                    return "";
                }
            }
        }

        public String getTitle() {
            if (title != null)
                return title;
            else
                return "";
        }

        public String getDescription() {
            if (description != null) {
                description = description.replaceAll("\r\n", "\n");
                description = description.replaceAll("\n\r", "\n");
                return description;
            } else {
                if (BuildConfig.DEBUG) {
                    return "先付、お造り、焚き合わせ、お凌ぎ（松阪牛握り、松阪牛と鮪の裏巻寿司）、\n" +
                            "冷菜、揚げ物（松阪牛の天ぷら他）、\n" +
                            "焼き物（伊勢海老またはあわび(あわびは＋800円)";
                } else {
                    return "";
                }
            }
        }

        public String getPrice() {
            int priceInt = Utils.atoi(this.price);

            String itemPrice = Utils.iToCurrency(priceInt);
            return "¥ " + itemPrice;
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
