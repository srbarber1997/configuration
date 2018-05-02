package org.bitbucket.srbarber1997.configuration.serialise;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class GsonSerialiserConfigBase
        implements SelfSerializable, SelfDeserializable {

    /**
     * Static final variable, so it won't serialise
     */
    private static final Gson GSON = new GsonBuilder()
                .setPrettyPrinting()
                .create();

    @Override
    public Object deserialize(String objString) {
        return GSON.fromJson(objString, Object.class);
    }

    @Override
    public String serialise(Object obj) {
        return GSON.toJson(obj);
    }
}
