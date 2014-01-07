package org.timepedia.exporter.client;

import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;

/**
 * Methods used to maintain a mapping between JS types and Java (GWT) objects.
 */
public class ExporterBaseActual extends ExporterBaseImpl {
  
  public static final String WRAPPER_PROPERTY = "__gwtex_wrap";

  private native static JavaScriptObject wrap0(Object type,
      JavaScriptObject constructor) /*-{
    return constructor && ((typeof constructor == 'function')) ? new (constructor)(type) : type;
  }-*/;

  private HashMap<Class<?>, Exporter> exporterMap = new HashMap<Class<?>, Exporter>();
  private HashMap<Class<?>, JavaScriptObject> dispatchMap = new HashMap<Class<?>, JavaScriptObject>();
  private HashMap<Class<?>, JavaScriptObject> staticDispatchMap = new HashMap<Class<?>, JavaScriptObject>();

  //TODO: track garbage collected wrappers and remove mapping

  private IdentityHashMap<Object, JavaScriptObject> wrapperMap = null;

  public ExporterBaseActual() {
    if (!GWT.isScript()) {
      wrapperMap = new IdentityHashMap<Object, JavaScriptObject>();
    }
  }

  public <T extends Exporter> void addExporter(Class<?>c, T o) {
    exporterMap.put(c, o);
  }

  public JavaScriptObject typeConstructor(Object type) {
    JavaScriptObject jso = typeConstructor(type.getClass());
    if (jso == null) {
      for (Exporter e : exporterMap.values()) {
        if (e.isAssignable(type)) {
          jso = e.getJsConstructor();
          break;
        }
      }
    }
    return jso;
  }
  
  public JavaScriptObject typeConstructor(Class<?> clz) {
    Exporter e = exporterMap.get(clz);
    Class<?> sup = clz.getSuperclass();
    if (e == null && sup != null && sup != Object.class) {
      return typeConstructor(sup);
    }
    return e != null ? e.getJsConstructor() : null;
  }
  
  private JavaScriptObject getWrapper(Object type) {
    JavaScriptObject wrapper = null;
    if (!GWT.isScript()) {
      wrapper = wrapperMap.get(type);
    } else {
      wrapper = getWrapperJS(type, WRAPPER_PROPERTY);
    }

    if (wrapper == null) {
      wrapper = setWrapper(type);
    }
    return wrapper;
  }

  private static native JavaScriptObject reinterpretCast(Object nl) /*-{
    return nl;
  }-*/;
  
  private static native <T> T[] reinterpretArray(Object nl) /*-{
    return nl;
  }-*/;

  @Override
  public JavaScriptObject wrap(Date[] type) {
    JsArray<JavaScriptObject> ret = JavaScriptObject.createArray().cast();
    for (Date d : type) {
      ret.push(dateToJsDate(d));
    }
    return ret;
  }
  
  @Override
  public JavaScriptObject wrap(float[] type) {
    if (!GWT.isScript()) {
      if (type == null) {
        return null;
      }
      JsArrayNumber wrapperArray = JavaScriptObject.createArray().cast();
      for (int i = 0; i < type.length; i++) {
        wrapperArray.set(i, type[i]);
      }
      return wrapperArray;
    } else {
      return reinterpretCast(type);
    }
  }  

  @Override
  public JavaScriptObject wrap(byte[] type) {
    if (!GWT.isScript()) {
      if (type == null) {
        return null;
      }
      JsArrayNumber wrapperArray =  JavaScriptObject.createArray().cast();
      for (int i = 0; i < type.length; i++) {
        wrapperArray.set(i, type[i]);
      }
      return wrapperArray;
    } else {
      return reinterpretCast(type);
    }
  }

  @Override
  public JavaScriptObject wrap(char[] type) {
    if (!GWT.isScript()) {
      if (type == null) {
        return null;
      }
      JsArrayNumber wrapperArray =  JavaScriptObject.createArray().cast();
      for (int i = 0; i < type.length; i++) {
        wrapperArray.set(i, type[i]);
      }
      return wrapperArray;
    } else {
      return reinterpretCast(type);
    }
  }

