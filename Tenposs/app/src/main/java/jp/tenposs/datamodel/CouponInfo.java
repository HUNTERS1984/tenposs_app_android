package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import jp.tenposs.communicator.TenpossCommunicator;

/**
 * Created by ambient on 9/2/16.
 */

public class CouponInfo {
    public static class Request extends CommonRequest {

        public int store_id;
        public int pageindex;
        public int pagesize;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + store_id + "" + privateKey;
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData implements Serializable {
            public ArrayList<Coupon> coupons;
        }

        public ResponseData data;
        public int total_coupons;

        public Coupon getItemById(int id) {
            try {
                for (Coupon coupon : data.coupons) {
                    if (coupon.id == id) {
                        return coupon;
                    }
                }
                return null;
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    public class Coupon extends UrlImageObject implements Serializable {
        public int id;
        public int type;
        public String title;
        public String description;
        public String start_date;
        public String end_date;
        public int status;
        String image_url;
        public String created_at;
        public String updated_at;
        public int store_id;

        @Override
        public String getImageUrl() {
            String temp = image_url.toLowerCase(Locale.US);
            if (temp.indexOf("http://") != -1 || temp.indexOf("https://") != -1) {
                return image_url;
            } else {
                return TenpossCommunicator.BASE_ADDRESS + image_url;
            }
        }
    }
    //total_coupons
}
