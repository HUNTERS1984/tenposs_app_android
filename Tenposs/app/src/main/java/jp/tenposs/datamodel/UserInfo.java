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
        public class ResponseData implements Serializable {
            public SignInInfo.User user;
        }

        public ResponseData data;
    }
}
