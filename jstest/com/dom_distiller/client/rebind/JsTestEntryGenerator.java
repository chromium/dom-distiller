// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;


public class JsTestEntryGenerator extends Generator {
    public static final boolean DEBUG = false;

    private static class TestCase {
        JClassType classType;
        List<JMethod> tests;
        TestCase(JClassType ct, List<JMethod> t) {
            classType = ct;
            tests = t;
        }
    }

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typename)
            throws UnableToCompleteException {
        String packageName = "com.dom_distiller.client";
        String outputClassname = "JsTestBuilderImpl";

        List<TestCase> testCases = getTestCases(logger, context);

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(
                packageName, outputClassname);
        composer.addImplementedInterface("com.dom_distiller.client.JsTestSuiteBuilder");
        PrintWriter printWriter = context.tryCreate(logger, packageName, outputClassname);
        if (printWriter != null) {
            for (TestCase ts : testCases) {
                String className = ts.classType.getName();
                String qualifiedClassName = ts.classType.getPackage().getName() + "." + className;
                composer.addImport(qualifiedClassName);
            }
            SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);
            sourceWriter.println("JsTestBuilderImpl () {");
            sourceWriter.println("}");
            sourceWriter.println("public JsTestSuiteBase build() {");
            sourceWriter.indent();
            sourceWriter.println("JsTestSuiteBase testSuite = new JsTestSuiteBase();");
            for (TestCase ts : testCases) {
                String className = ts.classType.getName();
                String qualifiedClassName = ts.classType.getPackage().getName() + "." + className;
                sourceWriter.println("testSuite.addTestCase(");
                sourceWriter.println("        new JsTestSuiteBase.TestCaseFactory() {");
                sourceWriter.println("            @Override");
                sourceWriter.println("            public JsTestCase build() {");
                sourceWriter.println("                return new " + className + "();");
                sourceWriter.println("            }");
                sourceWriter.println("        }, \"" + qualifiedClassName + "\")");
                sourceWriter.indent();
                for (JMethod test : ts.tests) {
                    String methodName = test.getName();
                    sourceWriter.println(".addTest(");
                    sourceWriter.println("        new JsTestSuiteBase.TestCaseRunner() {");
                    sourceWriter.println("            @Override");
                    sourceWriter.println("            public void run(JsTestCase testCase) throws Throwable {");
                    sourceWriter.println("                ((" + className + ")testCase)." + methodName + "();");
                    sourceWriter.println("            }");
                    sourceWriter.println("        }, \"" + methodName + "\")");
                }
                sourceWriter.println(";");
                sourceWriter.outdent();
            }
            sourceWriter.println("return testSuite;");
            sourceWriter.outdent();
            sourceWriter.println("}");
            sourceWriter.commit(logger);
        }
        return composer.getCreatedClassName();
    }

    public static List<TestCase> getTestCases(TreeLogger logger, GeneratorContext context)
            throws UnableToCompleteException {
        if (DEBUG) logger = logger.branch(TreeLogger.WARN, "Getting test cases", null, null);
        TypeOracle oracle = context.getTypeOracle();
        JClassType jsTestCaseClass = oracle.findType(JsTestCase.class.getName());

        List<TestCase> testCases = new ArrayList<TestCase>();

        for (JClassType classType : oracle.getTypes()) {
            if (classType.equals(jsTestCaseClass) || !classType.isAssignableTo(jsTestCaseClass)) {
                continue;
            }

            if (classType.getEnclosingType() != null) {
                if (DEBUG) logger.log(TreeLogger.WARN, "Skipping nested class: " +
                        classType.getEnclosingType().getName() + "." + classType.getName());
                continue;
            }

            if (DEBUG) logger.log(TreeLogger.WARN, "Found class: " + classType.getName());
            testCases.add(new TestCase(classType, findTests(logger, context, classType)));
        }
        return testCases;
    }

    public static List<JMethod> findTests(
            TreeLogger logger, GeneratorContext context, JClassType classType)
            throws UnableToCompleteException {
        if (DEBUG) logger = logger.branch(TreeLogger.WARN,
                "Finding tests for class: " + classType.getName());

        List<JMethod> tests = new ArrayList<JMethod>();
        for (JMethod method : classType.getMethods()) {
            if (method.getName().startsWith("test")) {
                if (DEBUG) logger.log(TreeLogger.WARN, "Found test: " + method.getName());
                verifyTestSignature(logger, classType, method);
                tests.add(method);
            }
        }
        return tests;
    }

    public static void verifyTestSignature(
            TreeLogger logger, JClassType classType, JMethod method)
            throws UnableToCompleteException {
        if (!method.isPublic()) {
            logger.log(TreeLogger.ERROR, classType + "." + method.getName() + " should be public.");
            throw new UnableToCompleteException();
        }

        if (method.getParameters().length != 0) {
            logger.log(TreeLogger.ERROR, classType + "." + method.getName()
                    + " should not have any parameters.");
            throw new UnableToCompleteException();
        }
    }
}
