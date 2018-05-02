package org.bitbucket.srbarber1997.configuration.serialise;

public interface SelfDeserializable {
    Object deserialize(String objString) throws Exception;
}
