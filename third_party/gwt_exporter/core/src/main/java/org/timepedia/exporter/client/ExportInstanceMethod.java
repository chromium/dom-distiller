package org.timepedia.exporter.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * Mark a static method in a class annotated with ExportOverlay to
 * be exported as an instance method of the original class.
 * The first argument of the method must be the instance of the original class.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportInstanceMethod {
  String value() default "";
}