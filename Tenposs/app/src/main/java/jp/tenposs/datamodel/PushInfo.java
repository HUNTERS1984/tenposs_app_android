package jp.tenposs.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ambient on 9/27/16.
 */

public class PushInfo {

    public static class Request extends CommonRequest {
        public int ranking;
        public int news;
        public int coupon;
        public int chat;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + privateKey;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            ArrayList<String> arr = new ArrayList<>();
            arr.add("app_id");
            return arr;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("app_id", app_id);
//                formData.put("token", token);

                formData.put("ranking", Integer.toString(ranking));
                formData.put("news", Integer.toString(news));
                formData.put("coupon", Integer.toString(coupon));
                formData.put("chat", Integer.toString(chat));
//                formData.put("time", Long.toString(time));
//                formData.put("sig", sig);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public static class Response extends CommonResponse {
        public Response() {
            this.data = new ResponseData();
        }

        public ResponseData data;

        public class ResponseData {
            int ranking;
            int news;
            int coupon;
            int chat;

            public ResponseData() {
                this.ranking = 0;
                this.news = 0;
                this.coupon = 0;
                this.chat = 0;
            }

            public boolean isRankingEnable() {
                return ranking == 1;
            }

            public boolean isNewsEnable() {
                return news == 1;
            }

            public boolean isCouponEnable() {
                return coupon == 1;
            }

            public boolean isChatEnable() {
                return chat == 1;
            }


            public void enableRanking(boolean isChecked) {
                ranking = (isChecked == true) ? 1 : 0;
            }

            public void enableChat(boolean isChecked) {
                chat = (isChecked == true) ? 1 : 0;
            }

            public void enableCoupon(boolean isChecked) {
                coupon = (isChecked == true) ? 1 : 0;
            }

            public void enableNews(boolean isChecked) {
                news = (isChecked == true) ? 1 : 0;
            }
        }

        public Response copy() {
            try {
                return (Response) CommonObject.fromJSONString(CommonObject.toJSONString(this, this.getClass()), this.getClass(), null);
            } catch (Exception ignored) {
                return this;
            }
        }
    }
}
