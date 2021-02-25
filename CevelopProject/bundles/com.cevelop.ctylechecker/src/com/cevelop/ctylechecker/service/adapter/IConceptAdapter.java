package com.cevelop.ctylechecker.service.adapter;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.types.Concept;


public class IConceptAdapter implements JsonSerializer<IConcept>, JsonDeserializer<IConcept> {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public JsonElement serialize(IConcept src, Type arg1, JsonSerializationContext context) {
        return gson.toJsonTree(src);
    }

    @Override
    public IConcept deserialize(JsonElement json, Type arg1, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        IConcept concept = gson.fromJson(jsonObject, Concept.class);
        return concept;
    }
}
