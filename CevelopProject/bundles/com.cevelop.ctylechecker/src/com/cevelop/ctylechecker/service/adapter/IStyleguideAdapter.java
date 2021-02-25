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

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.domain.types.Styleguide;


public class IStyleguideAdapter implements JsonSerializer<IStyleguide>, JsonDeserializer<IStyleguide> {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(IStyleguide.class,
            new IStyleguideAdapter()).registerTypeAdapter(IGrouping.class, new IGroupingAdapter()).registerTypeAdapter(IRule.class,
                    new IRuleAdapter()).create();

    @Override
    public IStyleguide deserialize(JsonElement json, Type pType, JsonDeserializationContext pContext) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        IStyleguide styleguide = gson.fromJson(jsonObject, Styleguide.class);
        return styleguide;
    }

    @Override
    public JsonElement serialize(IStyleguide pSource, Type pType, JsonSerializationContext pContext) {
        return gson.toJsonTree(pSource);
    }

}