  @Override
  public JavaScriptObject wrap(int[] type) {
    if (!GWT.isScript()) {
      if (type == null) {
        return null;
      }
      JsArrayNumber wrapperArray =  JavaScriptObject.createArray().cast();
      for (int i = 0; i < type.length; i++) {
        wrapperArray.set(i, type[i]);
      }
      return wrapperArray;
    } else {
      return reinterpretCast(type);
    }
  }

  @Override
  public JavaScriptObject wrap(long[] type) {
    if (type == null) {
      return null;
    }
    JsArrayNumber wrapperArray =  JavaScriptObject.createArray().cast();
    for (int i = 0; i < type.length; i++) {
      wrapperArray.set(i, type[i]);
    }
    return wrapperArray;
  }

  @Override
  public JavaScriptObject wrap(short[] type) {
    if (!GWT.isScript()) {
      if (type == null) {
        return null;
      }
      JsArrayNumber wrapperArray =  JavaScriptObject.createArray().cast();
      for (int i = 0; i < type.length; i++) {
        wrapperArray.set(i, type[i]);
      }
      return wrapperArray;
    } else {
      return reinterpretCast(type);
    }
  }

  @Override
  public JavaScriptObject wrap(double[] type) {
    if (!GWT.isScript()) {
      if (type == null) {
        return null;
      }
      JsArrayNumber wrapperArray =  JavaScriptObject.createArray().cast();
      for (int i = 0; i < type.length; i++) {
        wrapperArray.set(i, type[i]);
      }
      return wrapperArray;
    } else {
      return reinterpretCast(type);
    }
  }
  
  @Override
  public JavaScriptObject wrap(String[] type) {
    if (!GWT.isScript()) {
      if (type == null) {
        return null;
      }
      JsArrayString wrapperArray =  JavaScriptObject.createArray().cast();
      for (int i = 0; i < type.length; i++) {
        wrapperArray.set(i, type[i]);
      }
      return wrapperArray;
    } else {
      return reinterpretCast(type);
    }
  }

  @Override
  public JavaScriptObject wrap(Object type) {
    if (type == null) {
      return null;
    }
    return getWrapper(type);
  }

  @Override
  public JavaScriptObject wrap(Object[] type) {
    if (type == null) {
      return null;
    }
    JsArrayObject wrapperArray = JavaScriptObject.createArray().cast();
    for (int i = 0; i < type.length; i++) {
      wrapperArray.setObject(i, wrap(type[i]));
    }
    return wrapperArray;
  }

  public JavaScriptObject setWrapper(Object type) {
    if (type.getClass().isArray()) {
      // Create arrays for arrays, I wonder if javascript can have circular references..
      // I should probably implement a map from array to cast
      Object[] a = (Object[])type;
      JsArrayObject wrapperArray = JavaScriptObject.createArray().cast();
      for (int i = 0; i < a.length; i++) {
        wrapperArray.setObject(i, setWrapper(a[i]));
      }
      return wrapperArray;
    }
    JavaScriptObject cons = typeConstructor(type);
    assert cons != null : "No constructor for type: " + type.getClass().getName() + " " + type.getClass().getSuperclass(); 
    JavaScriptObject wrapper = wrap0(type, cons);
    setWrapper(type, wrapper);
    return wrapper;
  }
  
  @Override
  public void setWrapper(Object instance, JavaScriptObject wrapper) {
    if (GWT.isScript()) {
      setWrapperJS(instance, wrapper, WRAPPER_PROPERTY);
    } else {
      setWrapperHosted(instance, wrapper);
    }
  }

  public JavaScriptObject getWrapper(Exportable type) {
    JavaScriptObject wrapper = null;
    if (GWT.isScript()) {
      wrapper = getWrapperJS(type, WRAPPER_PROPERTY);
    } else {
      wrapper = wrapperMap.get(type);
    }
    return wrapper;
  }

  
  @Override
  public JavaScriptObject wrap(JavaScriptObject[] type) {
    if (type == null) {
      return null;
    }
    JsArray<JavaScriptObject> wrapperArray = JavaScriptObject.createArray().cast();
    for (int i = 0; i < type.length; i++) {
      wrapperArray.set(i, type[i]);
    }
    return wrapperArray;
  }
  
