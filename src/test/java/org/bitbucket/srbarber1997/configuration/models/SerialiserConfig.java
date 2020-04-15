package org.bitbucket.srbarber1997.configuration.models;

import com.google.gson.Gson;
import org.bitbucket.srbarber1997.configuration.ConfigurationModel;
import org.bitbucket.srbarber1997.configuration.serialise.SelfDeserializable;
import org.bitbucket.srbarber1997.configuration.serialise.SelfSerializable;

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
