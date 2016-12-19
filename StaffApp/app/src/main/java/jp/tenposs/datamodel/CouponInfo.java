package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 10/17/16.
 */

public class CouponInfo {
    public static class Request extends CommonRequest {
        @Override
        String sigInput() {
            return null;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }
    }

    public class Response extends CommonResponse {
        public ArrayList<Coupon> getItems() {
            return data.coupons;
        }

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
        public String created_at;
        public String updated_at;
        public String deleted_at;
        String image_url;
        public String limit;
        public String coupon_type_id;
        ArrayList<String> taglist;
        public boolean can_use;

        public String code;
        public CouponType coupon_type;

        public class CouponType {
            public int id;
            public String name;
            public String store_id;
            public String deleted_at;
        }


        @Override
        public String getImageUrl() {
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, image_url, "https://google.com");

        }

        public String getHashTag() {
            String hashTag = "";

            if (this.taglist != null && this.taglist.size() > 0) {
                for (String tag : taglist) {
                    hashTag += "#" + tag + " ";
                }
            }
            return hashTag;
        }
    }
}
