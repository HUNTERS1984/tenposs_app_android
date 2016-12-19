package jp.tenposs.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ambient on 12/13/16.
 */

public class CompleteProfileInfo {
    public static class Request extends CommonRequest {
        public String birthday;
        public String address;
        public String code;
        public String email;
        public String gender;

        @Override
        String sigInput() {
            return null;
        }

        @Override
        ArrayList<String> getAvailableParams() {
            return null;
        }

        public HashMap<String, String> getFormData() {
            generateSig();
            HashMap<String, String> formData = new HashMap<>();
            try {
                if (birthday != null) {
                    formData.put("birthday ", birthday);
                }
                if (gender != null) {
                    formData.put("gender", gender);
                }
                if (address != null) {
                    formData.put("address ", address);
                }
                if (email != null) {
                    formData.put("email", email);
                }
                if (code != null) {
                    formData.put("code", code);
                }
                formData.put("app_id", app_id);

            } catch (Exception ignored) {

            }
            return formData;
        }
    }
}
