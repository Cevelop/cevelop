package com.cevelop.ctylechecker.service.adapter;

import java.lang.reflect.Type;

import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.domain.types.Configuration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class IConfigurationAdapter implements JsonSerializer<IConfiguration>, JsonDeserializer<IConfiguration> {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(IStyleguide.class,
            new IStyleguideAdapter()).create();

    @Override
    public IConfiguration deserialize(JsonElement json, Type pType, JsonDeserializationContext pContext) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        IConfiguration configuration = gson.fromJson(jsonObject, Configuration.class);
        return configuration;
    }

    @Override
    public JsonElement serialize(IConfiguration pSource, Type pType, JsonSerializationContext pContext) {
        return gson.toJsonTree(pSource);
    }

}
