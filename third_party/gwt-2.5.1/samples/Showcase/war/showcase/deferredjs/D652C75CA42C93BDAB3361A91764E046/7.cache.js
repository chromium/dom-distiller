function V7b(){W7b.call(this,false)}
function r8b(a,b){t8b.call(this,a,false);this.c=b}
function s8b(a,b){t8b.call(this,a,false);q8b(this,b)}
function u8b(a){t8b.call(this,'GWT',true);q8b(this,a)}
function cyb(a){this.d=a;this.c=iib(this.d.b)}
function z7b(a,b){return G7b(a,b,a.b.c)}
function Pb(a,b){$b((ie(),de),a,RT(bdb,szc,135,[(coc(),b?boc:aoc)]))}
function q8b(a,b){a.e=b;!!a.d&&U7b(a.d,a);if(b){b.db.tabIndex=-1;yf();Pb(a.db,true)}else{yf();Pb(a.db,false)}}
function G7b(a,b,c){if(c<0||c>a.b.c){throw new Vnc}a.p&&(b.db[VHc]=2,undefined);y7b(a,c,b.db);suc(a.b,c,b);return b}
function dib(a){var b,c;b=_T(a.b.ie(qIc),149);if(b==null){c=RT(gdb,tzc,1,[rIc,'R\xE9tablir','Couper','Copier','Coller']);a.b.ke(qIc,c);return c}else{return b}}
function eib(a){var b,c;b=_T(a.b.ie(sIc),149);if(b==null){c=RT(gdb,tzc,1,['Nouveau','Ouvrir',tIc,'R\xE9cent','Quitter']);a.b.ke(sIc,c);return c}else{return b}}
function hib(a){var b,c;b=_T(a.b.ie(wIc),149);if(b==null){c=RT(gdb,tzc,1,['Contenu','Biscuit de fortune','\xC0 propos de GWT']);a.b.ke(wIc,c);return c}else{return b}}
function gib(a){var b,c;b=_T(a.b.ie(vIc),149);if(b==null){c=RT(gdb,tzc,1,['T\xE9l\xE9charger','Exemples',JEc,'GWiTtez avec le programme']);a.b.ke(vIc,c);return c}else{return b}}
function fib(a){var b,c;b=_T(a.b.ie(uIc),149);if(b==null){c=RT(gdb,tzc,1,['P\xEAcher dans le d\xE9sert.txt','Comment apprivoiser un perroquet sauvage',"L'\xE9levage des \xE9meus pour les nuls"]);a.b.ke(uIc,c);return c}else{return b}}
function x8b(){var a;Ni(this,$doc.createElement(dGc));this.db[nCc]='gwt-MenuItemSeparator';a=$doc.createElement(sCc);nWb(this.db,a);a[nCc]='menuSeparatorInner'}
function iib(a){var b,c;b=_T(a.b.ie(xIc),149);if(b==null){c=RT(gdb,tzc,1,["Merci d'avoir s\xE9lectionn\xE9 une option de menu",'Une s\xE9lection vraiment pertinente',"N'avez-vous rien de mieux \xE0 faire que de s\xE9lectionner des options de menu?","Essayez quelque chose d'autre","ceci n'est qu'un menu!",'Un autre clic gaspill\xE9']);a.b.ke(xIc,c);return c}else{return b}}
function $xb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new cyb(a);n=new V7b;n.c=true;n.db.style[oCc]='500px';n.f=true;q=new W7b(true);p=fib(a.b);for(k=0;k<p.length;++k){x7b(q,new r8b(p[k],o))}d=new W7b(true);d.f=true;x7b(n,new s8b('Fichier',d));e=eib(a.b);for(k=0;k<e.length;++k){if(k==3){z7b(d,new x8b);x7b(d,new s8b(e[3],q));z7b(d,new x8b)}else{x7b(d,new r8b(e[k],o))}}b=new W7b(true);x7b(n,new s8b('\xC9dition',b));c=dib(a.b);for(k=0;k<c.length;++k){x7b(b,new r8b(c[k],o))}f=new W7b(true);x7b(n,new u8b(f));g=gib(a.b);for(k=0;k<g.length;++k){x7b(f,new r8b(g[k],o))}i=new W7b(true);z7b(n,new x8b);x7b(n,new s8b('Aide',i));j=hib(a.b);for(k=0;k<j.length;++k){x7b(i,new r8b(j[k],o))}Ygc(n.db,OBc,yIc);T7b(n,yIc);return n}
var yIc='cwMenuBar',qIc='cwMenuBarEditOptions',sIc='cwMenuBarFileOptions',uIc='cwMenuBarFileRecents',vIc='cwMenuBarGWTOptions',wIc='cwMenuBarHelpOptions',xIc='cwMenuBarPrompts';ieb(683,1,{},cyb);_.oc=function dyb(){dXb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;ieb(684,1,gAc);_.mc=function hyb(){Ngb(this.c,$xb(this.b))};ieb(1078,102,vzc,V7b);ieb(1085,103,{100:1,105:1,119:1},r8b,s8b,u8b);ieb(1086,103,{100:1,106:1,119:1},x8b);var t2=xoc(XGc,'CwMenuBar$1',683),f8=xoc(VGc,'MenuItemSeparator',1086);VAc(wn)(7);