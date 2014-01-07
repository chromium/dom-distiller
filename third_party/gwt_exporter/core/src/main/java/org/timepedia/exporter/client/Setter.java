package org.timepedia.exporter.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a setter for a writable property  
 * its Javascript name to be overriden else the default java convention will be used (get/set/is removed and lowercase)
 * 
 * @see Getter for also generating the get method for a read/write property
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Setter {

  String value() default "";

  boolean all() default false;
}
