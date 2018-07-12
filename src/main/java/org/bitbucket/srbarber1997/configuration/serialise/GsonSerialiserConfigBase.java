package org.bitbucket.srbarber1997.configuration.serialise;

import com.google.gson.GsonBuilder;

/**
 * An abstract class used if you want to use the Gson serializer
 * but don't want it 'as is'. This is a convenience class that
 * allows you to customise the gson serializer without having to
 * implement {@link SelfSerializable}.
 */
public abstract class GsonSerialiserConfigBase
        implements SelfSerializable, SelfDeserializable {

    @Override
    public Object deserialize(String objString, Class<?> objClass) {
        return this.serializer(new GsonBuilder()).create().fromJson(objString, objClass);
    }

    @Override
    public String serialise(Object obj) {
        return this.serializer(new GsonBuilder()).create().toJson(obj);
    }

    /**
     * Method that is overwritten by extending classes used to get the
     * gson builder object that can be used to create the object to
     * serialise and deserialize the a given object
     * @param gsonBuilder object
     * @return a gsonBuilder object that will be used to create a gson serializer
     */
    public abstract GsonBuilder serializer(GsonBuilder gsonBuilder);
}
