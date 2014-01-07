package org.timepedia.exporter.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * This class performs the generation of export methods for a single class
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ClassExporter {

  private TreeLogger logger;

  private GeneratorContext ctx;

  private ExportableTypeOracle xTypeOracle;

  private SourceWriter sw;

  private ArrayList<JExportableClassType> exported;

  private HashSet<String> overloadExported = new HashSet<String>();

  private HashSet<String> visited;
  
  // These constants had the values: arg_ and __gwt_instance, but
  // having just a letter decreases the final size of javascript code in large classes.
  public static final String ARG_PREFIX = "a";
  public static final String GWT_INSTANCE = "g";

  public ClassExporter(TreeLogger logger, GeneratorContext ctx) {
    this(logger, ctx, new HashSet<String>());
  }

  public ClassExporter(TreeLogger logger, GeneratorContext ctx,
      HashSet<String> visited) {
    this.logger = logger;
    this.ctx = ctx;
    // a type oracle that can answer questions about whether types are
    // exportable
    xTypeOracle = new ExportableTypeOracle(ctx.getTypeOracle(), logger);
    this.visited = visited;
    exported = new ArrayList<JExportableClassType>();
  }
  
  private static boolean debug = "true".equalsIgnoreCase(System.getProperty("gwt.exporter.debug"));
  
  private void log(String txt) {
    logger.log(debug ? TreeLogger.INFO : TreeLogger.TRACE, txt);
  }

  /**
   * This method generates an implementation of the specified interface that
   * accepts a JavaScriptObject in its constructor containing a callback. It
   * then delegates the single-method of the interface to this callback. <p/>
   * For example: <p/> <p/> / ** * @gwt.exportClosure * / public interface
   * ClickListener implements Exportable { public void onClick(Sender s); } <p/>
   * generates a delegation class <p/> public class ClickListenerImpl implements
   * Exporter, ClickListener { <p/> private JavaScriptObject jso; public
   * ClickListenerClosure(JavaScriptObject jso) { this.jso = jso; } <p/> public
   * void onClick(Sender s) { invoke(jso, ExporterBase.wrap(s)); } <p/> public
   * native void invoke(JavaScriptObject closure, JavascriptObject s) {
   * closure(s); } <p/> }
   */
  public void exportClosure(JExportableClassType requestedType)
      throws UnableToCompleteException {

    if (requestedType == null) {
      logger.log(TreeLogger.ERROR, "exportClosure: requestedType is null", null);
      throw new UnableToCompleteException();
    }

    // get the name of the Java class implementing Exporter
    String genName = requestedType.getExporterImplementationName();

    sw.indent();

    // wrapped javascript function
    sw.println("private " + ExportableTypeOracle.JSO_CLASS + " jso;");
    sw.println();
    
    // equals method compares the wrapped javascript
    sw.println("public boolean equals(Object obj) {");
    sw.indent();
    sw.println("return obj != null && obj instanceof " + genName
        + " && jso.equals(((" + genName + ")obj).jso);");
    sw.outdent();
    sw.println("}");
    
    // Constructor used to export the class
    sw.println("public " + genName + "() { export(); }");

    // Main constructor
    sw.println(
        "public " + genName + "(" + ExportableTypeOracle.JSO_CLASS + " jso) {");
    sw.indent();
    sw.println("export();");
    sw.println("this.jso = jso;");
    if (requestedType.isStructuralType()) {
      sw.println("___importStructuralType();");
    }
    sw.outdent();
    sw.println("}");
    sw.println();

    // export static factory method
    sw.println("public static " + genName + " makeClosure("
        + ExportableTypeOracle.JSO_CLASS + " closure) {");
    sw.indent();
    sw.println("return new " + genName + "(closure);");
    sw.outdent();
    sw.println("}");
    sw.println();

    for (JExportableMethod method: requestedType.getExportableMethods()) {
      JExportableType retType = method.getExportableReturnType(true);
      if (retType != null && retType.needsExport() && !exported
          .contains(retType.getQualifiedSourceName())) {
        if (exportDependentClass(retType.getQualifiedSourceName())) {
          exported.add((JExportableClassType) retType);
        }
      }
      exportDependentParams(method);

      boolean isVoid = retType != null && retType.getQualifiedSourceName().equals("void");
      boolean isBoolean = !isVoid && retType != null && retType.getQualifiedSourceName().equals("boolean");
      boolean isPrimitive = !isVoid && retType != null && retType instanceof JExportablePrimitiveType;
      boolean hasParams = method.getExportableParameters().length > 0;
      
      // We use the first JavascriptObject in the function for applying the closure
      String apply = "null";
      for (int i = 0, l = method.getExportableParameters().length; i < l ; i++) {
        if (xTypeOracle.isJavaScriptObject(method.getExportableParameters()[i].getParam().getType())) {
          apply = ARG_PREFIX + i;
          break;
        }
      }
      
      String rType = retType == null ? "Object" : retType.getQualifiedSourceName();
      String mName = method.getName();
      
      sw.print("public " + rType);

      sw.print(" " + mName + "(");
      declareParameters(method, -1, true);
      sw.println(") {");
      sw.indent();
      sw.print((isVoid ? "" : "return ") + "invoke_" + mName + "(jso " + (hasParams ? "," : ""));
      declareJavaPassedValues(method, false);
      sw.println(");");
      sw.outdent();
      sw.println("}");
      sw.print("public native " + rType);
      sw.print(" invoke_" + mName + "(" + ExportableTypeOracle.JSO_CLASS + " closure");
      if (method.getExportableParameters().length > 0) {
        sw.print(", ");
      }

      declareParameters(method, -1, true);
      sw.println(") /*-{");
      sw.indent();
      sw.print((!isVoid ? "var r = " : "") + "closure.apply(" + apply + " ,[");
      declareJavaPassedValues(method, true);
      sw.println("]);");
      boolean isArray = retType != null && retType instanceof JExportableArrayType;
      if (retType != null && retType.needsExport() && !isVoid && !isArray) {
        sw.println("return r == undefined ? null : r != null ? r.instance : r");
      } else if (isBoolean) {
        sw.println("return !!r;");
      } else if (isPrimitive) {
        sw.println("return r && (typeof r == 'number') ? r: 0;");
      } else if (!isVoid) {
        sw.println("return r;");
      }
      sw.outdent();
      sw.println("}-*/;");
      sw.println();
    }
    sw.outdent();
  }

  /**
   * This method generates an implementation class that implements Exporter and
   * returns the fully qualified name of the class.
   */
  public String exportClass(String requestedClass, boolean export)
      throws UnableToCompleteException {
    
    // JExportableClassType is a wrapper around JClassType
    // which provides only the information and logic neccessary for
    // the generator
    JExportableClassType requestedType = xTypeOracle
        .findExportableClassType(requestedClass);

    if (requestedType == null) {
      logger.log(TreeLogger.ERROR,
          "Type '" + requestedClass + "' does not implement Exportable", null);
      throw new UnableToCompleteException();
    }
    
    log(requestedClass);
    log("  " + requestedType.getJSQualifiedExportName());
    
    if (debug) {
      System.out.println(">> Exporting : " + requestedClass);
      System.out.println("    getJSExportPackage: " + requestedType.getJSExportPackage());
      System.out.println("    getJSExportName: " + requestedType.getJSExportName());
      System.out.println("    getJSQualifiedExportName: " + requestedType.getJSQualifiedExportName());
      System.out.println("    getQualifiedExporterImplementationName: " + requestedType.getQualifiedExporterImplementationName());
      System.out.println("    getQualifiedSourceName: " + requestedType.getQualifiedSourceName());
    }


    // add this so we don't try to recursively reexport ourselves later
    exported.add(requestedType);
    visited.add(requestedType.getQualifiedSourceName());
    
    // get the name of the Java class implementing Exporter
    String genName = requestedType.getExporterImplementationName();

    // get the package name of the Exporter implementation
    String packageName = requestedType.getPackageName();

    // get a fully qualified reference to the Exporter implementation
    String qualName = requestedType.getQualifiedExporterImplementationName();
    
    boolean isClosure = xTypeOracle.isClosure(requestedType);
    String superClass = xTypeOracle.isStructuralType(requestedType.getType())
        ? requestedClass : xTypeOracle.isExportAll(requestedClass) ? "ExporterUtil.ExportAll" : null;

    // try to construct a sourcewriter for the qualified name
    if (isClosure) {
      String rtype = requestedType.getQualifiedSourceName();
      if (requestedType.getType().isInterface() != null) {
        sw = getSourceWriter(logger, ctx, packageName, genName, null, "Exporter", rtype);
      } else {
        sw = getSourceWriter(logger, ctx, packageName, genName, rtype, "Exporter");
      }
    } else {
      sw = getSourceWriter(logger, ctx, packageName, genName, superClass, "Exporter");
    }
    if (sw == null) {
      return qualName; // null, already generated
    }

    if (export && xTypeOracle.isExportAll(requestedClass)) {
      exportAll(genName);
    } else if (export) {
      if (isClosure) {
        exportClosure(requestedType);
      } else if (requestedType.isStructuralType()) {
        exportStructuralTypeConstructor(genName, requestedType);
      }

      if (requestedType.isStructuralType()) {
        exportStructuralTypeImporter(requestedType);
        exportStructuralTypeMatchMethod(requestedType);
      }

      sw.indent();
      
      // Create static wrapper methods to execute java methods which can not be 
      // exported as JSNI functions, like methods with 'long' arguments 
      createStaticWrappers(requestedType);

      if (!isClosure) {
        sw.println("public " + genName + "() { export(); }");
      }
      // here we define a JSNI Javascript method called export0()
      sw.println("public native void export0() /*-{");
      sw.indent();

      // if not defined, we create a Javascript package hierarchy
      // foo.bar.baz to hold the Javascript bridge
      declarePackages(requestedType);
      
      // export Javascript constructors
      exportConstructor(requestedType);
      
      // export all static fields
      exportFields(requestedType);
      
      // export all exportable methods
      exportMethods(requestedType);

      // restore inner class namespace
      sw.println("\nif(pkg) for (p in pkg) if ($wnd."
          + requestedType.getJSQualifiedExportName()
          + "[p] === undefined) $wnd."
          + requestedType.getJSQualifiedExportName() + "[p] = pkg[p];");
      
      if (requestedType.getAfterCreateMethod() != null) {
        sw.println("@" + requestedType.getAfterCreateMethod().getJSNIReference() + "();");
      }
      
      exportProperties(requestedType);
      
      sw.outdent();
      sw.println("}-*/;");

      // the Javascript constructors refer to static factory methods
      // on the Exporter implementation, referenced via JSNI
      // We generate them here
      if (xTypeOracle.isInstantiable(requestedType.getTypeToExport())) {
        exportStaticFactoryConstructors(requestedType);
      }

      // if this class is a structural type, generate overrides for every
      // structure type field
      if (requestedType.isStructuralType()) {
        exportStructuralTypeFields(requestedType);
      }

      // finally, generate the Exporter.export() method
      // which invokes recursively, via GWT.create(),
      // every other Exportable type we encountered in the exported ArrayList
      // ending with a call to export0()

      genExportMethod(requestedType, exported);
    } else {
      sw.indent();
      sw.println("public void export() {}");
    }
    
    sw.println("public boolean isAssignable(Object o) {");
    sw.indent();
    sw.println("return o != null && o instanceof " + requestedType.getQualifiedSourceName() + ";");
    sw.outdent();
    sw.println("}");
    
    sw.println("public native JavaScriptObject getJsConstructor() /*-{");
    sw.indent();
    sw.println("return $wnd." + requestedType.getJSQualifiedExportName() + ";");
    sw.outdent();
    sw.println("}-*/;");
    
    sw.outdent();

    sw.commit(logger);

    // return the name of the generated Exporter implementation class
    return qualName;
  }

  private void exportStructuralTypeMatchMethod(
      JExportableClassType requestedType) throws UnableToCompleteException {
    sw.println("public static native boolean ___match(JavaScriptObject jso) /*-{");
    sw.indent();
    sw.print("return ");
    for (JStructuralTypeField field : requestedType.getStructuralTypeFields()) {

      JExportableType eType = field.getExportableType();
      if (eType == null) {
        logger.log(TreeLogger.ERROR,
            "Structural type field " + field.getMethodName() + " for class "
                + requestedType.getQualifiedSourceName()
                + " is not exportable.");
        throw new UnableToCompleteException();
      }
      if (eType instanceof JExportableClassType) {
        JExportableClassType cType = (JExportableClassType) field
            .getExportableType();
        if (cType.needsExport() && cType.isStructuralType()) {
          sw.print("(jso." + field.getName() + " && @"
              + ((JExportableClassType) eType)
              .getQualifiedExporterImplementationName()
              + "::___match(Lcom/google/gwt/core/client/JavaScriptObject;)(jso."
              + field.getName() + ") &&");
        } else if (cType.needsExport()) {
          sw.print("(jso." + field.getName() + " && jso." + field.getName()
              + ".__gwt__instance) && ");
        } else if(!cType.needsExport()) {
          sw.print(
            "typeof(jso." + field.getName() + ") == '" + eType.getJsTypeOf()
                + "' && ");
        }
      } else if (eType instanceof JExportablePrimitiveType) {
        sw.print(
            "typeof(jso." + field.getName() + ") == '" + eType.getJsTypeOf()
                + "' && ");
      }
    }
    sw.println("true;");
    sw.outdent();
    sw.println("}-*/;");
  }

  private void exportStructuralTypeImporter(
      JExportableClassType requestedType) {
    sw.println("public void ___importStructuralType() {");
    sw.indent();
    for (JStructuralTypeField field : requestedType.getStructuralTypeFields()) {
      sw.println("super." + field.getMethodName() + "((" + field.getFieldType()
          + ")org.timepedia.exporter.client.ExporterUtil.getStructuralField"
          + field.getFieldLowestType() + "(jso, \"" + field.getName()
          + "\"));");
    }
    sw.outdent();
    sw.println("}");
  }

  private void exportStructuralTypeConstructor(String genName,
      JExportableClassType requestedType) {
    // export constructor
    sw.println("private " + ExportableTypeOracle.JSO_CLASS + " jso;");
    sw.println();

    sw.println(
        "public " + genName + "(" + ExportableTypeOracle.JSO_CLASS + " jso) {");
    sw.indent();
    sw.println("this.jso = jso;");
    if (requestedType.isStructuralType()) {
      sw.println("___importStructuralType();");
    }
    sw.outdent();
    sw.println("}");
    sw.println();
  }

  private void exportStructuralTypeFields(JExportableClassType requestedType) {
    for (JStructuralTypeField field : requestedType.getStructuralTypeFields()) {
      exportStructuralTypeField(field);
    }
  }

  private void exportStructuralTypeField(JStructuralTypeField field) {
    sw.println("public " + field.JavaDeclaration() + "{");
    sw.indent();
    if (field.isVoidReturn()) {
      sw.println("super." + field.getMethodName() + "(arg);");
      sw.println(
          "org.timepedia.exporter.client.ExporterUtil.setStructuralField("
              + "jso, \"" + field.getName() + "\", arg);");
    } else {
      sw.println(field.getReturnType() + " x = super." + field.getMethodName()
          + "(arg);");
      sw.println(
          "org.timepedia.exporter.client.ExporterUtil.setStructuralField("
              + "jso, '" + field.getName() + "', arg);");
      sw.println("return x;");
    }

    sw.outdent();
    sw.println("}");
  }

  private void exportAll(String genName) {
    List<JClassType> types = xTypeOracle.findAllExportableTypes();
    List<JClassType> instantiableTypes = new ArrayList<JClassType>();
    List<JClassType> otherTypes = new ArrayList<JClassType>();

    for (JClassType type : types) {
      if ( type.isDefaultInstantiable() && type.isPublic()) {
        instantiableTypes.add(type);
      } else {
        otherTypes.add(type);
      }
    }
    sw.println("public void export(boolean all) { ");
    sw.indent();
    sw.println("if (all) {");
    sw.indent();
    for (JClassType type : otherTypes) {
      sw.println("GWT.create(" + type.getQualifiedSourceName() + ".class);");
    }
    sw.outdent();
    sw.println("}");
    for (JClassType type : instantiableTypes) {
      sw.println("GWT.create(" + type.getQualifiedSourceName() + ".class);");
    }
    sw.outdent();
    sw.println("}");
    sw.println("public void exportAll() { ");
    sw.indent();
    sw.println("export();");
    sw.outdent();
    sw.println("}");
  }

  /**
   * Exports a static factory method corresponding to each exportable
   * constructor of the class
   */
  private void exportStaticFactoryConstructors(
      JExportableClassType requestedType) {

    JExportableConstructor[] constructors = requestedType
        .getExportableConstructors();

    for (JExportableConstructor constructor : constructors) {
      exportStaticFactoryConstructor(constructor);
    }
  }
  
  /**
   * Creates static methods to wrap functions with return or argument
   * types problematic in jsni (long, date, etc).
   */
  private void createStaticWrappers(JExportableClassType requestedType)
      throws UnableToCompleteException {
    for (JExportableMethod method : requestedType.getExportableMethods()) {
      if (method.needsWrapper()) {
        createStaticWrapperMethod(method, requestedType);
      }
    }
  }

  /**
   * Exports all exportable methods of a class
   */
  private void exportMethods(JExportableClassType requestedType)
      throws UnableToCompleteException {

    HashMap<String, DispatchTable> dispatchMap = buildDispatchTableMap(
        requestedType, false);

    HashMap<String, DispatchTable> staticDispatchMap = buildDispatchTableMap(
        requestedType, true);
    HashSet<String> exported = new HashSet<String>();
    HashSet<String> staticExported = new HashSet<String>();
    
    for (JExportableMethod method : requestedType.getExportableMethods()) {
      String methodName = method.getUnqualifiedExportName();
      if (method.isInStaticMap() ? !staticExported.contains(methodName)
           :!exported.contains(methodName)) {
        exportMethod(method, method.isInStaticMap() ? staticDispatchMap : dispatchMap);
        if(method.isStatic()) {
          exported.add(methodName);
        }
        else {
          staticExported.add(methodName);
        }
      }
    }
    if (!xTypeOracle
        .isClosure(requestedType)) {
      if (DispatchTable.isAnyOverridden(dispatchMap)) {
        registerDispatchMap(requestedType, dispatchMap, false);
      }
      if (DispatchTable.isAnyOverridden(staticDispatchMap)) {
        registerDispatchMap(requestedType, staticDispatchMap, true);
      }
    }
  }


  private void registerDispatchMap(JExportableClassType requestedType,
      HashMap<String, DispatchTable> dispatchMap, boolean isStatic) {
    
    
    sw.println("\n@org.timepedia.exporter.client.ExporterUtil::registerDispatchMap("
        + "Ljava/lang/Class;Lcom/google/gwt/core/client/JavaScriptObject;Z)\n(@"
        + requestedType.getQualifiedSourceName() + "::class,"
        + DispatchTable.toJSON(dispatchMap) + ", " + isStatic + ");");
  }

  private HashMap<String, DispatchTable> buildDispatchTableMap(
      JExportableClassType requestedType, boolean staticDispatch)
      throws UnableToCompleteException {
    HashMap<String, DispatchTable> dispMap
        = new HashMap<String, DispatchTable>();
    for (JExportableMethod meth : requestedType.getExportableMethods()) {
      if (/* !requestedType.isInstantiable() || */
          staticDispatch && !meth.isInStaticMap() || 
          !staticDispatch && meth.isInStaticMap()) {
        continue;
      }
      DispatchTable dt = dispMap.get(meth.getUnqualifiedExportName());
      if (dt == null) {
        dt = new DispatchTable(xTypeOracle);
        dispMap.put(meth.getUnqualifiedExportName(), dt);
      }
      if (!dt.addSignature(meth)) {
        logger.log(TreeLogger.ERROR,
            "Ambiguous method signature " + meth.getJSNIReference()
                + " would conflict in JS with another method");
        throw new UnableToCompleteException();
      }
    }
    return dispMap;
  }

  /**
   * Exports a Javascript constructor as $wnd.packageName.classname =
   * function(args) { if(arg0 is GWT type) { this.instance = arg0; } else
   * this.instance = invoke static factory method with args }
   */
  private void exportConstructor(JExportableClassType requestedType)
      throws UnableToCompleteException {
    
    JExportableMethod init = requestedType.getJsInitMethod();
    String namespace = "$wnd." + requestedType.getJSQualifiedExportName();
    
    // we assign the prototype of the class to underscore so we can use it
    // later to define a bunch of methods
    sw.println("var _, __ = this;");

    sw.print(namespace + " = $entry(function(){");
    sw.indent();
    sw.println("var g, j = this, " + ARG_PREFIX + " = arguments;");
    // check if this is being used to wrap GWT types
    // e.g. code is calling constructor as
    // new $wnd.package.className(opaqueGWTobject)
    // if so, we store the opaque reference in this.instance
    sw.println("if (" + ARG_PREFIX + ".length == 1 && __.@org.timepedia.exporter.client.Exporter::isAssignable(*)(" + ARG_PREFIX + "[0]))");
    sw.println("  g = " + ARG_PREFIX + "[0];");

    // used to hold arity of constructors that have been generated
    HashMap<Integer, JExportableMethod> arity
        = new HashMap<Integer, JExportableMethod>();
    
    List<JExportableMethod> constructors = new ArrayList<JExportableMethod>();
    constructors.addAll(Arrays.asList(requestedType.getExportableConstructors()));
    constructors.addAll(Arrays.asList(requestedType.getExportableFactoryMethods()));

    for (JExportableMethod constructor : constructors) {
      int numArguments = constructor.getExportableParameters().length;
      JExportableMethod conflicting = arity.get(numArguments);
      if (conflicting != null) {
        logger.log(TreeLogger.ERROR,
            "Constructor " + conflicting + " with " + numArguments + " "
                + "arguments conflicts with " + constructor + "."
                + "Two constructors may not have identical numbers of "
                + "arguments.", null);
        throw new UnableToCompleteException();
      }
      arity.put(numArguments, constructor);
      sw.println("else if (" + ARG_PREFIX + ".length == " + numArguments + ")");
      sw.indent();
      
      log("    " + namespace + constructor.toString().replaceFirst("^.+\\(", "("));
      
      // else someone is calling the constructor normally
      // we generate a JSNI call to the matching static factory method
      // and store it in this.instance
      String jsniCall;
      if (constructor instanceof JExportableConstructor) {
        jsniCall = ((JExportableConstructor)constructor).getStaticFactoryJSNIReference();
      } else {
        jsniCall =  constructor.getJSNIReference();
      }
      sw.print("g = @"+ jsniCall + "(");
      // pass arguments[0], ..., arguments[n] to the JSNI call
      declareJSConstructorPassedValues(constructor);
      sw.println(");");
      sw.outdent();
    }
    if (init != null) {
      sw.println("j = g.@" + init.getJSNIReference() + "();");
      // We have to copy the prototype functions into the new returned object
      sw.println("for (k in _) if (j[k] === undefined) j[k] = _[k];");
    }
    sw.println("j." + GWT_INSTANCE + " = g;");
    sw.println("@org.timepedia.exporter.client.ExporterUtil::setWrapper(Ljava/lang/Object;Lcom/google/gwt/core/client/JavaScriptObject;)(g, j);");
    sw.println("return j;");
    sw.outdent();
    sw.println("});");
    
    // We assign the prototype after the constructor has been defined
    sw.println("_ = " + namespace + ".prototype = new Object();");
  }

  /**
   * We create a static factory method public static [typeName] ___create(args)
   * that just invokes the real constructor with the args
   */
  private void exportStaticFactoryConstructor(
      JExportableConstructor constructor) {
    JExportableClassType consType = (JExportableClassType) constructor
        .getExportableReturnType();
    String typeName = consType.getQualifiedSourceName();
    sw.print("public static " + typeName + " "
        + constructor.getStaticFactoryMethodName() + "(");
    declareParameters(constructor, -1);
    sw.println(") {");
    sw.indent();
    sw.print("return new " + typeName + "(");
    declareJavaPassedValues(constructor, false);
    sw.println(");");
    sw.outdent();
    sw.println("}");
  }

  @SuppressWarnings("unused")
  private void debugJSPassedValues(JExportableMethod method) {
    JExportableParameter params[] = method.getExportableParameters();
    for (int i = 0; i < params.length; i++) {
      sw.print(
          "$wnd.alert(\"\"+" + params[i].getExportParameterValue(ARG_PREFIX + i)
              + ");");
    }
  }

  private void declareJSMethodPassedValues(JExportableMethod method) {
    JExportableParameter params[] = method.getExportableParameters();
    int i = method.isExportInstanceMethod() ? 1 :0;
    if ((!method.isStatic() && (method.needsWrapper()) || method.isExportInstanceMethod())) {
      sw.print("this." + GWT_INSTANCE + "" + (params.length > i ? ", " : ""));
    }
    for (int j = 0; i < params.length; i++, j++) {
      String pName = ARG_PREFIX + (method.isVarArgs() ? "[" + j + "]" : j);
      sw.print((j > 0 ? "," : "") + params[i].getExportParameterValue(pName));
    }
  }
  
  private void declareJSConstructorPassedValues(JExportableMethod method) {
    JExportableParameter params[] = method.getExportableParameters();
    for (int i = 0; i < params.length; i++) {
      String pName = ARG_PREFIX +  "[" + i + "]";
      sw.print((i > 0 ? "," : "") + params[i].getExportParameterValue(pName));
    }
  }

  /**
   * Generate comma separated list of argnames, arg0, ..., arg_n where n =
   * number of parameters of method
   *
   * @param wrap whether to wrap the passed value with ExporterBase::wrap
   */
  private void declareJavaPassedValues(JExportableMethod method, boolean wrap) {
    JExportableParameter params[] = method.getExportableParameters();
    for (int i = 0; i < params.length; i++) {
      JExportableType eType = params[i].getExportableType(false);
      boolean needExport = eType != null && eType.needsExport();
      
      if (wrap && needExport) {
        sw.print(getGwtToJsWrapper(eType) + "(");
      }
      sw.print(ARG_PREFIX + i);
      if (wrap && needExport) {
        sw.print(")");
      }
      if (i < params.length - 1) {
        sw.print(", ");
      }
    }
  }

  /**
   * Generate comma separated list of argnames, arg0, ..., arg_n where n =
   * number of parameters of constructor
   *
   * @param includeTypes true if arg names should have declared types
   */
  private void declareParameters(JExportableMethod method, int arity,
      boolean includeTypes) {
    JExportableParameter params[] = method.getExportableParameters();
    int numParams = includeTypes || arity < 0 ? params.length : arity;
    for (int i = 0; i < numParams;
        i++) {
      sw.print(
          (includeTypes ? params[i].getTypeName() : "") + " " + ARG_PREFIX + i);
      if (i < numParams - 1) {
        sw.print(", ");
      }
    }
  }

  /**
   * declare java typed Java method parameters
   */
  private void declareParameters(JExportableMethod method, int arity) {
    declareParameters(method, arity, true);
  }

  /**
   * For each exportable field Foo, we generate the following Javascript:
   * $wnd.package.className.Foo = JSNI Reference to Foo
   */
  private void exportFields(JExportableClassType requestedType)
	      throws UnableToCompleteException {
	    for (JExportableField field : requestedType.getExportableFields()) {
	      sw.print("$wnd." + field.getJSQualifiedExportName() + " = ");
	      sw.println("@" + field.getJSNIReference() + ";");
	    }
	    // if the type is exportable
	    if (xTypeOracle.isExportable(requestedType)) {
		    JClassType[] implementedInterfaces = requestedType.getType().getImplementedInterfaces();
		    // check all its interfaces
		    for (JClassType intfc : implementedInterfaces) {
				JClassType[] implInterfaces = intfc.getImplementedInterfaces();
				for (JClassType implInterface : implInterfaces) {
					// if a interface of the class extends again Exportable, export all the fields of that interface (and super types)
					if (implInterface.equals(xTypeOracle.getExportableType())) {
						Set<? extends JClassType> flattenedSupertypeHierarchy = intfc.getFlattenedSupertypeHierarchy();
						for (JClassType superType : flattenedSupertypeHierarchy) {
							if (!superType.equals(xTypeOracle.getExportableType())) {
								for (JField field : superType.getFields()) {
									if (field.isFinal() && field.isStatic() && field.isPublic()) {
										JExportableField exportableField = new JExportableField(requestedType, field);
										sw.print("$wnd." + exportableField.getJSQualifiedExportName() + " = ");
									    sw.println("@" + exportableField.getJSNIReference() + ";");
									}
								}
								
							}
						}
					}
				}
		    }
		}
	  }
  
  /**
   * Create a static wrapper for a method which can not be exported in JSNI.
   * Typically all methods which have 'long' parameters or return 'long'
   * 
   * These methods look like:
   * <pre>
    // wrapper for a static method
    public static double test13(double arg0, double arg1) {
      return (double) simpledemo.client.SimpleDemo.HelloClass.test13((long) arg0,  arg1);
    }
    // wrapper for an instance method
    public static double test14(simpledemo.client.SimpleDemo.HelloClass instance, double arg0, double arg1) {
      return (double) instance.test14((long) arg0,  arg1);
    }
   * </pre>
   * @param method
   * @param requestedType
   */
  private void createStaticWrapperMethod(JExportableMethod method, JExportableClassType requestedType) {
    String r = method.getExportableReturnType(false) != null ? method.getExportableReturnType(false).getQualifiedSourceName() : "Object";
    String body = r.equals("void") ? "" : "return ";
    String function = "public static ";
    String end = "";
    if (r.equals("long")) {
      function += "double";
      body += "(double) ";
    } else if (r.equals("java.util.Date")) {
      function += "JavaScriptObject";
      body += "ExporterUtil.dateToJsDate(";
      end = ")";
    } else {
      function += r;
    }
    function += " " + JExportableMethod.WRAPPER_PREFIX + method.getName() + "(";
    if (method.isStatic()) {
      body += method.getEnclosingTypeQualifiedSourceName();
    } else {
      function += requestedType.getQualifiedSourceName() + " instance" + (method.getExportableParameters().length > 0 ? ", " : "");
      body += "instance";
    }
    body += "." + method.getName() + "(";
    
    int i = 0;
    for (JExportableParameter p : method.getExportableParameters()) {
      String pr = i > 0 ? ", " : "";
      function += pr;
      body += pr;
      String type = p.getTypeName();
      String argName = ARG_PREFIX + i++;
      if (type.equals("long")) {
        function += "double";
        body += "(long)" + argName;
      } else if (type.equals("java.util.Date")) {
        body += "ExporterUtil.jsDateToDate(" + argName + ")";
        function += "JavaScriptObject";
      } else if (type.contains("[]")) {  
        body += p.getToArrayFunc(type, argName);
        function += "JavaScriptObject";
      } else {
        function += type;
        body += argName;
      }
      function +=  " " + argName; 
    }
    // TODO: test for issue #54
    sw.println(function + ") throws Exception {\n  " + body + end + ");\n}" );
  }
  
  /**
   * Export a method If the return type of the method is Exportable, we invoke
   * ClassExporter recursively on this type <p/> For static methods, the
   * Javascript looks like this: $wnd.package.className.staticMethod =
   * function(args) { // body } <p/> for regular methods, it looks like <p/>
   * _.methodName = function(args) { //body } <p/> where _ is previously
   * assigned to $wnd.package.className.prototype <p/> For methods returning
   * Exportable types, the body looks like <p/> return new
   * $wnd.package.className(this.instance.@methodNameJSNI(args)); <p/> which
   * wraps the returned type, otherwise it looks like this <p/> return
   * this.instance.@methodNameJSNI(args); <p/> for primitives, String,
   * subclasses of Number, and JavaScriptObject
   */
  private void exportMethod(JExportableMethod method,
      HashMap<String, DispatchTable> dispatchMap)
      throws UnableToCompleteException {
    
    JExportableType retType = method.getExportableReturnType(false);
    
//    int arity = method.getExportableParameters().length;
//    String name = method.getUnqualifiedExportName();
//    String key = name + "_" + arity;
//
//    JExportableMethod conflicting = method.isStatic() ? staticVisited.get(key)
//        : visited.get(key);
//
//    if (conflicting != null) {
//      logger.log(TreeLogger.ERROR,
//          "Method " + method + " having " + arity + " arguments conflicts with "
//              + conflicting + ". "
//              + "Two exportable methods cannot have the same number of arguments. "
//              + "Use @gwt.export <newName> on one of the methods to disambiguate.",
//          null);
//      throw new UnableToCompleteException();
//    } else {
//      if (method.isStatic()) {
//        staticVisited.put(key, method);
//      } else {
//        visited.put(key, method);
//      }
//    }

    boolean isVoid = retType != null && retType.getQualifiedSourceName().equals("void");
    boolean needsExport = retType != null && retType.needsExport();
    
    exportDependentParams(method);
    
    // Overloaded methods only need being exported once
    DispatchTable dt = dispatchMap.get(method.getUnqualifiedExportName());
    if (dt == null) {
      throw new RuntimeException("Unable to find method in dispatchMap table " + method.getUnqualifiedExportName());
    }
    if (dt.isOverloaded()
        && overloadExported.contains(method.getJSQualifiedExportName())) {
      return;
    }
    
    boolean isStatic = method.isStatic();
    boolean isExportInstanceMethod = method.isExportInstanceMethod();
    
    String mbase, mname; 
    if (isStatic && !isExportInstanceMethod) {
      mbase = "$wnd";
      mname = method.getJSQualifiedExportName();
    } else {
      mbase = "_";
      mname = method.getUnqualifiedExportName();
    }
    sw.print(mbase + "." + mname + " = $entry(function(");
    
    String margs = method.toString().replaceFirst("^.+\\(", "(") ;
    String mret = retType == null ? "void" : retType.getQualifiedSourceName().replaceAll("^.+\\.", "");
    log("      " + mname + margs + " - " + mret);

    int l = dt.isOverloaded() ? dt.maxArity() : method.getExportableParameters().length;
    if (method.isExportInstanceMethod()) l--;
    for (int i = 0; i < l; i++) {
      sw.print((i>0 ? "," : "") +  ARG_PREFIX + i);
    }
    sw.println(") {");
    sw.indent();
    
    try {
      sw.print(isVoid ? "" : "return ");
      if (!dt.isOverloaded()) {
        if (needsExport) sw.print(getGwtToJsWrapper(retType) + "(");
        sw.print((isStatic || method.needsWrapper() ? "@" : "this." + GWT_INSTANCE + ".@") + method.getJSNIReference() + "(" );
        declareJSMethodPassedValues(method);
        sw.print(")");
        if (needsExport) sw.print(")");
      } else {
        sw.print("@org.timepedia.exporter.client.ExporterUtil::runDispatch("
            + "Ljava/lang/Object;Ljava/lang/Class;I"
            + "Lcom/google/gwt/core/client/JsArray;ZZ)\n (" + (isStatic && !isExportInstanceMethod ? "null" : "this." + GWT_INSTANCE + "") + ", @"
            + method.getEnclosingExportType().getQualifiedSourceName()
            + "::class, " + getMethodDispatchIndex(dispatchMap, method.getUnqualifiedExportName()) + " , arguments, "
            + method.isInStaticMap() + ", " + method.isVarArgs() + ")[0]");
        
        overloadExported.add(method.getJSQualifiedExportName());
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    sw.println(";");
    sw.outdent();
    sw.println("});");
  }
  
  // Return the index of the method in the dispatchMap Json struct
  // Before we used the method name, but an integer reduces the js size.
  private int getMethodDispatchIndex(HashMap<String, DispatchTable> dispatchMap, String methodName) {
    int i = 0;
    for (Entry<String, DispatchTable> e: dispatchMap.entrySet()) {
      if (e.getKey().equals(methodName)){
        break;
      }
      if (e.getValue().isOverloaded()) {
        i++;
      }
    }
    return i;
  }
  
  private String getGwtToJsWrapper(JExportableType retType) {
    String rType = "Ljava/lang/Object;";
    if (retType != null && retType instanceof JExportableArrayType) {
      JExportableType xcompType = ((JExportableArrayType) retType).getComponentType();
      if (xcompType instanceof JExportablePrimitiveType) {
        rType = ((JExportableArrayType) retType).getJSNIReference();
      } else if (xcompType instanceof JExportableClassType) {
        JExportableClassType ct = (JExportableClassType) xcompType;
        rType = ct.getJsniSigForArrays();
      }
    }
    return "@org.timepedia.exporter.client.ExporterUtil::wrap(" + rType + ")";
  }

  private void exportDependentParams(JExportableMethod method) throws UnableToCompleteException {
    // for convenience to the developer, let's export any exportable
    // parameters and return types
    exportType(method.getExportableReturnType(true));
    for (JExportableParameter param : method.getExportableParameters()) {
      exportType(param.getExportableType(true));
    }
  }
  
  private void exportType(JExportableType eType) throws UnableToCompleteException {
    if (eType != null && eType.needsExport() && !exported.contains(eType)) {
      if (exportDependentClass(eType.getQualifiedSourceName())) {
        exported.add((JExportableClassType) eType);
      }
    }
  }

  private boolean exportDependentClass(String qualifiedSourceName)
      throws UnableToCompleteException {
    if (visited.contains(qualifiedSourceName)) {
      return false;
    }

    JExportableType xType = xTypeOracle.findExportableType(qualifiedSourceName);
    if (xType instanceof JExportableArrayType) {
      JExportableType xcompType = ((JExportableArrayType) xType)
          .getComponentType();
      if (xcompType instanceof JExportablePrimitiveType) {
        return false;
      } else if (xcompType instanceof JExportableClassType 
          && ((JExportableClassType)xcompType).isTransparentType()) {
        return false;
      } else {
        try {
          return exportDependentClass(xcompType.getQualifiedSourceName());
        } catch (Exception e) {
          System.out.println("Unable to exportDependentClass: " + xType);
          return false;
        }
      }
    }

    visited.add(qualifiedSourceName);
    ClassExporter exporter = new ClassExporter(logger, ctx, visited);
    exporter.exportClass(qualifiedSourceName, true);
    return true;
  }
  
  private void exportProperties(JExportableClassType requestedType) {
	  Property[] exportableProperties = requestedType.getExportableProperties();
	  for (Property property : exportableProperties) {
		  sw.print("Object.defineProperty(_, '");
		  sw.print(property.getName());
		  sw.print("', {");
		  JExportableMethod getter = property.getGetter();
		  if (getter != null) {
			  sw.print("get: function() { return this."); 
			  sw.print(getter.getUnqualifiedExportName());
			  sw.print("();}");
		  }
		  JExportableMethod setter = property.getSetter();
		  if (setter != null) {
			  if (getter != null) sw.print(", ");
			  sw.print("set: function(val) { this."); 
			  sw.print(setter.getUnqualifiedExportName());
			  sw.print("(val);}");
		  }
		  sw.println("});");
	  }
  }

  /**
   * For each package of sub1.sub2.sub3... we create a chain of objects
   * $wnd.sub1.sub2.sub3
   */
  private void declarePackages(JExportableClassType requestedClassType) {
    // save this namespace to restore later
    sw.print("var pkg = ");
    sw.println(
        "@org.timepedia.exporter.client.ExporterUtil::declarePackage(Ljava/lang/String;)('"
        + requestedClassType.getJSQualifiedExportName() + "');");
  }

  /**
   * Generate the main export method <p/> <p/> We generate a method that looks
   * like: <p/> public void export() { Exporter export1 =
   * (Exporter)GWT.create(ExportableDependency1.class) export1.export(); <p/>
   * Exporter export2 = (Exporter)GWT.create(ExportableDependency2.class)
   * export2.export(); <p/> ... export0(); }
   *
   * @param exported a list of other types that we depend on to be exported
   */
  private void genExportMethod(JExportableClassType requestedType,
      ArrayList<JExportableClassType> exported) {
    
    sw.println("private static boolean exported;");

    sw.println("public void export() { ");
    sw.indent();
    sw.println("if(!exported) {");
    sw.indent();
    sw.println("exported=true;");

    // add map from TypeName to JS constructor in ExporterUtil
    sw.println("ExporterUtil.addExporter(" + requestedType.getQualifiedSourceName() + ".class, this);");

    // first, export our dependencies
    for (JExportableClassType classType : exported) {
      if (classType.equals(requestedType)) {
          // || classType instanceof JExportableArrayType
        continue;
      }
      String qualName = classType.getRequestedType().getQualifiedSourceName();
      sw.println("GWT.create(" + qualName  + ".class);");
    }

    // now export our class
    sw.println("export0();");
    sw.outdent();
    sw.println("}");
    sw.outdent();
    sw.println("}");
  }

  /**
   * Get SourceWriter for following class and preamble package packageName;
   * import com.google.gwt.core.client.GWT; import org.timepedia.exporter.client.Exporter;
   * public class className implements interfaceName (usually Exporter) { <p/>
   * }
   *
   * @param interfaceNames vararg list of interfaces
   */
  protected SourceWriter getSourceWriter(TreeLogger logger,
      GeneratorContext context, String packageName, String className,
      String superClass, String... interfaceNames) {
    PrintWriter printWriter = context.tryCreate(logger, packageName, className);
    if (printWriter == null) {
      return null;
    }
    ClassSourceFileComposerFactory composerFactory
        = new ClassSourceFileComposerFactory(packageName, className);
    composerFactory.addImport("com.google.gwt.core.client.GWT");
    composerFactory.addImport("com.google.gwt.core.client.JavaScriptObject");
    
    if (superClass != null) {
      composerFactory.setSuperclass(superClass);
    }
    for (String interfaceName : interfaceNames) {
      composerFactory.addImplementedInterface(interfaceName);
    }

    composerFactory.addImport("org.timepedia.exporter.client.Exporter");
    composerFactory.addImport("org.timepedia.exporter.client.ExporterUtil");
    return composerFactory.createSourceWriter(context, printWriter);
  }
}
