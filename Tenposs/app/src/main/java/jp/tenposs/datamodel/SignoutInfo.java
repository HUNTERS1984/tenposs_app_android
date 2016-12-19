package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ambient on 7/26/16.
 */
public class SignOutInfo {
    public static class Request extends CommonRequest {
        private Serializable formData;

        @Override
        String sigInput() {
            return token + "" + time + "" + privateKey;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("token", token);
                formData.put("time", Long.toString(time));
                formData.put("sig", sig);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public class Response extends CommonResponse {
    }
}
