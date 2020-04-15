package com.github.srbarber1997.configuration.models;

import com.github.srbarber1997.configuration.ConfigurationModel;
import com.github.srbarber1997.configuration.serialise.SelfDeserializable;
import com.github.srbarber1997.configuration.serialise.SelfSerializable;
import com.google.gson.Gson;

@ConfigurationModel
public class SerialiserConfig implements SelfSerializable, SelfDeserializable {

    public boolean called = false;

    @Override
    public String serialise(Object obj) throws Exception {
        if (obj == this)
            called = true;
        return new Gson().toJson(obj);
    }

    @Override
    public Object deserialize(String objString, Class<?> objClass) throws Exception {
        called = true;
        return new Gson().fromJson(objString, SerialiserConfig.class);
    }
}
