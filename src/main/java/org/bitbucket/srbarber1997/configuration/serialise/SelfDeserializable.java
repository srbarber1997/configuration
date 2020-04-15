package org.bitbucket.srbarber1997.configuration.serialise;

import org.bitbucket.srbarber1997.configuration.ConfigLoader;

public interface SelfDeserializable {

    /**
     * Method that is called when an object needs to be loaded
     * @see ConfigLoader#configure()
     * @param objString representation of an object
     * @param objClass of the object represented in the string
     * @return a instance of the objClass
     * @throws Exception if the string cannot be deserialized
     */
    Object deserialize(String objString, Class<?> objClass) throws Exception;
}
