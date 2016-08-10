package jp.tenposs.datamodel;

import java.io.Serializable;

import jp.tenposs.utils.CryptoUtils;

/**
 * Created by ambient on 7/26/16.
 */
public class LoginInfo {
    public class Request extends CommonRequest {
        public String email;        //string			o	username
        public String password;     //string			o	sha256(password)
        public String type;         //string			o	type login: email or social
        public String social_id;    //string				facebookid,twitterid, instagramid
        public String social_type;  //string				facebook,twitter, instagram
        public String social_token; //string				facebook_token,twitter_token, instagram_token
        public String time;         //string				timestamp miliseconds
        public String sig;

        public void generateSig() {
            String input = "";
            String privateKey = "TODO: ";
            if (type.compareTo("email") == 0) {
                //if login with email: sig = sha256(email + private key + type)
                input = this.email + privateKey + type;

            } else if (type.compareTo("social") == 0) {
                //if login with social: sig = sha256(social_id + social_type + private key + type)
                input = this.social_id + this.social_type + privateKey + type;
            }
            this.sig = CryptoUtils.sha256(input);
        }

        //if login with email: sig = sha256(email + private key + type)
        //if login with social: sig = sha256(social_id + social_type + private key + type)
        //private key: request when integrate"
    }

    public class Response extends CommonResponse {
        public class LoginResponseData implements Serializable {
            public class Profile {
                public int user_profile_id;     //integer
                public String name;             //string
                public int gender;              //integer
                public String avatar_url;       //string
                public int facebook_status;
                public int twitter_status;
                public int instagram_status;
            }

            public Profile profile;
            public String token;            //string			token sinh ra khi mỗi lần login thành công.
            public int app_id;              //integer
            public String login_type;       //string
        }

        public LoginResponseData data;
    }
}