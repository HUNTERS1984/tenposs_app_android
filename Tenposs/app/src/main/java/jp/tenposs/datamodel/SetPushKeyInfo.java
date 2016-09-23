package jp.tenposs.datamodel;

import java.util.HashMap;

/**
 * Created by ambient on 9/23/16.
 */
public class SetPushKeyInfo {
    public static class Request extends CommonRequest {
        public int client = 0;   //string			o	0: android; 1:ios
        public String key;         //integer			o	android or ios push key


        @Override
        String sigInput() {
            return token + "" + client + "" + key + "" + time;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("app_id", app_id);
                formData.put("time", Long.toString(time));
                formData.put("sig", sig);
                formData.put("client", Integer.toString(client));
                formData.put("key", key);
                formData.put("token", token);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public class Response extends CommonResponse {
    }
}
