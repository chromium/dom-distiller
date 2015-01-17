// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextDocument;

public class TextDocumentConstructionTest extends DomDistillerJsTestCase {
    private static boolean DEBUG = false;

    public void testTextDocumentConstruction() throws Exception {
        mBody.setInnerHTML(html);
        TextDocument doc = TestTextDocumentBuilder.fromPage(mRoot);

        if (DEBUG) {
            System.out.println("---~~~~---");
            System.out.println(doc.debugString());
            System.out.println("---~~~~---");
        }

        String[] left = expectedDebugString.split("\n");
        String[] right = doc.debugString().split("\n");
        for (int i = 0; i < left.length; i++) {
            assertEquals(left[i], right[i]);
        }
        assertEquals(expectedDebugString, doc.debugString());
    }

    // This is the <body> of gwt's doc/javadoc/help-doc.html
    private String html =
        "<!-- ========= START OF TOP NAVBAR ======= -->" +
        "<div class=\"topNav\"><a name=\"navbar_top\">" +
        "<!--   -->" +
        "</a><a href=\"#skip-navbar_top\" title=\"Skip navigation links\"></a><a name=\"navbar_top_firstrow\">" +
        "<!--   -->" +
        "</a>" +
        "<ul class=\"navList\" title=\"Navigation\">" +
        "<li><a href=\"overview-summary.html\">Overview</a></li>" +
        "<li>Package</li>" +
        "<li>Class</li>" +
        "<li>Use</li>" +
        "<li><a href=\"overview-tree.html\">Tree</a></li>" +
        "<li><a href=\"deprecated-list.html\">Deprecated</a></li>" +
        "<li><a href=\"index-all.html\">Index</a></li>" +
        "<li class=\"navBarCell1Rev\">Help</li>" +
        "</ul>" +
        "<div class=\"aboutLanguage\"><em>GWT 2.5.1</em></div>" +
        "</div>" +
        "<div class=\"subNav\">" +
        "<ul class=\"navList\">" +
        "<li>Prev</li>" +
        "<li>Next</li>" +
        "</ul>" +
        "<ul class=\"navList\">" +
        "<li><a href=\"index.html?help-doc.html\" target=\"_top\">Frames</a></li>" +
        "<li><a href=\"help-doc.html\" target=\"_top\">No Frames</a></li>" +
        "</ul>" +
        "<ul class=\"navList\" id=\"allclasses_navbar_top\">" +
        "<li><a href=\"allclasses-noframe.html\">All Classes</a></li>" +
        "</ul>" +
        "<div>" +
        "</div>" +
        "<a name=\"skip-navbar_top\">" +
        "<!--   -->" +
        "</a></div>" +
        "<!-- ========= END OF TOP NAVBAR ========= -->" +
        "<div class=\"header\">" +
        "<h1 class=\"title\">How This API Document Is Organized</h1>" +
        "<div class=\"subTitle\">This API (Application Programming Interface) document has pages corresponding to the items in the navigation bar, described as follows.</div>" +
        "</div>" +
        "<div class=\"contentContainer\">" +
        "<ul class=\"blockList\">" +
        "<li class=\"blockList\">" +
        "<h2>Overview</h2>" +
        "<p>The <a href=\"overview-summary.html\">Overview</a> page is the front page of this API document and provides a list of all packages with a summary for each.  This page can also contain an overall description of the set of packages.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Package</h2>" +
        "<p>Each package has a page that contains a list of its classes and interfaces, with a summary for each. This page can contain six categories:</p>" +
        "<ul>" +
        "<li>Interfaces (italic)</li>" +
        "<li>Classes</li>" +
        "<li>Enums</li>" +
        "<li>Exceptions</li>" +
        "<li>Errors</li>" +
        "<li>Annotation Types</li>" +
        "</ul>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Class/Interface</h2>" +
        "<p>Each class, interface, nested class and nested interface has its own separate page. Each of these pages has three sections consisting of a class/interface description, summary tables, and detailed member descriptions:</p>" +
        "<ul>" +
        "<li>Class inheritance diagram</li>" +
        "<li>Direct Subclasses</li>" +
        "<li>All Known Subinterfaces</li>" +
        "<li>All Known Implementing Classes</li>" +
        "<li>Class/interface declaration</li>" +
        "<li>Class/interface description</li>" +
        "</ul>" +
        "<ul>" +
        "<li>Nested Class Summary</li>" +
        "<li>Field Summary</li>" +
        "<li>Constructor Summary</li>" +
        "<li>Method Summary</li>" +
        "</ul>" +
        "<ul>" +
        "<li>Field Detail</li>" +
        "<li>Constructor Detail</li>" +
        "<li>Method Detail</li>" +
        "</ul>" +
        "<p>Each summary entry contains the first sentence from the detailed description for that item. The summary entries are alphabetical, while the detailed descriptions are in the order they appear in the source code. This preserves the logical groupings established by the programmer.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Annotation Type</h2>" +
        "<p>Each annotation type has its own separate page with the following sections:</p>" +
        "<ul>" +
        "<li>Annotation Type declaration</li>" +
        "<li>Annotation Type description</li>" +
        "<li>Required Element Summary</li>" +
        "<li>Optional Element Summary</li>" +
        "<li>Element Detail</li>" +
        "</ul>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Enum</h2>" +
        "<p>Each enum has its own separate page with the following sections:</p>" +
        "<ul>" +
        "<li>Enum declaration</li>" +
        "<li>Enum description</li>" +
        "<li>Enum Constant Summary</li>" +
        "<li>Enum Constant Detail</li>" +
        "</ul>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Use</h2>" +
        "<p>Each documented package, class and interface has its own Use page.  This page describes what packages, classes, methods, constructors and fields use any part of the given class or package. Given a class or interface A, its Use page includes subclasses of A, fields declared as A, methods that return A, and methods and constructors with parameters of type A.  You can access this page by first going to the package, class or interface, then clicking on the \"Use\" link in the navigation bar.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Tree (Class Hierarchy)</h2>" +
        "<p>There is a <a href=\"overview-tree.html\">Class Hierarchy</a> page for all packages, plus a hierarchy for each package. Each hierarchy page contains a list of classes and a list of interfaces. The classes are organized by inheritance structure starting with <code>java.lang.Object</code>. The interfaces do not inherit from <code>java.lang.Object</code>.</p>" +
        "<ul>" +
        "<li>When viewing the Overview page, clicking on \"Tree\" displays the hierarchy for all packages.</li>" +
        "<li>When viewing a particular package, class or interface page, clicking \"Tree\" displays the hierarchy for only that package.</li>" +
        "</ul>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Deprecated API</h2>" +
        "<p>The <a href=\"deprecated-list.html\">Deprecated API</a> page lists all of the API that have been deprecated. A deprecated API is not recommended for use, generally due to improvements, and a replacement API is usually given. Deprecated APIs may be removed in future implementations.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Index</h2>" +
        "<p>The <a href=\"index-all.html\">Index</a> contains an alphabetic list of all classes, interfaces, constructors, methods, and fields.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Prev/Next</h2>" +
        "<p>These links take you to the next or previous class, interface, package, or related page.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Frames/No Frames</h2>" +
        "<p>These links show and hide the HTML frames.  All pages are available with or without frames.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>All Classes</h2>" +
        "<p>The <a href=\"allclasses-noframe.html\">All Classes</a> link shows all classes and interfaces except non-static nested types.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Serialized Form</h2>" +
        "<p>Each serializable or externalizable class has a description of its serialization fields and methods. This information is of interest to re-implementors, not to developers using the API. While there is no link in the navigation bar, you can get to this information by going to any serialized class and clicking \"Serialized Form\" in the \"See also\" section of the class description.</p>" +
        "</li>" +
        "<li class=\"blockList\">" +
        "<h2>Constant Field Values</h2>" +
        "<p>The <a href=\"constant-values.html\">Constant Field Values</a> page lists the static final fields and their values.</p>" +
        "</li>" +
        "</ul>" +
        "<em>This help file applies to API documentation generated using the standard doclet.</em></div>" +
        "<!-- ======= START OF BOTTOM NAVBAR ====== -->" +
        "<div class=\"bottomNav\"><a name=\"navbar_bottom\">" +
        "<!--   -->" +
        "</a><a href=\"#skip-navbar_bottom\" title=\"Skip navigation links\"></a><a name=\"navbar_bottom_firstrow\">" +
        "<!--   -->" +
        "</a>" +
        "<ul class=\"navList\" title=\"Navigation\">" +
        "<li><a href=\"overview-summary.html\">Overview</a></li>" +
        "<li>Package</li>" +
        "<li>Class</li>" +
        "<li>Use</li>" +
        "<li><a href=\"overview-tree.html\">Tree</a></li>" +
        "<li><a href=\"deprecated-list.html\">Deprecated</a></li>" +
        "<li><a href=\"index-all.html\">Index</a></li>" +
        "<li class=\"navBarCell1Rev\">Help</li>" +
        "</ul>" +
        "<div class=\"aboutLanguage\"><em>GWT 2.5.1</em></div>" +
        "</div>" +
        "<div class=\"subNav\">" +
        "<ul class=\"navList\">" +
        "<li>Prev</li>" +
        "<li>Next</li>" +
        "</ul>" +
        "<ul class=\"navList\">" +
        "<li><a href=\"index.html?help-doc.html\" target=\"_top\">Frames</a></li>" +
        "<li><a href=\"help-doc.html\" target=\"_top\">No Frames</a></li>" +
        "</ul>" +
        "<ul class=\"navList\" id=\"allclasses_navbar_bottom\">" +
        "<li><a href=\"allclasses-noframe.html\">All Classes</a></li>" +
        "</ul>" +
        "<div>" +
        "</div>" +
        "<a name=\"skip-navbar_bottom\">" +
        "<!--   -->" +
        "</a></div>";

