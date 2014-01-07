package org.timepedia.exporter.doclet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import com.sun.tools.doclets.internal.toolkit.util.PackageListWriter;
import com.sun.tools.doclets.internal.toolkit.util.Util;

/**
 * Generates Js and Gss docs.
 */
public class JsDoclet extends HtmlDoclet {

  public static boolean start(RootDoc rootDoc) {
    JsDoclet jsDoclet = new JsDoclet();
    try {
      return jsDoclet.startGeneration3(rootDoc);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  private boolean startGeneration3(RootDoc root) throws Exception {
    configuration = ConfigurationImpl.getInstance();
    configuration.root = root;

    if (root.classes().length == 0) {
      configuration.message.
          error("doclet.No_Public_Classes_To_Document");
      return false;
    }
    configuration.setOptions();
    //configuration.getDocletSpecificMsg().notice("doclet.build_version",
        //configuration.getDocletSpecificBuildDate());
    ClassTree classtree = new ClassTree(configuration,
        configuration.nodeprecated);

    generateClassFiles(root, classtree);
    if (configuration.sourcepath != null
        && configuration.sourcepath.length() > 0) {
      StringTokenizer pathTokens = new StringTokenizer(configuration.sourcepath,
          String.valueOf(File.pathSeparatorChar));
      boolean first = true;
      while (pathTokens.hasMoreTokens()) {
        Util.copyDocFiles(configuration,
            pathTokens.nextToken() + File.separator,
            DocletConstants.DOC_FILES_DIR_NAME, first);
        first = false;
      }
    }

    PackageListWriter.generate(configuration);
    generatePackageFiles(classtree);

    generateOtherFiles(root, classtree);
    configuration.tagletManager.printReport();
    return true;
  }
  
  // FIXME: exportOverlay is not implemented yet
  Map<String,String> classTypeMap = new HashMap<String, String>();

  @Override
  protected void generateOtherFiles(RootDoc rootDoc, ClassTree classTree)
      throws Exception {
    super.generateOtherFiles(rootDoc, classTree);
    HtmlDocletWriter writer = new HtmlDocletWriter(configuration, "jsdoc.html");

    writer.html();
    writer.head();
    writer.println("<meta http-equiv='content-type' content='text/html; charset=UTF-8'/>");
    writer.println(getCSS());
    writer.headEnd();
    writer.body("white", true);

    ClassDoc[] classes = rootDoc.classes();
    Arrays.sort(classes);
    
    // Document static methods
    List<MethodDoc> smethods = new ArrayList<MethodDoc>();
    for (ClassDoc clz : classes) {
      if (isExportable(clz)) {
        
        // FIXME: implement exportoverlay somehow.
        classTypeMap.put(clz.simpleTypeName(), getExportedName(clz, false, true));
        
        if (hasStaticMethods(clz)) {
          for (MethodDoc md : clz.methods()) {
            if (md.isStatic() && isExportable(md)) {
              smethods.add(md);
            }
          }
        }
      }
    }
    if (smethods.size() > 0) {
      writer.h1("Exported JavaScript-API: Index of static functions");
      writer.table(1, "100%", 0, 0);
      java.util.Collections.sort(smethods);
      for (MethodDoc md: smethods) {
        writeMethod(writer, false, true, md);
      }
      writer.tableEnd();
    }
    
    List<ClassDoc> eclasses = new ArrayList<ClassDoc>();
    for (ClassDoc clz : classes) {
      if (isExportable(clz) && hasClassMethods(clz) && !isExportedClosure(clz)) {
        eclasses.add(clz);
      }
    }
    
    if (eclasses.size() > 0) {
      // Write an index of classes
      writer.h1("Exported JavaScript-API: Index of Classes");
      writer.ul();
      for (ClassDoc clz : eclasses) {
        writer.li();
        writer.println(getExportedName(clz, true, true));
      }
      writer.ulEnd();
      
      // Write each class
      for (ClassDoc clz : eclasses) {
        String className = getExportedName(clz, false, true);
        writer.h2("<div id=" + className + ">"+ className + "</div>");
        String comments = clz.commentText().trim();
        if (!comments.isEmpty()) {
          writer.println("<div class=jsdocText>" + filter(clz.commentText()) + "</div>");
        }

        writer.table(1, "100%", 0, 0);
        writeConstructors(writer, clz);
        writeMethods(writer, clz, true, isExportedAll(clz), new ArrayList<String>());
        writer.tableEnd();
      }
    }

    writer.bodyEnd();
    writer.htmlEnd();
    writer.flush();
    writer.close();
  }

  private void writeConstructors(HtmlDocletWriter writer, ClassDoc clz) {
    String cName = getExportedName(clz, false, true);
    boolean firstcon = true;
    for (ConstructorDoc cd : clz.constructors()) {
      if (isExportable(cd)) {
        if (firstcon) {
          writer.tr();
          writer.tdColspanBgcolorStyle(2, "", "jsdocHeader");
          writer.print("Constructors");
          firstcon = false;
          writer.tdEnd();
          writer.trEnd();
        }
        writer.tr();
        writer.tdVAlignClass("top", "jsdocRetType");
        writer.print("&nbsp");
        writer.tdEnd();
        writer.tdVAlignClass("top", "jsdocMethod");
        writer.print("<span class=jsdocMethodName>" + cName + "</span>(");
        writeParameters(writer, cd.parameters());
        writer.print(")");
        writer.br();
        
        String comment = filter(cd.commentText());
        comment += getCommentTags(cd);
        writer.print("<span class=jsdocComment>" + comment + "</span>");
        
        writer.tdEnd();
        writer.trEnd();
      }
    }
  }
  
  private void writeMethods(HtmlDocletWriter writer, ClassDoc clz, boolean firstcon, boolean all, List<String> visited) {
    if (clz == null) {
      return;
    }
    for (MethodDoc md : clz.methods()) {
      if (!md.isStatic() && md.isPublic() && !md.isAbstract()) {
        String sig = getSignatureMethod(md);
        if (!visited.contains(sig)) {
          if (all || isExportable(md)) {
            writeMethod(writer, firstcon, false, md);
            firstcon = false;
          }
          visited.add(sig);
        }
      }
    }
    writeMethods(writer, clz.superclass(), firstcon, all, visited);
  }
  
  private MethodDoc getMethodInInterface(MethodDoc md) {
    if (!md.containingClass().isInterface()) {
      String sig = getSignatureMethod(md);
      for (ClassDoc cd: md.containingClass().interfaces()) {
        for (MethodDoc m: cd.methods()) {
          if (sig.equals(getSignatureMethod(m)) && isExportable(m)) {
            return m;
          }
        }
      }
    }
    return null;
  }
  
  private MethodDoc getMethodInSuperclass(ClassDoc superc, MethodDoc md) {
    if (superc != null) {
      String sig = getSignatureMethod(md);
      for (MethodDoc m : superc.methods()) {
        if (sig.equals(getSignatureMethod(m))) {
          return m;
        }
      }
      return getMethodInSuperclass(superc.superclass(), md);
    }
    return null;
  }
  
  private String getSignatureMethod (MethodDoc md) {
    return md.returnType() + " " + md.name() + md.signature();
  }

  private void writeMethod(HtmlDocletWriter writer, boolean firstcon, boolean writePakage,
      MethodDoc md) {
    ClassDoc cd = md.containingClass();
    String pkg = writePakage ? getExportedName(cd, false, true) : "";
    String name = getExportedName(md);
    if (name.startsWith("$wnd")) {
      pkg = "";
      name = name.replaceFirst("^\\$wnd\\.", "");
    }
    if (!pkg.isEmpty()) {
      pkg += ".";
    }
    
    if (firstcon) {
      writer.tr();
      writer.tdColspanBgcolorStyle(2, "", "jsdocHeader");
      writer.print("Methods");
      firstcon = false;
      writer.tdEnd();
      writer.trEnd();
    }
    writer.tr();
    writer.tdVAlignClass("top", "jsdocRetType");
    writer.print(getExportedName(md.returnType(), true));

    writer.tdEnd();
    writer.tdVAlignClass("top", "jsdocMethod");
    writer.print(
        "<b class=jsdocMethodName>" + pkg + name + "</b>"
            + "(");
    writeParameters(writer, md.parameters());
    writer.print(")");
    writer.br();
    
    String comments = md.commentText();
    if (!md.isStatic() && comments.isEmpty()) {
      MethodDoc id = getMethodInInterface(md);
      if (id != null) {
        comments = id.commentText();
      }
      if (comments.isEmpty()) {
        id = getMethodInSuperclass(cd.superclass(), md);
      } else {
        md = id;
      }
      if (id != null) {
        comments = id.commentText();
        md = id;
      }
    }
    comments += getCommentTags(md);
    if (!comments.isEmpty()) {
      writer.print("<span class=jsdocComment>" + filter(comments) + "</span>");
    }
    writer.tdEnd();
    writer.trEnd();
  }
  
  private String getCommentTags(ExecutableMemberDoc doc) {
    String ret = "";
    if (doc != null) {
      ret += "<ul class='params'>";
      for (ParamTag t : doc.paramTags() ){
        ret +="<li><span class='paramName'>" + t.parameterName() + "</span> " + t.parameterComment();
      }
      for (Tag t : doc.tags("return") ){
        ret += "<li><span class='return'>return</span> " + t.text();
      }
      ret += "</ul>";
    }
    return ret;
  }
  
  private boolean hasClassMethods(ClassDoc clz) {
    if (clz != null && !clz.isInterface() && !clz.isAbstract()) {
      for (ConstructorDoc cd : clz.constructors()) {
        if (isExportable(cd)) {
          return true;
        }
      }
      for (MethodDoc md : clz.methods()) {
        if (!md.isStatic()) {
          boolean exportable = isExportable(md);
          if (!exportable && (md = getMethodInInterface(md)) != null) {
            exportable = isExportable(md);
          }
          if (exportable) {
            return true;
          }
        }
      }
      return hasClassMethods(clz.superclass());
    }
    return false;
  }
  
  private boolean hasStaticMethods(ClassDoc clz) {
    int countExportedMethods = 0;
    for (MethodDoc md : clz.methods()) {
      if (isExportable(md) && md.isStatic()) {
        countExportedMethods++;
      }
    }
    return countExportedMethods > 0;
  }
  
  private String getExportedName(MethodDoc cd) {
    String ename = cd.name();
    for (AnnotationDesc a : cd.annotations()) {
      if (a.annotationType().name().equals("Export")) {
        for (AnnotationDesc.ElementValuePair p : a.elementValues()) {
          ename = p.value().toString().trim();
          break;
        }
      }
    }
    return ename.replaceAll("\"", "");
  }

  protected String filter(String s) {
    if (s.startsWith("Created")) {
      return "";
    }
    s = s.replaceAll("(?s)\\{@link\\s[^\\}]*?#(.+)\\}", "$1");
    s = s.replaceAll("(?s)\\{@link\\s[^\\}]*?([^\\.\\}]+)\\}", "<a href=#$1>$1</a>");
    return s;
  }

  private String getExportedName(Type clz, boolean link) {
    return (clz.isPrimitive() ? "void".equals(clz.typeName()) ? "&nbsp;" 
        : clz.typeName() : getExportedName(clz.asClassDoc(), link, false)) + clz.dimension();
  }

  private void writeParameters(HtmlDocletWriter writer, Parameter[] ps) {
    writer.print(getParameterString(ps));
  }

  private boolean isExportable(ConstructorDoc cd) {
    boolean export = isExported(cd.containingClass());
    for (AnnotationDesc a : cd.annotations()) {
      if (a.annotationType().name().equals("Export")) {
        export = true;
      }
      if (a.annotationType().name().equals("NoExport")) {
        export = false;
      }
    }
    return export;
  }

  private boolean isExportable(MethodDoc md) {
    if (md == null) {
      return false;
    }
    boolean export = isExported(md.containingClass());
    for (AnnotationDesc a : md.annotations()) {
      if (a.annotationType().name().equals("Export")) {
        return true;
      }
      if (a.annotationType().name().equals("NoExport")) {
        return false;
      }
    }
    if (!export) {
      export = isExportable(getMethodInInterface(md));
    }
    return export;
  }
  

  private boolean isExportedClosure(ClassDoc clz) {
    for (AnnotationDesc a : clz.annotations()) {
      String aname = a.annotationType().name();
      if (aname.equals("ExportClosure")) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isExportedAll(ClassDoc clz) {
    for (AnnotationDesc a : clz.annotations()) {
      String aname = a.annotationType().name();
      if (aname.equals("Export") || aname.equals("ExportClosure")) {
        for (AnnotationDesc.ElementValuePair p : a.elementValues()) {
          if ("all".equals(p.element().name())) {
            return "true".equals(p.value().toString());
          }
        }
      }
    }
    return false;
  }
  
  private boolean isExported(ClassDoc clz) {
    for (AnnotationDesc a : clz.annotations()) {
      String aname = a.annotationType().name();
      if (aname.equals("Export") || aname.equals("ExportClosure")) {
        return true;
      }
    }
    return false;
  }
  
  private String getExportedPackage(ClassDoc clz) {
    if (clz == null) {
      return "";
    }

    PackageDoc cpkg = clz.containingPackage();
    String pkg = cpkg == null ? "" : (cpkg.name().trim());

    for (AnnotationDesc a : clz.annotations()) {
      if (a.annotationType().name().equals("ExportPackage")) {
        for (AnnotationDesc.ElementValuePair p : a.elementValues()) {
          pkg = p.value().toString().replaceAll("\"", "");
          if (!pkg.isEmpty()) {
            pkg += ".";
          }
          break;
        }
      }
    }
    return pkg;
  }

  private String getExportedName(ClassDoc clz, boolean withLink, boolean withPkg) {
    if (clz == null) {
      return "";
    }

    PackageDoc cpkg = clz.containingPackage();
    String pkg = cpkg == null ? "" : cpkg.name();
    String name = clz.name();
    
    boolean isClosure = false;
    boolean isEnclosed = clz.containingClass() != null;
    boolean isExportEnclosed = isEnclosed && isExportable(clz.containingClass());
    boolean removeEnclosedPart = false;
    
    if (isExportEnclosed) {
      pkg = getExportedName(clz.containingClass(), false, true);
      removeEnclosedPart = true;
    }

    for (AnnotationDesc a : clz.annotations()) {
      if (a.annotationType().name().equals("ExportPackage")) {
        for (AnnotationDesc.ElementValuePair p : a.elementValues()) {
          pkg = p.value().toString().trim();
          removeEnclosedPart = true;
          break;
        }
      }
      if (a.annotationType().name().equals("Export")) {
        for (AnnotationDesc.ElementValuePair p : a.elementValues()) {
          if ("value".equals(p.element().name())) {
            name = p.value().toString().trim().replaceAll("\"", "");
            if (!name.isEmpty()) {
              removeEnclosedPart = false;
              break;
            }
          }
        }
      }
      if (a.annotationType().name().equals("ExportClosure")) {
        isClosure = true;
        name = "<i class=jsdocClosureFunc>function</i>(";
        name += getParameterString(clz.methods()[0].parameters());
        name += ")";
        pkg = "";
      }
    }
    if (removeEnclosedPart) {
      name = name.replaceFirst("^.+\\.", "");
    }
    
    pkg = pkg.replaceAll("\"", "").replaceFirst("\\.+$", "");
    if (!pkg.isEmpty()) {
      pkg += ".";
    }

    if (withLink && !isClosure && 
        !name.matches("String|JavaScriptObject|Object|Exportable|Class|Date")) {
      if (withPkg) {
        name = pkg + "<a href=#" + pkg + name + "><b>" + name + "</b></a>";  
      } else {
        name = "<a href=#" + pkg + name + ">" + name + "</a>";  
      }
    } else if (withPkg) {
      name = pkg + name;
    }
    return name;
  }
  
  private String getParameterString(Parameter[] ps) {
    String result = "";
    for (int i = 0; i < ps.length; i++) {
      Type type = ps[i].type();
      String ename = getExportedName(type, true);
      String pname =  ename.contains("function") ? "{}" : ps[i].name();
      result += "<span class=jsdocParameterType>" + ename
          + "</span> <span class=jsdocParameterName>" + pname
          + "</span>";
      if (i < ps.length - 1) {
        result += ", ";
      }
    }
    return result;
  }



  private static boolean isExportable(ClassDoc clz) {
    if (clz == null) {
      return false;
    }
    for (ClassDoc i : clz.interfaces()) {
      if (i.name().contains("Exportable")) {
        return true;
      }
      for (ClassDoc j : i.interfaces()) {
        if (j.name().contains("Exportable")) {
          return true;
        }
      }
    }
    for (MethodDoc m : clz.methods()) {
      if (!m.isStatic()) {
        for (AnnotationDesc a : m.annotations()) {
          if (a.annotationType().name().equals("Export")) {
            return true;
          }
        }
      }
    }
    return isExportable(clz.superclass());
  }
  
  public String getCSS() {
    return "<style>"
    + "  body,table {"
    + "    background-color: #f3ead8;"
    + "    color: #000000;"
    + "    font-family: Arial, Helvetica, sans-serif;"
    + "    font-size: 14px;"
    + "  }"
    + "  h1,h2 {"
    + "    font-family: Trebuchet MS;"
    + "    color: navy;"
    + "    font-size: 18pt;"
    + "    border-bottom: double 3px;"
    + "    padding-top: 20px;"
    + "    padding-left: 20px;"
    + "  }"
    + "  table {"
    + "    width: 100%;"
    + "    border: 1px;"
    + "  }"
    + "  td.jsdocRetType {"
    + "    width: 200px;"
    + "  }"
    + "  div.jsdocText {"
    + "    padding-bottom: 20px;"
    + "  }"
    + "  .jsdocHeader {"
    + "    background-color:  #E8F1F6;"
    + "    padding: 5px;"
    + "    font-weight: bold;"
    + "  }"
    + "  td {"
    + "    padding-left: 5px;"
    + "    padding-right: 10px;"
    + "  }"
    + "  .jsdocMethodName {"
    + "    font-weight: bold;"
    + "  }"
    + "  .jsdocComment,.jsdocComment p {"
    + "    font-size: 11px;"
    + "    color: blue;"
    + "    padding-left: 15px;"
    + "  }"
    + "  a {"
    + "    color: #331166;"
    + "    text-decoration: none;"
    + "  }"
    + "  a:hover {"
    + "    color: #666666;"
    + "    text-decoration: underline;"
    + "  }"
    + "  .params li {"
    + "  }"
    + "  .paramName, .return {"
    + "    font-weight: bold;"
    + "  }"
    + "</style>";
  }
  
}
