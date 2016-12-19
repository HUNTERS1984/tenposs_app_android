package jp.tenposs.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/17/16.
 */
public class SocialSignInInfo {
    public final static String FACEBOOK = "1";
    public final static String TWITTER = "2";
    public final static String INSTAGRAM = "3";

    public final static String TWITTER_CONSUMER_KEY = "qY0dnYDqh99zztg8gBWkLIFrm";
    public final static String TWITTER_CONSUMER_SECRET = "Byy6PCW51zvhVrDZayLm8PhenqkHXiRIqLMpK7A5H5XNEzlKYi";

    public static class Request extends CommonRequest {

        public String social_type;//1: facebook 2:twitter
        public String social_id;
        public String social_token;
        public String social_secret;//twitter secret (used for twitter only)
        public String platform = "android";
        public String avatar_url;
        public String username;

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
                formData.put("social_type", social_type);
                formData.put("social_token", social_token);
                formData.put("platform", platform);
                formData.put("app_id", app_id);
                formData.put("social_id", social_id);

                if (social_secret != null) {
                    formData.put("social_secret", social_secret);
                }

                if (avatar_url != null) {
                    formData.put("avatar_url", avatar_url);
                }

                if (username != null) {
                    formData.put("username", username);
                }

//                formData.put("sig", sig);
//                formData.put("time", Long.toString(time));

            } catch (Exception ignored) {
                Utils.log(ignored);
            }
            return formData;
        }
    }
}
