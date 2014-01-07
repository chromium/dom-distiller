package org.timepedia.exporter.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a getter for a readable property  
 * its Javascript name to be overriden else the default java convention will be used (get/set/is removed and lowercase)
 * 
 * @see Setter for also generating the set method for a writeable property
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Getter {

  String value() default "";

  boolean all() default false;
}
