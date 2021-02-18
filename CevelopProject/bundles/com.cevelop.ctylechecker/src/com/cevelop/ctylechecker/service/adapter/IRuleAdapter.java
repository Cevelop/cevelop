package com.cevelop.ctylechecker.service.adapter;

import java.lang.reflect.Type;

import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IResolution;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.types.Rule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class IRuleAdapter implements JsonSerializer<IRule>, JsonDeserializer<IRule> {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(IConcept.class,
            new IConceptAdapter()).registerTypeAdapter(IResolution.class, new IResolutionAdapter()).registerTypeAdapter(IExpression.class,
                    new IExpressionAdapter()).create();

    @Override
    public JsonElement serialize(IRule src, Type arg1, JsonSerializationContext context) {
        return gson.toJsonTree(src);
    }

    @Override
    public IRule deserialize(JsonElement json, Type arg1, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        IRule rule = gson.fromJson(jsonObject, Rule.class);
        return rule;
    }
}
