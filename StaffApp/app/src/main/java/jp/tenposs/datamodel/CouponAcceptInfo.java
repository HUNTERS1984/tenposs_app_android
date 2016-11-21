package jp.tenposs.datamodel;

import java.util.HashMap;

/**
 * Created by ambient on 10/17/16.
 */

public class CouponAcceptInfo {
    public static class Request extends CommonRequest {
        public String action;
        public int coupon_id;

        @Override
        String sigInput() {
            return null;
        }

        public HashMap<String, String> getFormData() {
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("action", action);
                formData.put("coupon_id", Integer.toString(coupon_id));
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public class Response extends CommonResponse {
    }
}
