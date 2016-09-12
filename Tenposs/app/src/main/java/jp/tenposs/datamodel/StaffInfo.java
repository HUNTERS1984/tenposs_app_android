package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import jp.tenposs.communicator.TenpossCommunicator;

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
            //TODO:
            return "";
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData {
            public ArrayList<Staff> staffs;
            public int total_staffs;
        }

        public ResponseData data;
    }

    public class Staff implements Serializable {
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

        public String getImageUrl() {
            String temp = image_url.toLowerCase(Locale.US);
            if (temp.indexOf("http://") != -1 || temp.indexOf("https://") != -1) {
                return image_url;
            } else {
                return TenpossCommunicator.BASE_ADDRESS + image_url;
            }
        }
    }
}
