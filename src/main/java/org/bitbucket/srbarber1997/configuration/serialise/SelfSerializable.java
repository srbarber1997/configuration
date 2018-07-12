package org.bitbucket.srbarber1997.configuration.serialise;

import org.bitbucket.srbarber1997.configuration.ConfigLoader;
import org.bitbucket.srbarber1997.configuration.ConfigurationModel;

import java.io.Serializable;

/**
 * Interface that should be implemented by all {@link ConfigurationModel}s
 * that want to define their own way of serialising themselves.
 */
public interface SelfSerializable extends Serializable {

    /**
     * Method used to serialise this object when {@link ConfigLoader#save()}
     * @param obj to be serialised. For the implementing class it would be {@code this}
     *            although 'obj' should be used NOT 'this'
     * @return string representation of the given object
     * @throws Exception if the object could not be serialised
     */
    String serialise(Object obj) throws Exception;
}
