package org.bitbucket.srbarber1997.configuration;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that specifies a field as a configuration. The field
 * will then be loaded and saved for you between runtime. The field
 * must have a type of something annotated with {@link ConfigurationModel}
 * to be saved correctly. Configs will be loaded when {@link ConfigLoader#load()}
 * is called and then saved when {@link ConfigLoader#save()} is called
 * @see ConfigLoader
 * @see ConfigurationModel
 * @author srbarber1997
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configuration {

    /**
     * Name property of the Configuration, the name is used
     * to identify the resource not the configuration. Two
     * configuration annotations with the same name will
     * reference the same object.
     * The only property that is required by the user.
     *
     *
     * Names that use forward slashes will make a directory
     * in the root {{@link ConfigLoader#directory}.
     *
     * For example, a name like:
     * "myConfig" will create a config file called "myConfig.config"
     * in the root directory
     * Making a path like: {root}/myConfig.config
     *
     * However, a name like:
     * "myDir/myConfig" will create a config file called myConfig.config
     * inside the directory {root}/myDir
     * Making a path like: {root}/myDir/myConfig.config
     * @return string name of the config
     */
    String name();

    /**
     * Readable property of the configuration. This property
     * indicates to the loader that when serialising the object
     * it should be made readable or not.
     * This property is optional and will default to being readable
     * @return boolean saying if the json object stored should
     * be readable to a viewer of the config file
     */
    boolean readable() default true;
}
