package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 9/12/16.
 */

public class StaffInfo {
    public static class Request extends CommonRequest {

        public int store_id;
        public int pageindex;
        public int pagesize;
        public int category_id;

        @Override
        String sigInput() {
            //TODO: chua co spec
            return "";
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData implements Serializable {
            public ArrayList<Staff> staffs;
            public int total_staffs;
        }

        public ResponseData data;
    }

    public class Staff extends CommonItem implements Serializable {
        public int id;
        public String name;
        public String price;
        String image_url;
        public String introduction;
        public String gender;
        public String birthday;
        public String tel;
        public String created_at;
        public String updated_at;
        public String deleted_at;
        public String staff_category_id;
        public String staff_category = "";

        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");
        }

        @Override
        public String getCategory() {
            return null;
        }

        @Override
        public String getTitle() {
            if (name != null) {
                return name;
            } else {
                return "";
            }
        }

        @Override
        public String getDescription() {
            if (introduction != null) {
                introduction = introduction.replaceAll("\r\n", "\n");
                introduction = introduction.replaceAll("\n\r", "\n");
                return introduction;
            } else {
                return "";
            }
        }

        @Override
        public String getPrice() {
            String itemPrice = Utils.aToCurrency(price);
            return "¥ " + itemPrice;
        }
    }
}
