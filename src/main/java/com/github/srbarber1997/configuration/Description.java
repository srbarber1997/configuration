package com.github.srbarber1997.configuration;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to give a description to a
 * {@link ConfigurationModel} field. The description
 * will be shown in the generated .config file above
 * the json field.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Description {

    /**
     * The string that will be a description for the
     * field that is annotated
     * @return string that will be displayed
     */
    String value();
}
