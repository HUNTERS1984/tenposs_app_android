package jp.tenposs.datamodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/29/16.
 */
public class CommonObject implements Serializable {
    public static Type customTypeJsonDeserializer() {
        return null;
    }

    public String toJSONString() {
        Gson gs = new Gson();
        String data = gs.toJson(this, this.getClass());
        return data;
    }

    public static String toJSONString(Object obj, Class cls) {
        Gson gs = new Gson();
        String data = gs.toJson(obj, cls);
        return data;
    }

    public static Object fromJSONString(String json, Class<?> cls, CustomListDeserializer customDeserializer) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gs;
            if (customDeserializer != null) {
                builder.registerTypeAdapter(customDeserializer.typeOfModel, customDeserializer);
            }
            gs = builder.create();

            return gs.fromJson(json, cls);
        } catch (Exception ex) {
            Utils.log("CommonObject", json);
            ex.printStackTrace();
        }
        return null;
    }

    public static CustomListDeserializer buildCustomDeserializer(Type type, String elementName) {
        if (type == null || elementName == null || elementName.isEmpty()) {
            return null;
        } else return new CustomListDeserializer(type, elementName);
    }

    public static List<CustomListDeserializer> buildCustomDeserializer(HashMap<Type, String> deserializers) {
        if (deserializers == null || deserializers.size() == 0) {
            return null;
        }
        List<CustomListDeserializer> dList = new ArrayList<>();
        Set<Type> keys = deserializers.keySet();

        for (Type type : keys) {
            String elementName = deserializers.get(type);
            if (type == null || elementName == null || elementName.isEmpty()) {

            } else {
                dList.add(new CustomListDeserializer(type, elementName));
            }
        }
        return dList;
    }

    public static class CustomListDeserializer<T> implements JsonDeserializer<T> {
        Type typeOfModel;
        String elementName;

        public CustomListDeserializer(Type type, String elementName) {
            this.typeOfModel = type;
            this.elementName = elementName;
        }

        @Override
        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get(this.elementName);
            return new Gson().fromJson(content, this.typeOfModel);
        }
    }
}
