package jp.tenposs.datamodel;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.tenposs.utils.CryptoUtils;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/25/16.
 */
public abstract class CommonRequest implements Serializable {
    //name: "Restaurants App",
    public String app_id = "2a33ba4ea5c9d70f9eb22903ad1fb8b2";
    final static String privateKey = "002861062a0a2a5bb5c8a069d8ad1a66";

    //name: "Coffee App",


    //public String app_id = "bdb372395c03020340a3c863b27ffeef";
    //final static String privateKey = "a2ed29ec2df944c704e2dddcaace5332";

    //name: "Shopping App",
    //public String app_id = "f07305682545bce5bf644690f99b4df2";
    //final static String privateKey = "ba7c8d6c333b037bde734158c91426f1";

    //name: "Shopping App",
    //public String app_id = "69845f6c034706ceda10c6e34192726e";
    //final static String privateKey = "f1e02ec69e8cd447baa11f6459632233";

    //name: "Restaurants App",
    //public String app_id = "71097546401f99713bba55996e468c77";
    //final static String privateKey = "3b4d94ef3c2526a6ad7b2242f39d523d";

    //name:"Shopping App",
    //public String app_id = "b99dabb38f1dee9e6f029fcc9f4590c8";
    //final static String privateKey = "2b73509bc44e27339b3edc16d47c4ceb";

    //name:"News App",
    //public String app_id = "63b6a1e2813ce72b2b4a5b985d25173e";
    //final static String privateKey = "6bc801953b9dd8a1a2a0cbed9e2af928";

    //name:"News App",
    //public String app_id = "bdc10101c9b5ed9fb599a7c69ea73f6e";
    //final static String privateKey = "7e044b688edb047e6af628c1ace023ba";

    //name:"News App",
    //public String app_id = "58836f99b54198e1e8c82a776f20da08";
    //final static String privateKey = "23d7a613400c4cf126ada3bd4f53abbe";

    //name:"Coffee App",
    //public String app_id = "76daaa944a6ac16160352c7ed55959ae";
    //final static String privateKey = "8cc8cdefb227a1df930d6b44b2871a29";


    public String token = null;
    public String sig = null;
    public long time;

    abstract String sigInput();

    protected void generateSig() {
        time = Utils.gmtMillis();
        String input = sigInput();
        sig = CryptoUtils.sha256(input);
    }

    public String makeParams() {
        try {
            generateSig();
            String params = "";
            Field[] fields = this.getClass().getFields();
            String separate = "";
            for (Field field : fields) {
                try {
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    String name = URLEncoder.encode(field.getName(), "UTF-8");
                    Object value = field.get(this);
                    if (value != null) {
                        if (value instanceof Double || value instanceof Integer || value instanceof String || value instanceof Boolean || value instanceof Long || value instanceof Float || value instanceof Short) {
                            params += separate + name + "=" + URLEncoder.encode(value.toString(), "UTF-8");
                        } else if (value instanceof Date) {
                            params += separate + name + "=" + URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format((Date) value), "UTF-8");
                        }
                        if (separate == "") {
                            separate = "&";
                        }
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return params;
        } catch (Exception ex) {
            return "";
        }
    }
}
