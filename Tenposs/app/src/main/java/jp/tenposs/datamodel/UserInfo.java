package jp.tenposs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 9/7/16.
 */

public class UserInfo {
    public static class Request extends CommonRequest {

        @Override
        String sigInput() {
            return token + "" + time + "" + privateKey;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            ArrayList<String> arr = new ArrayList<>();
            arr.add("app_id");
            return arr;
        }
    }


    public static class Response extends CommonResponse {
        public ResponseData data;

        public class ResponseData {

            public User user;
        }
    }

    public class User implements Serializable {

//            "user": {
//                        , "created_at": "2016-12-07 07:57:05"
//                        , "updated_at": "2016-12-07 07:57:05"
//                        , "deleted_at": null
//                        , "app_id": "1"
//                        , "auth_user_id": "88"
//                        , "email": "2429044514@tw.com"
//                        , "profile": {
//                    "name": "Phúc Nguyễn"
//                            , "gender": "1"
//                            , "address": "北海道"
//                            , "avatar_url": "https:\/\/api.ten-po.com\/uploads\/6ff451920aa162c173ea7fad073ab1c1.png"
//                            , "facebook_status": "1"
//                            , "twitter_status": "1"
//                            , "instagram_status": "0"

        public Integer id;
        public Integer app_id;
        String email;
        public Profile profile;

        public String created_at;
        public String updated_at;
        public String deleted_at;
        public Integer auth_user_id;

        public String getEmail() {
            if (email != null) {
                return email;
            } else {
                return "";
            }
        }

        public String getName() {
            if (profile != null && profile.name != null)
                return profile.name;
            else return "";
        }
    }

    public class Profile extends CommonItem implements Serializable {
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
