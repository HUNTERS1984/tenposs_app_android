package jp.tenposs.datamodel;

import java.util.HashMap;

/**
 * Created by ambient on 8/17/16.
 */
public class SignUpInfo {
    public static class Request extends SignInInfo.Request {
        public String name;

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
                formData.put("name", name);
                formData.put("sig", sig);
            } catch (Exception ignored) {

            }
            return formData;
        }
    }

    public class Response extends SignInInfo.Response {
    }
}