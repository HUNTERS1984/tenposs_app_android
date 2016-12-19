package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ambient on 10/28/16.
 */

public class CouponRequestInfo {
    public class Response extends CommonResponse {
        public ResponseData data;

        public ArrayList<RequestInfo> getItems() {
            return data.list_request;
        }

        public RequestInfo get(int position) {
            return data.list_request.get(position);
        }

        public class ResponseData implements Serializable {
            public ArrayList<RequestInfo> list_request;
            public int total;
        }
    }

    public static class RequestInfo implements Serializable {
        public String coupon_id;
        //public String code;
        public String app_user_id;
        public String title;
        public String description;
        public String image_url;
        public String name;
        public String user_use_date;


        public String getImageUrl() {
            return image_url;
        }
    }
}