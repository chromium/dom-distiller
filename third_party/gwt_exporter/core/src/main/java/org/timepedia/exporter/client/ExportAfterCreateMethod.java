package org.timepedia.exporter.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * Mark a static method in a class so as it will run after the class
 * has been created. 
 * It is run just once and it is useful to redefine name-spaces or 
 * whatever we needed to do after the class has been exported.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportAfterCreateMethod {
}