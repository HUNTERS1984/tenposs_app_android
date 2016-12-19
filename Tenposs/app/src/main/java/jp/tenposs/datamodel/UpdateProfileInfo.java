package jp.tenposs.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ambient on 9/16/16.
 */

public class UpdateProfileInfo {

    public static class Request extends CommonRequest {
        public String username;
        public int gender;
        public String address;
        public String avatar;

        @Override
        String sigInput() {
            return app_id + "" + time + "" + privateKey;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
//                formData.put("token", token);
//                formData.put("time", Long.toString(time));

                formData.put("app_id", app_id);
                formData.put("username", username);
                formData.put("gender", Integer.toString(gender));
                formData.put("address", address);
                if (avatar != null) {
                    formData.put("avatar", avatar);
                }
//                formData.put("sig", sig);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }
}
