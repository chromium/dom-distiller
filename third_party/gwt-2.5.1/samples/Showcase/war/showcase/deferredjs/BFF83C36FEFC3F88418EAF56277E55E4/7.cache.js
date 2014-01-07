function AXb(){BXb.call(this,false)}
function YXb(a,b){$Xb.call(this,a,false);this.c=b}
function ZXb(a,b){$Xb.call(this,a,false);XXb(this,b)}
function _Xb(a){$Xb.call(this,'GWT',true);XXb(this,a)}
function Glb(a){this.d=a;this.c=M5(this.d.b)}
function eXb(a,b){return lXb(a,b,a.b.c)}
function $b(a,b){jc((te(),oe),a,PH(F0,jnc,134,[(Ubc(),b?Tbc:Sbc)]))}
function XXb(a,b){a.e=b;!!a.d&&zXb(a.d,a);if(b){b.db.tabIndex=-1;Jf();$b(a.db,true)}else{Jf();$b(a.db,false)}}
function lXb(a,b,c){if(c<0||c>a.b.c){throw new Lbc}a.p&&(b.db[Svc]=2,undefined);dXb(a,c,b.db);iic(a.b,c,b);return b}
function I5(a){var b,c;b=ZH(a.b.pd(owc),148);if(b==null){c=PH(K0,knc,1,['New','Open',pwc,qwc,'Exit']);a.b.rd(owc,c);return c}else{return b}}
function H5(a){var b,c;b=ZH(a.b.pd(nwc),148);if(b==null){c=PH(K0,knc,1,['Undo','Redo','Cut','Copy','Paste']);a.b.rd(nwc,c);return c}else{return b}}
function L5(a){var b,c;b=ZH(a.b.pd(twc),148);if(b==null){c=PH(K0,knc,1,['Contents','Fortune Cookie','About GWT']);a.b.rd(twc,c);return c}else{return b}}
function K5(a){var b,c;b=ZH(a.b.pd(swc),148);if(b==null){c=PH(K0,knc,1,['Download','Examples',ssc,"GWT wit' the program"]);a.b.rd(swc,c);return c}else{return b}}
function J5(a){var b,c;b=ZH(a.b.pd(rwc),148);if(b==null){c=PH(K0,knc,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.rd(rwc,c);return c}else{return b}}
function cYb(){var a;Yi(this,$doc.createElement(Xtc));this.db[eqc]='gwt-MenuItemSeparator';a=$doc.createElement(jqc);UJb(this.db,a);a[eqc]='menuSeparatorInner'}
function M5(a){var b,c;b=ZH(a.b.pd(uwc),148);if(b==null){c=PH(K0,knc,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.rd(uwc,c);return c}else{return b}}
function Clb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Glb(a);n=new AXb;n.c=true;n.db.style[fqc]='500px';n.f=true;q=new BXb(true);p=J5(a.b);for(k=0;k<p.length;++k){cXb(q,new YXb(p[k],o))}d=new BXb(true);d.f=true;cXb(n,new ZXb('File',d));e=I5(a.b);for(k=0;k<e.length;++k){if(k==3){eXb(d,new cYb);cXb(d,new ZXb(e[3],q));eXb(d,new cYb)}else{cXb(d,new YXb(e[k],o))}}b=new BXb(true);cXb(n,new ZXb('Edit',b));c=H5(a.b);for(k=0;k<c.length;++k){cXb(b,new YXb(c[k],o))}f=new BXb(true);cXb(n,new _Xb(f));g=K5(a.b);for(k=0;k<g.length;++k){cXb(f,new YXb(g[k],o))}i=new BXb(true);eXb(n,new cYb);cXb(n,new ZXb('Help',i));j=L5(a.b);for(k=0;k<j.length;++k){cXb(i,new YXb(j[k],o))}D4b(n.db,Fpc,vwc);yXb(n,vwc);return n}
var vwc='cwMenuBar',nwc='cwMenuBarEditOptions',owc='cwMenuBarFileOptions',rwc='cwMenuBarFileRecents',swc='cwMenuBarGWTOptions',twc='cwMenuBarHelpOptions',uwc='cwMenuBarPrompts';M1(659,1,{},Glb);_.sc=function Hlb(){JKb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;M1(660,1,Znc);_.qc=function Llb(){p4(this.c,Clb(this.b))};M1(1055,104,mnc,AXb);M1(1062,105,{99:1,104:1,118:1},YXb,ZXb,_Xb);M1(1063,105,{99:1,105:1,118:1},cYb);var WR=ncc(Quc,'CwMenuBar$1',659),IX=ncc(Ouc,'MenuItemSeparator',1063);Moc(In)(7);