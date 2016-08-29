package jp.tenposs.datamodel;

/**
 * Created by ambient on 8/17/16.
 */
public class SocialSigninInfo {
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
    }

    public class Response extends CommonResponse {
        public Profile profile;
        public String token;
        public String app_id;
        public String app_user_id;
    }

    public class Profile {
        public String user_profile_id;
        public String name;
        public String gender;
        public String avatar_url;
        public String facebook_status;
        public String twitter_status;
        public String instagram_status;
    }

}
