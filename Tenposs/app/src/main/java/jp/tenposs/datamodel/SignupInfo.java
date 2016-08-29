package jp.tenposs.datamodel;

import jp.tenposs.utils.CryptoUtils;

/**
 * Created by ambient on 8/17/16.
 */
public class SignupInfo {
    public static class Request extends SignInInfo.Request {
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

    public class Response extends SignInInfo.Response {
    }
}
