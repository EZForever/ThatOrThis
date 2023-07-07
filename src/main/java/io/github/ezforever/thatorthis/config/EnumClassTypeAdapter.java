package io.github.ezforever.thatorthis.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.ezforever.thatorthis.internal.BidirectionalMap;

import java.lang.reflect.Type;

// T - The parent type to (de)serialize, U - The enum containing child classes' types
// e.g. EnumClassTypeAdapter<Option, OptionTypes>
public class EnumClassTypeAdapter<T>
        implements JsonSerializer<T>, JsonDeserializer<T> {
    private static final String TYPE_NAME = "type";

    // ---

    private final BidirectionalMap<String, Class<? extends T>> nameClassMap;

    public <U extends Enum<U> & EnumClassType<T>> EnumClassTypeAdapter(Class<U> classOfU) {
        this.nameClassMap = new BidirectionalMap<>();
        for(U value : classOfU.getEnumConstants())
            this.nameClassMap.put(value.name(), value.getClazz());
    }

    // --- Implements JsonSerializer<>

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        // NOTE: Assumes target type serializes into a JSON object
        JsonObject elem = context.serialize(src).getAsJsonObject();
        elem.addProperty(TYPE_NAME, nameClassMap.inverse().get(src.getClass()));
        return elem;
    }

    // --- Implements JsonDeserializer<>

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String typeName = jsonObject.getAsJsonPrimitive(TYPE_NAME).getAsString();
        Class<? extends T> realTypeOfT = nameClassMap.get(typeName);
        return context.deserialize(jsonObject, realTypeOfT);
    }
}
