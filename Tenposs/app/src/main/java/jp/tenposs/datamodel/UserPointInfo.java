package jp.tenposs.datamodel;

import java.util.ArrayList;

/**
 * Created by ambient on 12/7/16.
 */

public class UserPointInfo {
    public static class Request extends CommonRequest {

        @Override
        String sigInput() {
            return "";
        }

        @Override
        ArrayList<String> getAvailableParams() {
            ArrayList<String> arr = new ArrayList<>();
            arr.add("app_id");
            return arr;
        }
    }

    public class Response extends CommonResponse {
        public ResponseData data;//									json

        public class ResponseData {
            public int auth_user_id;//integer
            public String app_app_id;//string
            public int points;//integer
            public int miles;//integer
            public int next_points;//integer
            public int next_miles;//integer
        }
    }
}
