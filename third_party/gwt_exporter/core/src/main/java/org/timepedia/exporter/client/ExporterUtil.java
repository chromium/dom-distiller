package org.timepedia.exporter.client;

import java.util.Date;

import org.timepedia.exporter.client.ExporterBaseActual.JsArrayObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;

/**
 * Holds utility methods and wrapper state
 *
 * @author Ray Cromwell
 */
public class ExporterUtil {

  public static abstract class ExportAll implements Exportable, Exporter {
    public void export() {
      export(false);
    }
    public abstract void export(boolean all);
  }

  private static ExporterBaseImpl impl = GWT.create(ExporterBaseImpl.class);
  static {
  }

  public static JavaScriptObject declarePackage(String qualifiedExportName) {
    return impl.declarePackage(qualifiedExportName);
  }

  /**
   * Automatically export all instantiable and public classes marked with the Exportable
   * interface.
   * 
   * @param all, when this parameter is true it will export additionally all non-instantiable
   *  classes marked with Exportable
   */
  public static void export(boolean all) {
    ExportAll export = GWT.create(ExportAll.class);
    export.export(all);
  }
  
  /**
   * Export all classes marked with the Exportable interface, including those which are not default
   * instantiable (interfaces, abstracts, without constructor, etc).
   * 
   * Use ExporterUtil.export(false) to export just the set of instantiable classes and save some
   * js size, gwt-exporter will take care of exporting dependent classes when used. 
   * 
   */
  public static void exportAll() {
    export(true);
  }

//  public static void exportAllAsync() {
//    GWT.runAsync(new RunAsyncCallback() {
//      public void onFailure(Throwable reason) {
//        throw new RuntimeException(reason);
//      }
//
//      public void onSuccess() {
//        GWT.create(ExportAll.class);
//        onexport();
//      }
//
//      private native void onexport() /*-{
//        $wnd.onexport();
//      }-*/;
//    });
//  }
  
  public static JavaScriptObject runDispatch(Object instance, Class clazz, int meth,
      JsArray<JavaScriptObject> arguments, boolean isStatic, boolean isVarArgs) {
    return impl.runDispatch(instance, clazz, meth, arguments, isStatic, isVarArgs);
  }

  public static native byte getStructuralFieldbyte(JavaScriptObject jso,
      String field) /*-{
      return jso[field];
  }-*/;

  public static native char getStructuralFieldchar(JavaScriptObject jso,
      String field) /*-{
      return jso[field];
  }-*/;

  public static native double getStructuralFielddouble(JavaScriptObject jso,
      String field) /*-{
      return jso[field];
  }-*/;

  public static native float getStructuralFieldfloat(JavaScriptObject jso,
      String field) /*-{
      return jso[field];
  }-*/;

  public static native int getStructuralFieldint(JavaScriptObject jso,
      String field) /*-{
      return jso[field];
  }-*/;

  public static long getStructuralFieldlong(JavaScriptObject jso,
      String field) {
    return (long) getStructuralFielddouble(jso, field);
  }

  public static native <T> T getStructuralFieldObject(JavaScriptObject jso,
      String field) /*-{
      return jso[field];
  }-*/;

  public static native short getStructuralFieldshort(JavaScriptObject jso,
      String field) /*-{
      return jso[field];
  }-*/;

  public static void registerDispatchMap(Class clazz, JavaScriptObject dispMap,
      boolean isStatic) {
    impl.registerDispatchMap(clazz, dispMap, isStatic);
  }

  public static native void setStructuralField(JavaScriptObject jso,
      String field, Object val) /*-{
      jso[field]=type;
  }-*/;

  public static void setWrapper(Object instance, JavaScriptObject wrapper) {
    impl.setWrapper(instance, wrapper);
  }

  public static JavaScriptObject typeConstructor(Exportable type) {
    return impl.typeConstructor(type);
  }

  public static JavaScriptObject typeConstructor(String type) {
    return impl.typeConstructor(type);
  }
  
  public static JavaScriptObject wrap(Object type) {
    return impl.wrap(type);
  }
  
  public static JavaScriptObject wrap(JavaScriptObject[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(Object[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(Exportable[] type) {
    return wrap((Object[])type);
  }

  public static JavaScriptObject wrap(String[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(double[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(float[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(int[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(char[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(byte[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(long[] type) {
    return impl.wrap(type);
  }

  public static JavaScriptObject wrap(short[] type) {
    return impl.wrap(type);
  }
  
  public static JavaScriptObject wrap(Date[] type) {
    return impl.wrap(type);
  }

  public static String[] toArrString(JavaScriptObject type) {
    return impl.toArrString(type.<JsArrayString>cast());
  }
  
  public static double[] toArrDouble(JavaScriptObject type) {
    return impl.toArrDouble(type.<JsArrayNumber>cast());
  }
  
  public static float[] toArrFloat(JavaScriptObject type) {
    return impl.toArrFloat(type.<JsArrayNumber>cast());
  }
  
  public static int[] toArrInt(JavaScriptObject type) {
    return impl.toArrInt(type.<JsArrayNumber>cast());
  }
  
  public static byte[] toArrByte(JavaScriptObject type) {
    return impl.toArrByte(type.<JsArrayNumber>cast());
  }
  
  public static char[] toArrChar(JavaScriptObject type) {
    return impl.toArrChar(type.<JsArrayNumber>cast());
  }
  
  public static long[] toArrLong(JavaScriptObject type) {
    return impl.toArrLong(type.<JsArrayNumber>cast());
  }
  
  public static <T> T[] toArrObject(JavaScriptObject type, T[] ret) {
    return impl.toArrObject(type, ret);
  }
  
  public static Object[] toArrJsObject(JavaScriptObject type) {
    return impl.toArrJsObject(type);
  }
  
  public static Date[] toArrDate(JavaScriptObject type) {
    return impl.toArrDate(type);
  }
  
  // Although in Compiled mode we could cast an Exportable[] to any other T[] array
  // In hosted mode it is not possible, so we only support Exportable[] parameter
  public static Exportable[] toArrExport(JavaScriptObject type) {
    return impl.toArrExport(type);
  }
  
  public static JavaScriptObject unshift(Object o, JavaScriptObject arr) {
    return impl.unshift(o, arr);
  }
  
  public static JavaScriptObject dateToJsDate(Date d) {
    return impl.dateToJsDate(d);
  }
  
  public static Date jsDateToDate(JavaScriptObject jd) {
    return impl.jsDateToDate(jd);
  }
  
  public static int length(JavaScriptObject o) {
    return o.<JsArrayObject>cast().length();
  }
  
  public static Object gwtInstance(Object o) {
    return impl.gwtInstance(o);
  }
  
  public static <T extends Exporter> void addExporter(Class<?>c, T o) {
    impl.addExporter(c, o);
  }
  
}
