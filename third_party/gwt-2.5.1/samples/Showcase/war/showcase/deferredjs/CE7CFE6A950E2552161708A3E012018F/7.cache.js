function dDc(){eDc.call(this,false)}
function BDc(a,b){DDc.call(this,a,false);this.c=b}
function CDc(a,b){DDc.call(this,a,false);ADc(this,b)}
function EDc(a){DDc.call(this,'GWT',true);ADc(this,a)}
function j1b(a){this.d=a;this.c=pNb(this.d.b)}
function ICc(a,b){return QCc(a,b,a.b.c)}
function _b(a,b){kc((ue(),pe),a,Hlb(iIb,T2c,135,[(BTc(),b?ATc:zTc)]))}
function ADc(a,b){a.e=b;!!a.d&&cDc(a.d,a);if(b){(eAc(),b.db).tabIndex=-1;Kf();_b(a.db,true)}else{Kf();_b(a.db,false)}}
function QCc(a,b,c){if(c<0||c>a.b.c){throw new sTc}a.p&&(b.db[$bd]=2,undefined);HCc(a,c,b.db);SZc(a.b,c,b);return b}
function lNb(a){var b,c;b=Rlb(a.b.qe(wcd),149);if(b==null){c=Hlb(nIb,U2c,1,['New','Open',xcd,ycd,'Exit']);a.b.se(wcd,c);return c}else{return b}}
function kNb(a){var b,c;b=Rlb(a.b.qe(vcd),149);if(b==null){c=Hlb(nIb,U2c,1,['Undo','Redo','Cut','Copy','Paste']);a.b.se(vcd,c);return c}else{return b}}
function oNb(a){var b,c;b=Rlb(a.b.qe(Bcd),149);if(b==null){c=Hlb(nIb,U2c,1,['Contents','Fortune Cookie','About GWT']);a.b.se(Bcd,c);return c}else{return b}}
function nNb(a){var b,c;b=Rlb(a.b.qe(Acd),149);if(b==null){c=Hlb(nIb,U2c,1,['Download','Examples',H8c,"GWT wit' the program"]);a.b.se(Acd,c);return c}else{return b}}
function mNb(a){var b,c;b=Rlb(a.b.qe(zcd),149);if(b==null){c=Hlb(nIb,U2c,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.se(zcd,c);return c}else{return b}}
function HDc(){var a;Zi(this,$doc.createElement(dad));this.db[P5c]='gwt-MenuItemSeparator';a=$doc.createElement(U5c);upc(this.db,a);a[P5c]='menuSeparatorInner'}
function pNb(a){var b,c;b=Rlb(a.b.qe(Ccd),149);if(b==null){c=Hlb(nIb,U2c,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.se(Ccd,c);return c}else{return b}}
function f1b(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new j1b(a);n=new dDc;n.c=true;n.db.style[Q5c]='500px';n.f=true;q=new eDc(true);p=mNb(a.b);for(k=0;k<p.length;++k){GCc(q,new BDc(p[k],o))}d=new eDc(true);d.f=true;GCc(n,new CDc('File',d));e=lNb(a.b);for(k=0;k<e.length;++k){if(k==3){ICc(d,new HDc);GCc(d,new CDc(e[3],q));ICc(d,new HDc)}else{GCc(d,new BDc(e[k],o))}}b=new eDc(true);GCc(n,new CDc('Edit',b));c=kNb(a.b);for(k=0;k<c.length;++k){GCc(b,new BDc(c[k],o))}f=new eDc(true);GCc(n,new EDc(f));g=nNb(a.b);for(k=0;k<g.length;++k){GCc(f,new BDc(g[k],o))}i=new eDc(true);ICc(n,new HDc);GCc(n,new CDc('Help',i));j=oNb(a.b);for(k=0;k<j.length;++k){GCc(i,new BDc(j[k],o))}gMc(n.db,o5c,Dcd);bDc(n,Dcd);return n}
var Dcd='cwMenuBar',vcd='cwMenuBarEditOptions',wcd='cwMenuBarFileOptions',zcd='cwMenuBarFileRecents',Acd='cwMenuBarGWTOptions',Bcd='cwMenuBarHelpOptions',Ccd='cwMenuBarPrompts';pJb(750,1,{},j1b);_.sc=function k1b(){kqc(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;pJb(751,1,H3c);_.qc=function o1b(){ULb(this.c,f1b(this.b))};pJb(1145,104,W2c,dDc);pJb(1152,105,{100:1,105:1,119:1},BDc,CDc,EDc);pJb(1153,105,{100:1,106:1,119:1},HDc);var xxb=WTc(Yad,'CwMenuBar$1',750),jDb=WTc(Wad,'MenuItemSeparator',1153);u4c(Jn)(7);