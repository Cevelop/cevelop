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

import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IResolution;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.types.Grouping;


public class IGroupingAdapter implements JsonSerializer<IGrouping>, JsonDeserializer<IGrouping> {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(IGrouping.class,
            new IGroupingAdapter()).registerTypeAdapter(IResolution.class, new IResolutionAdapter()).registerTypeAdapter(IExpression.class,
                    new IExpressionAdapter()).registerTypeAdapter(IRule.class, new IRuleAdapter()).create();

    @Override
    public IGrouping deserialize(JsonElement json, Type pType, JsonDeserializationContext pContext) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        IGrouping grouping = gson.fromJson(jsonObject, Grouping.class);
        return grouping;
    }

    @Override
    public JsonElement serialize(IGrouping pSource, Type pType, JsonSerializationContext pContext) {
        return gson.toJsonTree(pSource);
    }

}
