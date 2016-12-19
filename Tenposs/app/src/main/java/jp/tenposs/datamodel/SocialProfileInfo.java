package jp.tenposs.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ambient on 9/23/16.
 */

public class SocialProfileInfo {
    public static class Request extends CommonRequest {
        public String social_type;
        public String social_id;
        public String social_token;
        public String social_secret;
        public String nickname;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + social_type + "" + social_id + "" + privateKey;
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
                formData.put("social_type", social_type);

                if (social_id != null) {
                    formData.put("social_id", social_id);
                }
                if (social_token != null) {
                    formData.put("social_token", social_token);
                }
                if (social_secret != null) {
                    formData.put("social_secret", social_secret);
                } else {
                    formData.put("social_secret", "");
                }
                if (nickname != null) {
                    formData.put("nickname", nickname);
                } else {
                    formData.put("nickname", "");
                }
            } catch (Exception ignored) {

            }
            return formData;
        }

    }

    public class Response extends CommonResponse {

    }
}
