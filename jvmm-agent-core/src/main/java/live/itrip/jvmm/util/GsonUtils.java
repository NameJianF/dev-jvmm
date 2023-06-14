package live.itrip.jvmm.util;

import com.google.common.base.Strings;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import live.itrip.jvmm.json.JsonPath2;
import live.itrip.jvmm.json.Parser;
import live.itrip.jvmm.monitor.core.entity.dto.PatchDTO;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于Gson封装的jsonUtil
 *
 * @author : fengjianfeng
 * @date : 2021-08-03 18:24
 * description : GsonUtil
 **/
public class GsonUtils {
    private static final String KEY_DEFAULT = "defaultGson";
    private static final String KEY_DELEGATE = "delegateGson";
    private static final String KEY_LOG_UTILS = "logUtilsGson";

    private static final Map<String, Gson> GSONS = new ConcurrentHashMap<>();

    private GsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * get first element value as String
     *
     * @param json json
     * @param expr json path expression
     * @return list
     */
    public static List<JsonElement> readJsonElements(String json, String expr) {
//        String json = "{'c':[{'v':5},{'v':51},{'v':52},{'c':{'v': 777}}], 'v':1}";
//        Expression expression = new Parser().parseExpression("$.c..v");
        JsonPath2.Expression expression = new Parser().parseExpression(expr);
        return expression.exec(json);
    }