  @Override
  public Object gwtInstance(Object o) {
    Object g;
    return (o != null && o instanceof JavaScriptObject && (g = getGwtInstance((JavaScriptObject)o)) != null) ? g : o;
  }
  
  // JsArray.get() returns a JavaScriptObject, so we need this wrapper 
  // class to avoid a casting exception at runtime.
  public static class JsArrayObject extends JavaScriptObject {
    protected JsArrayObject(){}
    final public native <T> T getObject(int i) /*-{
      return this[i];
    }-*/;
    final public native <T> void setObject(int i, T o) /*-{
      this[i] = o;
    }-*/;
    final public native int length() /*-{
      return this.length;
    }-*/;
    final public Double getNumberObject(int i) {
      return getPrimitiveNumber(i);
    };
    final public Boolean getBooleanObject(int i) {
      return getPrimitiveBoolean(i);
    };
    final public native double getPrimitiveNumber(int i) /*-{
      return this[i];
    }-*/;
    final public native boolean getPrimitiveBoolean(int i) /*-{
      return this[i];
    }-*/;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArrObject(JavaScriptObject j, T[] ret) {
    // We can not use here reinterpretArray because we have to replace
    // the gwtInstance
    JsArrayObject s = j.cast();
    int l = s.length();
    for (int i = 0; i < l; i++) {
      Object o = s.getObject(i);
      if (o instanceof JavaScriptObject && getGwtInstance((JavaScriptObject)o) != null) {
        o = getGwtInstance((JavaScriptObject)o);
      }
      ret[i] = (T)o;
    }
    return ret;
  }
  
  @Override
  public JavaScriptObject[] toArrJsObject(JavaScriptObject p) {
    if (!GWT.isScript()) {
      JsArray<JavaScriptObject> s = p.cast();
      int l = s.length();
      JavaScriptObject[] ret = new JavaScriptObject[l];
      for (int i = 0; i < l; i++) {
        ret[i] = s.get(i);
      }
      return ret;
    } else {
      return reinterpretArray(p);
    }
  }
  
  public Exportable[] toArrExport(JavaScriptObject j) {
    JsArray<JavaScriptObject> s = j.cast();
    int l = s.length();
    Exportable[] ret = new Exportable[l];
    for (int i = 0; i < l; i++) {
      Object o = getGwtInstance(s.get(i));
      if (o == null) {
        o = s.get(i);
      }
      assert (o != null && (o instanceof Exportable));
      ret[i] = (Exportable) o;
    }
   return ret;
  }
  
  public String[] toArrString(JsArrayString s) {
    int l = s.length();
    String[] ret = new String[l];
    for (int i = 0; i < l; i++) {
      ret[i] = s.get(i);
    }
    return ret;
  }
  
  public long[] toArrLong(JsArrayNumber s) {
    int l = s.length();
    long[] ret = new long[l];
    for (int i = 0; i < l; i++) {
      ret[i] = (long)s.get(i);
    }
    return ret;
  }

  public double[] toArrDouble(JsArrayNumber s) {
    int l = s.length();
    double[] ret = new double[l];
    for (int i = 0; i < l; i++) {
      ret[i] = s.get(i);
    }
    return ret;
  }

  public int[] toArrInt(JsArrayNumber s) {
    int l = s.length();
    int[] ret = new int[l];
    for (int i = 0; i < l; i++) {
      ret[i] = (int)s.get(i);
    }
    return ret;
  }

