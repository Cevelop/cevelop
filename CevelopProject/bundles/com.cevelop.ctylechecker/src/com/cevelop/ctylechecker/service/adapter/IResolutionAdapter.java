package com.cevelop.ctylechecker.service.adapter;

import java.lang.reflect.Type;

import com.cevelop.ctylechecker.domain.IResolution;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


// Inspired from: https://www.javacodegeeks.com/2012/04/json-with-gson-and-abstract-classes.html
public class IResolutionAdapter implements JsonSerializer<IResolution>, JsonDeserializer<IResolution> {

    @Override
    public JsonElement serialize(IResolution src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }

    @Override
    public IResolution deserialize(JsonElement json, Type arg1, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");
        try {
            return context.deserialize(element, Class.forName("com.cevelop.ctylechecker.domain.types." + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }

    }

}