    /**
     * get first element value as String
     *
     * @param json json
     * @param expr json path expression
     * @return string
     */
    public static String readAsString(String json, String expr) {
//        String json = "{'c':[{'v':5},{'v':51},{'v':52},{'c':{'v': 777}}], 'v':1}";
//        Expression expression = new Parser().parseExpression("$.c..v");
        JsonPath2.Expression expression = new Parser().parseExpression(expr);
        List<JsonElement> list = expression.exec(json);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0).getAsString();
    }

    /**
     * object to json element
     *
     * @param object object
     * @return element
     */
    public static JsonElement toJsonTree(Object object) {
        return getGson().toJsonTree(object);
    }


    /**
     * Set the delegate of {@link Gson}.
     *
     * @param delegate The delegate of {@link Gson}.
     */
    public static void setGsonDelegate(Gson delegate) {
        if (delegate == null) return;
        GSONS.put(KEY_DELEGATE, delegate);
    }

    /**
     * Set the {@link Gson} with key.
     *
     * @param key  The key.
     * @param gson The {@link Gson}.
     */
    public static void setGson(final String key, final Gson gson) {
        if (Strings.isNullOrEmpty(key) || gson == null) {
            return;
        }
        GSONS.put(key, gson);
    }

    /**
     * Return the {@link Gson} with key.
     *
     * @param key The key.
     * @return the {@link Gson} with key
     */
    public static Gson getGson(final String key) {
        return GSONS.get(key);
    }

    public static Gson getGson() {
        Gson gsonDelegate = GSONS.get(KEY_DELEGATE);
        if (gsonDelegate != null) {
            return gsonDelegate;
        }
        Gson gsonDefault = GSONS.get(KEY_DEFAULT);
        if (gsonDefault == null) {
            gsonDefault = createGson();
            GSONS.put(KEY_DEFAULT, gsonDefault);
        }
        return gsonDefault;
    }

    /**
     * Serializes an object into json.
     *
     * @param object The object to serialize.
     * @return object serialized into json.
     */
    public static String toJson(final Object object) {
        return toJson(getGson(), object);
    }

    /**
     * Serializes an object into json.
     *
     * @param src       The object to serialize.
     * @param typeOfSrc The specific genericized type of src.
     * @return object serialized into json.
     */
    public static String toJson(final Object src, @NonNull final Type typeOfSrc) {
        return toJson(getGson(), src, typeOfSrc);
    }

    /**
     * Serializes an object into json.
     *
     * @param gson   The gson.
     * @param object The object to serialize.
     * @return object serialized into json.
     */
    public static String toJson(@NonNull final Gson gson, final Object object) {
        return gson.toJson(object);
    }

    /**
     * Serializes an object into json.
     *
     * @param gson      The gson.
     * @param src       The object to serialize.
     * @param typeOfSrc The specific genericized type of src.
     * @return object serialized into json.
     */
    public static String toJson(@NonNull final Gson gson, final Object src, @NonNull final Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param json The json to convert.
     * @param type Type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(final String json, @NonNull final Class<T> type) {
        return fromJson(getGson(), json, type);
    }
    /**
     * Converts {@link String} to given type.
     *
     * @param json The json to convert.
     * @return instance of type
     */
    public static <T> List<T> fromJson2List(final String json) {
//        return fromJson(getGson(), json, type);
        return GsonUtils.fromJson(json, new TypeToken<List<T>>() {}.getType());
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param json the json to convert.
     * @param type type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(final String json, @NonNull final Type type) {
        return fromJson(getGson(), json, type);
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param reader the reader to convert.
     * @param type  type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Reader reader, @NonNull final Class<T> type) {
        return fromJson(getGson(), reader, type);
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param reader the reader to convert.
     * @param type  type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Reader reader, @NonNull final Type type) {
        return fromJson(getGson(), reader, type);
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param gson The gson.
     * @param json The json to convert.
     * @param type Type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Gson gson, final String json, @NonNull final Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param gson The gson.
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Gson gson, final String json, @NonNull final Type type) {
        return gson.fromJson(json, type);
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param gson   The gson.
     * @param reader the reader to convert.
     * @param type  type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Gson gson, final Reader reader, @NonNull final Class<T> type) {
        return gson.fromJson(reader, type);
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param gson   The gson.
     * @param reader the reader to convert.
     * @param type  type json will be converted to.
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Gson gson, final Reader reader, @NonNull final Type type) {
        return gson.fromJson(reader, type);
    }

    /**
     * Return the type of {@link List} with the {@code type}.
     *
     * @param type The type.
     * @return the type of {@link List} with the {@code type}
     */
    public static Type getListType(@NonNull final Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    /**
     * Return the type of {@link Set} with the {@code type}.
     *
     * @param type The type.
     * @return the type of {@link Set} with the {@code type}
     */
    public static Type getSetType(@NonNull final Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }

    /**
     * Return the type of map with the {@code keyType} and {@code valueType}.
     *
     * @param keyType   The type of key.
     * @param valueType The type of value.
     * @return the type of map with the {@code keyType} and {@code valueType}
     */
    public static Type getMapType(@NonNull final Type keyType, @NonNull final Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    /**
     * Return the type of array with the {@code type}.
     *
     * @param type The type.
     * @return the type of map with the {@code type}
     */
    public static Type getArrayType(@NonNull final Type type) {
        return TypeToken.getArray(type).getType();
    }

    /**
     * Return the type of {@code rawType} with the {@code typeArguments}.
     *
     * @param rawType       The raw type.
     * @param typeArguments The type of arguments.
     * @return the type of map with the {@code type}
     */
    public static Type getType(@NonNull final Type rawType, @NonNull final Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

    static Gson getGson4LogUtils() {
        Gson gson4LogUtils = GSONS.get(KEY_LOG_UTILS);
        if (gson4LogUtils == null) {
            gson4LogUtils = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            GSONS.put(KEY_LOG_UTILS, gson4LogUtils);
        }
        return gson4LogUtils;
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                })
                .create();
    }

    public static final class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
            final JsonObject wrapper = new JsonObject();
            wrapper.addProperty("type", object.getClass().getName());
            wrapper.add("data", context.serialize(object));
            return wrapper;
        }

        @Override
        public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject wrapper = (JsonObject) elem;
            final JsonElement typeName = get(wrapper, "type");
            final JsonElement data = get(wrapper, "data");
            final Type actualType = typeForName(typeName);
            return context.deserialize(data, actualType);
        }


        private Type typeForName(final JsonElement typeElem) {
            try {
                return Class.forName(typeElem.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        private JsonElement get(final JsonObject wrapper, String memberName) {
            final JsonElement elem = wrapper.get(memberName);
            if (elem == null)
                throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
            return elem;
        }
    }
}
