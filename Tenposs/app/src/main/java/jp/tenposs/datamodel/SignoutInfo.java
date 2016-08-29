package jp.tenposs.datamodel;

/**
 * Created by ambient on 7/26/16.
 */
public class SignoutInfo {
    public class Request extends CommonRequest {
        @Override
        String sigInput() {
            return token + "" + time + "" + privateKey;
        }
    }

    public class Response extends CommonResponse {
    }
}
