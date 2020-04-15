package com.github.srbarber1997.configuration.models;

import com.github.srbarber1997.configuration.ConfigurationModel;
import com.github.srbarber1997.configuration.serialise.GsonSerialiserConfigBase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ConfigurationModel
public class GsonSerialiserConfig extends GsonSerialiserConfigBase {

    public boolean called = false;

    @Override
    public String serialise(Object obj) {
        if (obj == this)
            called = true;
        return super.serialise(obj);
    }

    @Override
    public GsonBuilder serializer(GsonBuilder gsonBuilder) {
        return gsonBuilder;
    }

    @Override
    public Object deserialize(String objString, Class<?> objClass) {
        called = true;
        return super.deserialize(objString, objClass);
    }
}
