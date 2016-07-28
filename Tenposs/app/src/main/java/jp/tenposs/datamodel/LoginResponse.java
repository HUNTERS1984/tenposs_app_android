package jp.tenposs.datamodel;

import java.util.List;

/**
 * Created by ambient on 7/25/16.
 */
public class LoginResponse extends CommonResponse {

    public class LoginResultData {
        List<String> characters;
        public String token;
        long id;
        String email;
    }

    LoginResultData data;
}
