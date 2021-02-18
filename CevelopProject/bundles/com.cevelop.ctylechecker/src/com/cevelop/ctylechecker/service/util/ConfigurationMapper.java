package com.cevelop.ctylechecker.service.util;

import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IResolution;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.service.adapter.IConfigurationAdapter;
import com.cevelop.ctylechecker.service.adapter.IExpressionAdapter;
import com.cevelop.ctylechecker.service.adapter.IGroupingAdapter;
import com.cevelop.ctylechecker.service.adapter.IResolutionAdapter;
import com.cevelop.ctylechecker.service.adapter.IRuleAdapter;
import com.cevelop.ctylechecker.service.adapter.IStyleguideAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class ConfigurationMapper {

    private static final Gson gson = new GsonBuilder() //
            .excludeFieldsWithoutExposeAnnotation() //
            .setPrettyPrinting() //
            .registerTypeAdapter(IConfiguration.class, new IConfigurationAdapter()) //
            .registerTypeAdapter(IStyleguide.class, new IStyleguideAdapter()) //
            .registerTypeAdapter(IGrouping.class, new IGroupingAdapter()) //
            .registerTypeAdapter(IRule.class, new IRuleAdapter()) //
            .registerTypeAdapter(IResolution.class, new IResolutionAdapter()) //
            .registerTypeAdapter(IExpression.class, new IExpressionAdapter()) //
            .create();

    public static String toJson(Object pSource) {
        return gson.toJson(pSource);
    }

    public static <T> T fromJson(String pJson, Class<T> clazz) {
        return gson.fromJson(pJson, clazz);
    }

    public static JsonElement toJsonTree(Object src) {
        return gson.toJsonTree(src);
    }

    public static <T> T fromJson(JsonObject jsonObject, Class<T> clazz) {
        return gson.fromJson(jsonObject, clazz);
    }
}