  public byte[] toArrByte(JsArrayNumber s) {
    int l = s.length();
    byte[] ret = new byte[l];
    for (int i = 0; i < l; i++) {
      ret[i] = (byte)s.get(i);
    }
    return ret;
  }
  public char[] toArrChar(JsArrayNumber s) {
    int l = s.length();
    char[] ret = new char[l];
    for (int i = 0; i < l; i++) {
      ret[i] = (char)s.get(i);
    }
    return ret;
  }
  public float[] toArrFloat(JsArrayNumber s) {
    int l = s.length();
    float[] ret = new float[l];
    for (int i = 0; i < l; i++) {
      ret[i] = (long)s.get(i);
    }
    return ret;
  }
  public Date[] toArrDate(JavaScriptObject j) {
    JsArray<JavaScriptObject> s = j.cast();
    int l = s.length();
    Date[] ret = new Date[l];
    for (int i = 0; i < l; i++) {
      ret[i] = jsDateToDate(s.get(i));
    }
   return ret;
  }
  
  private native JsArray<JavaScriptObject> computeVarArguments(int len, JavaScriptObject args) /*-{
    var ret = [];
    for (i = 0; i < len - 1; i++) 
      ret.push(args[i]);
    var alen = args.length;
    var p = len - 1;
    if (alen >= len && Object.prototype.toString.apply(args[p]) === '[object Array]') {
        ret.push(args[p]);
    } else {
      var a = [];
      for (i = p; i < alen; i++) 
        a.push(args[i]);
      ret.push(a);  
    }
    return ret;
  }-*/;

  @Override
  public native JavaScriptObject unshift(Object o, JavaScriptObject arr) /*-{
    var ret = [o];
    for (i = 0; i<arr.length; i++) ret.push(arr[i]);
    return ret;
  }-*/;
  
  @Override
  public JavaScriptObject dateToJsDate(Date d) {
    return numberToJsDateObject(d.getTime());
  }

  @Override
  public Date jsDateToDate(JavaScriptObject jd) {
    return new Date((long)jsDateObjectToNumber(jd));
  }

  private native JavaScriptObject getWrapperJS(Object type, String wrapProp) /*-{
    return type[wrapProp];
  }-*/;

  private void setWrapperHosted(Object instance, JavaScriptObject wrapper) {
    wrapperMap.put(instance, wrapper);
  }

  private native void setWrapperJS(Object instance, JavaScriptObject wrapper,
      String wrapperProperty) /*-{
    instance[wrapperProperty] = wrapper;
  }-*/;

  private native void declarePackage0(JavaScriptObject prefix, String pkg) /*-{
    prefix[pkg] || (prefix[pkg] = {});
  }-*/;

  @Override
  public JavaScriptObject declarePackage(String qualifiedExportName) {
    String superPackages[] = qualifiedExportName.split("\\.");
    JavaScriptObject prefix = getWindow();
    int i = 0;
    for (int l = superPackages.length - 1; i < l ; i++) {
      if (!superPackages[i].equals("client")) {
        declarePackage0(prefix, superPackages[i]);
        prefix = getProp(prefix, superPackages[i]);
      }
    }
    // return the previous object stored in this name-space if any.
    JavaScriptObject o =  getProp(prefix, superPackages[i]);
    return o;
  }

  private static native JavaScriptObject getWindow() /*-{
    return $wnd;
  }-*/;

  private static native JavaScriptObject getProp(JavaScriptObject jso,
      String key) /*-{
    return jso != null ? jso[key] : null;
  }-*/;

  /**
   * Used for debuging 
   */
  private static native String dumpArguments(JavaScriptObject o) /*-{
    function dumpObj(obj, name, indent, depth) {
      if (depth > 4) return name + " 4 < depth=" + depth;
      if (typeof obj == "object") {
        var child = null;
        var output = indent + name + "\n";
        indent += "  ";
        for (var item in obj) {
           try {
             child = obj[item];
           } catch (e) {
             child = "<Unable to Evaluate>";
           }
           if (typeof child == "object") {
             output += dumpObj(child, item, indent, depth + 1);
           } else {
             output += indent + item + ": " + child + "\n";
           }
        }
        return output;
      } else {
        return "" + obj;
      }
    }    
    return dumpObj(o, 'arguments' , '', 1);
  }-*/;

