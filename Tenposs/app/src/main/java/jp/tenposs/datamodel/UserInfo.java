package jp.tenposs.datamodel;

import java.io.Serializable;

/**
 * Created by ambient on 9/7/16.
 */

public class UserInfo {
    public static class Request extends CommonRequest {

        @Override
        String sigInput() {
            return token + "" + time + "" + privateKey;
        }
    }

    public class Response extends CommonResponse {
        public class ResponseData {
            public User user;
        }

        public ResponseData data;
    }

    public class User implements Serializable{

        public int id;
        public String email;
        public String password;
        public String social_type;
        public String social_id;
        public String app_id;
        public SignInInfo.Profile profile;
    }
}
