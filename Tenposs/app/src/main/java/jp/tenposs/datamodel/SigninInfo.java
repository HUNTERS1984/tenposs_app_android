package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.utils.CryptoUtils;

/**
 * Created by ambient on 7/26/16.
 */
public class SignInInfo {
    public static class Request extends CommonRequest {
        public String email;
        String password;
        public String name;

        public void setPassword(String password) {
            this.password = CryptoUtils.sha256(password);
        }

        @Override
        String sigInput() {
            return app_id + "" + time + "" + email + "" + password + "" + privateKey;
        }
    }

    public static class Response extends CommonResponse {
        public class ResponseData implements Serializable {

            public ArrayList<Profile> profile;
            public String token;            //string			token sinh ra khi mỗi lần login thành công.
            public int app_id;              //integer
            public String login_type;       //string
            public int app_user_id;
        }

        public ResponseData data;
    }

    public class Profile implements Serializable {
        public int user_profile_id;     //integer
        public String name;             //string
        public int gender;              //integer
        public String avatar_url;       //string
        public int facebook_status;
        public int twitter_status;
        public int instagram_status;
    }
}