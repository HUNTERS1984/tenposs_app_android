package jp.tenposs.datamodel;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.tenposs.utils.CryptoUtils;

/**
 * Created by ambient on 7/25/16.
 */
public abstract class CommonRequest implements Serializable {

    final static String privateKey = "Tenposs";

    public String token = null;
    public String sig = null;
    public long time;

    abstract String sigInput();

    void generateSig(String input) {
        time = System.currentTimeMillis();
        sig = CryptoUtils.sha256(input);
    }

    public String makeParams(String method) {
        generateSig(sigInput());
        String params = "";
        if (method.compareToIgnoreCase("GET") == 0) {
            Field[] fields = this.getClass().getFields();
            String separate = "";
            for (Field field : fields) {
                try {
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    String name = field.getName();
                    Object value = field.get(this);
                    if (value != null) {
                        if (value instanceof Double || value instanceof Integer || value instanceof String || value instanceof Boolean || value instanceof Long || value instanceof Float || value instanceof Short) {
                            params += separate + name + "=" + value.toString();
                        } else if (value instanceof Date) {
                            params += separate + name + "=" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format((Date) value);
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
        } else {
            //POST
        }
        return params;
    }
}
