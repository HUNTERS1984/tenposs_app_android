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
        }
    }

    public class RequestInfo implements Serializable {
        public int coupon_id;
        public int app_user_id;
        public String title;
        public String description;
        public String image_url;
        public String name;
        public int user_use_date;
        public int total;

        public String getImageUrl() {
            return image_url;
        }
    }
}