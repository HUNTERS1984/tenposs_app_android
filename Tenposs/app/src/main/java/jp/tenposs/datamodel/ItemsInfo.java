package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

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

        @Override
        public String getImageUrl() {
            String temp = image_url.toLowerCase(Locale.US);
            if (temp.indexOf("http://") != -1 || temp.indexOf("https://") != -1) {
                return image_url;
            } else {
                return TenpossCommunicator.BASE_ADDRESS + image_url;
            }
        }

        public ArrayList<Item> rel_items;

        public String getPrice() {
            int priceInt = Utils.atoi(this.price);

            String itemPrice = Utils.iToCurrency(priceInt);
            return "¥ " + itemPrice;
        }
    }
}
