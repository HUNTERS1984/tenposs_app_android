package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ambient on 7/26/16.
 */
public class SignInInfo {

    public final static String admin = "admin";
    public final static String client = "client";
    public final static String staff = "staff";
    public final static String user = "user";

    public final static String ios = "ios";
    public final static String android = "android";
    public final static String web = "web";

    public static class Request extends CommonRequest {
        public String email;
        String password;
        public String role;
        public String platform = android;

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        String sigInput() {
            return app_id + "" + time + "" + email + "" + password + "" + privateKey;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("email", email);
                formData.put("password", password);
                formData.put("role", role);
                formData.put("platform", platform);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public static class Response extends CommonResponse {
        public Token data;
    }


    public class Token implements Serializable {
        public String token;
        public String refresh_token;
        public String access_refresh_token_href;
        public boolean first_login;

    }

    /*{
        "code": "1000"
            , "message": "OK"
            , "data": {
        "token": "f5a1b79f8f3981ed8c86044e88d1849e"
                , "app_id": "1"
                , "id": 44
                , "email": null
                , "social_type": "1"
                , "social_id": "1471523209531107"
                , "profile": {
                    "name": "Nguyễn Huy Phúc"
                    , "gender": "0"
                    , "address": "東京都"
                    , "avatar_url": "uploads\/7c7732c35a15e45ab93f5a2ad6a424c3.png"
                    , "facebook_status": "0"
                    , "twitter_status": "0"
                    , "instagram_status": "0"
        }
    }
    }*/
}