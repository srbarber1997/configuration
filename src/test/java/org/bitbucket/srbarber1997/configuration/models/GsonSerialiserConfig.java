package org.bitbucket.srbarber1997.configuration.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bitbucket.srbarber1997.configuration.ConfigurationModel;
import org.bitbucket.srbarber1997.configuration.serialise.GsonSerialiserConfigBase;
import org.bitbucket.srbarber1997.configuration.serialise.SelfDeserializable;
import org.bitbucket.srbarber1997.configuration.serialise.SelfSerializable;

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
