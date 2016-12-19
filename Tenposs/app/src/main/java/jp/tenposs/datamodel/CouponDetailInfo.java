package jp.tenposs.datamodel;

import java.util.ArrayList;

/**
 * Created by ambient on 9/2/16.
 */

public class CouponDetailInfo {

    public static class Request extends CommonRequest {
        public int coupon_id;

        @Override
        String sigInput() {
            return token + "" + coupon_id + "" + privateKey + "" + time;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }
    }

    public class Response extends CommonResponse {
        public CouponInfo data;
    }
}
