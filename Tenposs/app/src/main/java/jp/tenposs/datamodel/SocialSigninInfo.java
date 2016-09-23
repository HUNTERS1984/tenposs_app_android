package jp.tenposs.datamodel;

import java.util.HashMap;

/**
 * Created by ambient on 8/17/16.
 */
public class SocialSigninInfo {
    public final static String FACEBOOK = "1";
    public final static String TWITTER = "2";
    public final static String INSTAGRAM = "3";

    public final static String TWITTER_CONSUMER_KEY = "qY0dnYDqh99zztg8gBWkLIFrm";
    public final static String TWITTER_CONSUMER_SECRET = "Byy6PCW51zvhVrDZayLm8PhenqkHXiRIqLMpK7A5H5XNEzlKYi";

    public static class Request extends CommonRequest {

        public String social_type;
        public String social_id;//1: facebook 2:twitter
        public String social_token;
        public String social_secret;//twitter secret (used for twitter only)
        public String name;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + social_type + "" + social_id + "" + privateKey;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("app_id", app_id);
                formData.put("time", Long.toString(time));
                formData.put("social_type", social_type);
                formData.put("social_id", social_id);
                formData.put("social_token", social_token);
                if (social_secret != null) {
                    formData.put("social_secret", social_secret);
                }
                formData.put("name", name);
                formData.put("sig", sig);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }
}
