package org.bitbucket.srbarber1997.configuration;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should go on the class of any Configuration Model
 * that will be used to serialize and deserialize Configurations. Types
 * that do not declare they are {@link ConfigurationModel}s will be ignored
 * by the {@link ConfigLoader}
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurationModel {

    /**
     * Location of the resource that should be used to instantiate
     * a default instance of this model. Only if no config file is found
     * in the {@link ConfigLoader#directory}
     * @see ClassLoader#getResourceAsStream(String)
     * @return string of the resource as an absolute reference
     */
    String defaultResource() default "";

    /**
     * The outcome of what will happen when the serializer fails
     * when operating on this type of config model
     * @return Outcome when an error occurs
     */
    Outcome onError() default Outcome.THROW_EXCEPTION;
}
