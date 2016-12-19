package jp.tenposs.datamodel;

import java.util.ArrayList;

/**
 * Created by ambient on 12/3/16.
 */

public class RefreshTokenInfo {
    public static class Request extends CommonRequest {
        public String access_refresh_token_href;

        @Override
        String sigInput() {
            return null;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            ArrayList<String> arrParam = new ArrayList<>();
            arrParam.add("id_code");
            arrParam.add("refresh_token");
            return arrParam;
        }
    }

    public class Response extends CommonResponse {

        public String token;
        public String refresh_token;
        public String access_refresh_token_href;
    }
}