  private JavaScriptObject runDispatch(Object instance, Map<Class<?>, JavaScriptObject> dmap,
      Class<?> clazz, int meth, JsArray<JavaScriptObject> arguments) {
    
    JsArray<SignatureJSO> sigs = getSigs(dmap.get(clazz).cast(), meth,
        arguments.length());
    JavaScriptObject jFunc = null;
    JavaScriptObject wFunc = null;
    JavaScriptObject aFunc = null;
    for (int i = 0, l = sigs == null ? 0 : sigs.length(); i < l; i++) {
      SignatureJSO sig = sigs.get(i);
      if (sig.matches(arguments)) {
        jFunc = sig.getFunction();
        wFunc = sig.getWrapperFunc();
        aFunc = sig.getWrapArgumentsFunc();
        break;
      }
    }
    if (jFunc == null) {
      return null;
    } else {
      arguments = aFunc != null ? wrapArguments(instance, aFunc, arguments) : arguments;
      JavaScriptObject r =  runDispatch(instance, jFunc, wFunc, arguments);
      return r;
    }
  }
  
  private static native JsArray<JavaScriptObject> wrapArguments(Object instance, JavaScriptObject wrapper, JavaScriptObject arguments) /*-{
    return wrapper(instance, arguments);
  }-*/;

  private static native JsArray<JavaScriptObject> runDispatch(Object instance, JavaScriptObject java, JavaScriptObject wrapper, JavaScriptObject arguments) /*-{
    var x = java.apply(instance, arguments);
    return [wrapper ? wrapper(x) : x];
  }-*/;
  
  @Override
  public JavaScriptObject runDispatch(Object instance, Class clazz, int meth,
      JsArray<JavaScriptObject> arguments, boolean isStatic, boolean isVarArgs) {
    
    Map<Class<?>, JavaScriptObject> dmap = isStatic ? staticDispatchMap : dispatchMap;
    if (isVarArgs) {
      for (int l = getMaxArity(dmap.get(clazz).cast(), meth), i = l; i >= 1; i--) {
        JsArray<JavaScriptObject> args = computeVarArguments(i, arguments);
        JavaScriptObject ret = runDispatch(instance, dmap, clazz, meth, args);
        if (ret == null) {
          // ExportInstanceMethod case
          args =  unshift(instance, args).cast();
          ret = runDispatch(instance, dmap, clazz, meth, args);
        }
        if (ret != null) {
          return ret;
        }
      }
    } else {
      JavaScriptObject ret = runDispatch(instance, dmap, clazz, meth, arguments);
      if (ret == null) {
        // ExportInstanceMethod case
        arguments = unshift(instance, arguments).cast();
        ret = runDispatch(instance, dmap, clazz, meth, arguments);
      }
      if (ret != null) {
        return ret;
      }
    }
    String s = ""; //dumpArguments(arguments);

    throw new RuntimeException(
        "Can't find exported method for given arguments: " + meth + ":" + arguments.length() + "\n" + s);
  }
  
  @SuppressWarnings("rawtypes")
  private static boolean isAssignableToClass(Object o, Class clazz) {
    if (Object.class.equals(clazz)) {
      return true;
    }
    if (Exportable.class.equals(clazz) && o instanceof Exportable) {
      return true;
    }
    if (o != null) {
      for (Class sup = o.getClass(); sup != null && sup != Object.class; sup = sup.getSuperclass()) {
        if (sup == clazz) {
          return true;
        }
      }
    }
    return false;
  }

  private native JsArray<SignatureJSO> getSigs(JavaScriptObject jsoMap,
      int meth, int arity) /*-{
    return jsoMap[meth][arity];
  }-*/;
  
  private native int getMaxArity(JavaScriptObject jsoMap,
      int meth) /*-{
      var o = jsoMap[meth];
      var r = 0;
      for (k in o) r = Math.max(r, k);
      return r;
  }-*/;
  
  private static native JavaScriptObject numberToJsDateObject(double time) /*-{
    return new Date(time);
  }-*/;

