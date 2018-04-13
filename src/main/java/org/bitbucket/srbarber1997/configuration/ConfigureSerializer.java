package org.bitbucket.srbarber1997.configuration;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that is used to mark a method as configuring
 * the serializer object used to load and save. Each method will be
 * will be called before serializing or de-serializing.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigureSerializer {
}
