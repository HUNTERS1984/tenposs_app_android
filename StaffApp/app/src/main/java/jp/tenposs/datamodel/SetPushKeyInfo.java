package jp.tenposs.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ambient on 9/23/16.
 */
public class SetPushKeyInfo {
    public static class Request extends CommonRequest {
        public String client = "android";   //string			o	0: android; 1:ios
        public String key;         //integer			o	android or ios push key


        @Override
        String sigInput() {
            return token + "" + client + "" + key + "" + time;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("app_id", app_id);
                formData.put("client", client);
                formData.put("key", key);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public class Response extends CommonResponse {
    }
}