  private static native double jsDateObjectToNumber(JavaScriptObject d) /*-{
    return (d && d.getTime) ? d.getTime(): 0;
  }-*/;
  
  private static native <T> void putObject(JavaScriptObject o, int index, T val) /*-{
    o[index] = val;
  }-*/;

  private static native double getNumber(JavaScriptObject o, int index) /*-{
    return o[index];
  }-*/;

  @Override
  public void registerDispatchMap(Class clazz, JavaScriptObject dispMap,
      boolean isStatic) {
    HashMap<Class<?>, JavaScriptObject> map = isStatic ? staticDispatchMap : dispatchMap;
    JavaScriptObject jso = map.get(clazz);
    if (jso == null) {
      jso = dispMap;
    } else  {
      mergeJso(jso, dispMap);
    }
    map.put(clazz, jso);
  }
  
  private final static native Object getGwtInstance(JavaScriptObject o) /*-{
    // g must match ClassExporter.GWT_INSTANCE
    return o && o.g ? o.g : null;
  }-*/;
  
  private static native void mergeJso(JavaScriptObject o1, JavaScriptObject o2) /*-{
    for(p in o2) {o1[p] = o2[p];}
  }-*/;

  final public static class SignatureJSO extends JavaScriptObject {

    protected SignatureJSO() {
    }
    
    public boolean matches(JsArray<JavaScriptObject> arguments) {
      // add argument matching logic
      // add structural type checks
      for (int i = 0, l = arguments.length(); i < l; i++) {

        // The signature saved in the Dispatch table
        Object jsType = getJsTypeObject(i + 3);
        // The js type of the argument passed (number, boolean, string, object, array)
        String argJsType = typeof(arguments, i);
        
        // number, boolean, string and arrays
        if (argJsType.equals(jsType)){
          continue;
        }
        // accept nulls for strings
        if ("string".equals(jsType) && "null".equals(argJsType)) {
          continue;
        }

        boolean isNumber = "number".equals(argJsType);
        boolean isBoolean = "boolean".equals(argJsType);

        // when the signature is Object anything is valid, but we
        // have to replace primitives types by the appropriate object
        if (Object.class.equals(jsType)) {
          if (isNumber) {
            putObject(arguments, i, arguments.<JsArrayObject>cast().getNumberObject(i));
          }
          if (isBoolean) {
            putObject(arguments, i, arguments.<JsArrayObject>cast().getBooleanObject(i));
          }          
          continue;
        }

        boolean isPrimitive = isNumber || isBoolean;
        boolean isClass = !isPrimitive && jsType != null && jsType.getClass().equals(Class.class);
        
        // Deal with complex objects
        if (isClass) {
          Object o = arguments.<JsArrayObject>cast().getObject(i);
          if (o == null || isAssignableToClass(o, (Class)jsType)){
            continue;
          }
          if (o instanceof JavaScriptObject) {
            Object gwt = getGwtInstance((JavaScriptObject)o);
            if (gwt != null) {
              if (isAssignableToClass(gwt, (Class)jsType)){
                putObject(arguments, i, gwt);
                continue;
              }
            }
          }
        }
        
        if ("object".equals(jsType) && !isNumber && !isBoolean) {
          continue;
        }
        
        return false;
      }
      return true;
    }
    
    public native static String typeof(JavaScriptObject args, int i) /*-{
      var o = args[i];
      var t =  o == null ? 'null' : typeof(o);
      if (t == 'object') {
        return Object.prototype.toString.call(o) == '[object Array]' 
          || typeof o.length == 'number' ? 'array' : t;
      }
      return t
    }-*/;

    public native Object getJsTypeObject(int i) /*-{
      return this[i];
    }-*/;

    public native JavaScriptObject getFunction() /*-{
      return this[0];
    }-*/;

    public native JavaScriptObject getWrapperFunc() /*-{
      return this[1];
    }-*/;
    
    public native JavaScriptObject getWrapArgumentsFunc() /*-{
      return this[2];
    }-*/;
  }
}