    // This is constructed from the output file (w/ DEBUG=true) by a command like:
    // cat $output
    //   | sed -n -e '/---~~~~---/,/---~~~~---/p'
    //   | tail -n +2 | head -n -2
    //   | sed -e 's/"/\\"/g' -e 's/\(\[[01][m;]\)/\\u001B\1/g' -e 's/\(.*\)/        "\1\\n" +/'
    //   > /tmp/expected
    private String expectedDebugString =
        "[0-0;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "   Overview \n" +
        "[1-1;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Package\n" +
        "[2-2;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Class\n" +
        "[3-3;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Use\n" +
        "[4-4;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " Tree \n" +
        "[5-5;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " Deprecated \n" +
        "[6-6;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " Index \n" +
        "[7-7;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Help\n" +
        "[8-8;tl=4; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[]\u001B[0m\n" +
        "GWT 2.5.1\n" +
        "[9-9;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Prev\n" +
        "[10-10;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Next\n" +
        "[11-11;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " Frames \n" +
        "[12-12;tl=6; nw=2;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " No Frames \n" +
        "[13-13;tl=6; nw=2;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " All Classes \n" +
        "[14-14;tl=4; nw=6;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H1, de.l3s.boilerpipe/HEADING]\u001B[0m\n" +
        "How This API Document Is Organized\n" +
        "[15-15;tl=4; nw=19;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[]\u001B[0m\n" +
        "This API (Application Programming Interface) document has pages corresponding to the items in the navigation bar, described as follows.\n" +
        "[16-16;tl=6; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Overview\n" +
        "[17-17;tl=6; nw=36;ld=0.027778]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "The  Overview  page is the front page of this API document and provides a list of all packages with a summary for each.  This page can also contain an overall description of the set of packages.\n" +
        "[18-18;tl=6; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Package\n" +
        "[19-19;tl=6; nw=25;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Each package has a page that contains a list of its classes and interfaces, with a summary for each. This page can contain six categories:\n" +
        "[20-20;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Interfaces (italic)\n" +
        "[21-21;tl=7; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Classes\n" +
        "[22-22;tl=7; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Enums\n" +
        "[23-23;tl=7; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Exceptions\n" +
        "[24-24;tl=7; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Errors\n" +
        "[25-25;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Annotation Types\n" +
        "[26-26;tl=6; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Class/Interface\n" +
        "[27-27;tl=6; nw=31;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Each class, interface, nested class and nested interface has its own separate page. Each of these pages has three sections consisting of a class/interface description, summary tables, and detailed member descriptions:\n" +
        "[28-28;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Class inheritance diagram\n" +
        "[29-29;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Direct Subclasses\n" +
        "[30-30;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "All Known Subinterfaces\n" +
        "[31-31;tl=7; nw=4;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "All Known Implementing Classes\n" +
        "[32-32;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Class/interface declaration\n" +
        "[33-33;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Class/interface description\n" +
        "[34-34;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Nested Class Summary\n" +
        "[35-35;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Field Summary\n" +
        "[36-36;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Constructor Summary\n" +
        "[37-37;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Method Summary\n" +
        "[38-38;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Field Detail\n" +
        "[39-39;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Constructor Detail\n" +
        "[40-40;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Method Detail\n" +
        "[41-41;tl=6; nw=42;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Each summary entry contains the first sentence from the detailed description for that item. The summary entries are alphabetical, while the detailed descriptions are in the order they appear in the source code. This preserves the logical groupings established by the programmer.\n" +
        "[42-42;tl=6; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Annotation Type\n" +
        "[43-43;tl=6; nw=12;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Each annotation type has its own separate page with the following sections:\n" +
        "[44-44;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Annotation Type declaration\n" +
        "[45-45;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Annotation Type description\n" +
        "[46-46;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Required Element Summary\n" +
        "[47-47;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Optional Element Summary\n" +
        "[48-48;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Element Detail\n" +
        "[49-49;tl=6; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Enum\n" +
        "[50-50;tl=6; nw=11;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Each enum has its own separate page with the following sections:\n" +
        "[51-51;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Enum declaration\n" +
        "[52-52;tl=7; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Enum description\n" +
        "[53-53;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Enum Constant Summary\n" +
        "[54-54;tl=7; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Enum Constant Detail\n" +
        "[55-55;tl=6; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Use\n" +
        "[56-56;tl=6; nw=84;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Each documented package, class and interface has its own Use page.  This page describes what packages, classes, methods, constructors and fields use any part of the given class or package. Given a class or interface A, its Use page includes subclasses of A, fields declared as A, methods that return A, and methods and constructors with parameters of type A.  You can access this page by first going to the package, class or interface, then clicking on the \"Use\" link in the navigation bar.\n" +
        "[57-57;tl=6; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Tree (Class Hierarchy)\n" +
        "[58-58;tl=6; nw=45;ld=0.044444]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "There is a  Class Hierarchy  page for all packages, plus a hierarchy for each package. Each hierarchy page contains a list of classes and a list of interfaces. The classes are organized by inheritance structure starting with java.lang.Object. The interfaces do not inherit from java.lang.Object.\n" +
        "[59-59;tl=7; nw=14;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "When viewing the Overview page, clicking on \"Tree\" displays the hierarchy for all packages.\n" +
        "[60-60;tl=7; nw=18;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "When viewing a particular package, class or interface page, clicking \"Tree\" displays the hierarchy for only that package.\n" +
        "[61-61;tl=6; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Deprecated API\n" +
        "[62-62;tl=6; nw=40;ld=0.05]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "The  Deprecated API  page lists all of the API that have been deprecated. A deprecated API is not recommended for use, generally due to improvements, and a replacement API is usually given. Deprecated APIs may be removed in future implementations.\n" +
        "[63-63;tl=6; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Index\n" +
        "[64-64;tl=6; nw=14;ld=0.071429]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "The  Index  contains an alphabetic list of all classes, interfaces, constructors, methods, and fields.\n" +
        "[65-65;tl=6; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Prev/Next\n" +
        "[66-66;tl=6; nw=15;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "These links take you to the next or previous class, interface, package, or related page.\n" +
        "[67-67;tl=6; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Frames/No Frames\n" +
        "[68-68;tl=6; nw=16;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "These links show and hide the HTML frames.  All pages are available with or without frames.\n" +
        "[69-69;tl=6; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "All Classes\n" +
        "[70-70;tl=6; nw=13;ld=0.153846]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "The  All Classes  link shows all classes and interfaces except non-static nested types.\n" +
        "[71-71;tl=6; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Serialized Form\n" +
        "[72-72;tl=6; nw=61;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Each serializable or externalizable class has a description of its serialization fields and methods. This information is of interest to re-implementors, not to developers using the API. While there is no link in the navigation bar, you can get to this information by going to any serialized class and clicking \"Serialized Form\" in the \"See also\" section of the class description.\n" +
        "[73-73;tl=6; nw=3;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/H2, de.l3s.boilerpipe/HEADING, de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Constant Field Values\n" +
        "[74-74;tl=6; nw=13;ld=0.230769]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "The  Constant Field Values  page lists the static final fields and their values.\n" +
        "[75-75;tl=3; nw=12;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[]\u001B[0m\n" +
        "This help file applies to API documentation generated using the standard doclet.\n" +
        "[76-76;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "   Overview \n" +
        "[77-77;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Package\n" +
        "[78-78;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Class\n" +
        "[79-79;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Use\n" +
        "[80-80;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " Tree \n" +
        "[81-81;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " Deprecated \n" +
        "[82-82;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " Index \n" +
        "[83-83;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Help\n" +
        "[84-84;tl=4; nw=2;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[]\u001B[0m\n" +
        "GWT 2.5.1\n" +
        "[85-85;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Prev\n" +
        "[86-86;tl=5; nw=1;ld=0.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        "Next\n" +
        "[87-87;tl=6; nw=1;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " Frames \n" +
        "[88-88;tl=6; nw=2;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " No Frames \n" +
        "[89-89;tl=6; nw=2;ld=1.0]	\u001B[0;35mboilerplate\u001B[0m,\u001B[1;30m[de.l3s.boilerpipe/LI]\u001B[0m\n" +
        " All Classes \n";
}
