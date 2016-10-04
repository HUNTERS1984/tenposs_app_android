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
    }

    public class Response extends CommonResponse {
        public class ResponseData implements Serializable {
            public ArrayList<Staff> staffs;
            public int total_staffs;
        }

        public ResponseData data;
    }

    public class Staff extends UrlImageObject implements Serializable {
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

        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");
        }
    }
}
