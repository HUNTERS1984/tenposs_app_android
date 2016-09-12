package jp.tenposs.datamodel;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.CryptoUtils;

/**
 * Created by ambient on 7/26/16.
 */
public class SignInInfo {
    public static class Request extends CommonRequest {
        public String email;
        String password;


        public void setPassword(String password) {
            this.password = CryptoUtils.sha256(password);
        }

        @Override
        String sigInput() {
            return app_id + "" + time + "" + email + "" + password + "" + privateKey;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("app_id", app_id);
                formData.put("time", Long.toString(time));
                formData.put("email", URLEncoder.encode(email, "UTF-8"));
//                formData.put("email", email);
                formData.put("password", password);
                formData.put("sig", sig);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public static class Response extends CommonResponse {
        public class ResponseData implements Serializable {

            public Profile profile;
            public String token;            //string			token sinh ra khi mỗi lần login thành công.
            public int app_id;              //integer
            public String login_type;       //string
            public int app_user_id;
        }

        public ResponseData data;
    }

    public class Profile implements Serializable {
        public int app_user_id;     //integer
        public String name;             //string
        public int gender;              //integer
        String avatar_url;       //string
        public int facebook_status;
        public int twitter_status;
        public int instagram_status;
        public int province;


        public String getImageUrl() {
            try {
                String temp = avatar_url.toLowerCase(Locale.US);
                if (temp.indexOf("http://") != -1 || temp.indexOf("https://") != -1) {
                    return avatar_url;
                } else {
                    return TenpossCommunicator.BASE_ADDRESS + avatar_url;
                }
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}