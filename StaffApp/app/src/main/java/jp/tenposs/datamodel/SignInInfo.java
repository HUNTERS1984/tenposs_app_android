package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by ambient on 7/26/16.
 */
public class SignInInfo {
    public static class Request extends CommonRequest {
        public String email;
        public String password;
        public String source;

        //public void setPassword(String password) {
        //this.password=CryptoUtils.sha256(password);
        //}

        @Override
        String sigInput() {
            //return app_id + "" + time + "" + email + "" + password + "" + privateKey;
            return "";
        }

        public HashMap<String, String> getFormData() {
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("email", email);
                formData.put("password", password);
                formData.put("source", source);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public static class Response extends CommonResponse {
        public ResponseData data;

        public static class ResponseData implements Serializable {
            public String token;
        }
    }
}