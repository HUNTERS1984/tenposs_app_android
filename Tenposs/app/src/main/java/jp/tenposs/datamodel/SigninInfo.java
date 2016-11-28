package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.HashMap;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.CryptoUtils;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/26/16.
 */
public class SignInInfo {
    public static class Request extends CommonRequest {
        public String email;
        String password;


        public void setPassword(String password) {
            this.password = CryptoUtils.sha256(password);
        }

        @Override
        String sigInput() {
            return app_id + "" + time + "" + email + "" + password + "" + privateKey;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                formData.put("app_id", app_id);
                formData.put("time", Long.toString(time));
                formData.put("email", email);
                formData.put("password", password);
                formData.put("sig", sig);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public static class Response extends CommonResponse {
        public User data;
    }


    /*{
        "code": "1000"
            , "message": "OK"
            , "data": {
        "token": "f5a1b79f8f3981ed8c86044e88d1849e"
                , "app_id": "1"
                , "id": 44
                , "email": null
                , "social_type": "1"
                , "social_id": "1471523209531107"
                , "profile": {
                    "name": "Nguyễn Huy Phúc"
                    , "gender": "0"
                    , "address": "東京都"
                    , "avatar_url": "uploads\/7c7732c35a15e45ab93f5a2ad6a424c3.png"
                    , "facebook_status": "0"
                    , "twitter_status": "0"
                    , "instagram_status": "0"
        }
    }
    }*/


    public class User implements Serializable {
        public Integer id;
        String email;
        public Integer social_type;
        public String social_id;
        public Integer app_id;
        public String token;
        public String promotion_code = "THIS IS MY CODE";
        public Profile profile;

        public String getEmail() {
            if (email != null) {
                return email;
            } else {
                return "";
            }
        }
    }

    public class Profile extends CommonItem implements Serializable {
        public Integer app_user_id;     //integer
        public String name;             //string
        public Integer gender;              //integer
        public String address;
        String avatar_url;       //string
        String avatar_file;       //string
        public Integer facebook_status;
        public Integer twitter_status;
        public Integer instagram_status;

        public void setImageFile(String filePath) {
            avatar_file = filePath;
        }

        @Override
        public String getImageUrl() {
            if (avatar_file != null) {
                return avatar_file;
            }
            return Utils.getImageUrl(TenpossCommunicator.DOMAIN_ADDRESS, avatar_url, "https://google.com");
        }

        @Override
        public String getCategory() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getPrice() {
            return null;
        }
    }
}