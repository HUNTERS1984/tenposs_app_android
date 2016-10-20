package jp.tenposs.datamodel;

/**
 * Created by ambient on 10/17/16.
 */
public class SignInInfo {
    public static class Request extends CommonRequest {
        @Override
        String sigInput() {
            return null;
        }
    }

    public class Response extends CommonResponse {

    }
}
