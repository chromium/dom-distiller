function F8b(){G8b.call(this,false)}
function b9b(a,b){d9b.call(this,a,false);this.b=b}
function c9b(a,b){d9b.call(this,a,false);a9b(this,b)}
function e9b(a){d9b.call(this,'GWT',true);a9b(this,a)}
function Byb(a){this.c=a;this.b=Hib(this.c.a)}
function j8b(a,b){return q8b(a,b,a.a.b)}
function Pb(a,b){$b((ie(),de),a,hU(vdb,pAc,136,[(_oc(),b?$oc:Zoc)]))}
function a9b(a,b){a.d=b;!!a.c&&E8b(a.c,a);if(b){b.cb.tabIndex=-1;yf();Pb(a.cb,true)}else{yf();Pb(a.cb,false)}}
function q8b(a,b,c){if(c<0||c>a.a.b){throw new Soc}a.o&&(b.cb[PIc]=2,undefined);i8b(a,c,b.cb);pvc(a.a,c,b);return b}
function h9b(){var a;Ni(this,zr($doc,XGc));this.cb[kDc]='gwt-MenuItemSeparator';a=zr($doc,qDc);dXb(this.cb,a);a[kDc]='menuSeparatorInner'}
function Cib(a){var b,c;b=rU(a.a.ie(kJc),150);if(b==null){c=hU(Adb,qAc,1,[lJc,'R\xE9tablir','Couper','Copier','Coller']);a.a.ke(kJc,c);return c}else{return b}}
function Dib(a){var b,c;b=rU(a.a.ie(mJc),150);if(b==null){c=hU(Adb,qAc,1,['Nouveau','Ouvrir',nJc,'R\xE9cent','Quitter']);a.a.ke(mJc,c);return c}else{return b}}
function Gib(a){var b,c;b=rU(a.a.ie(qJc),150);if(b==null){c=hU(Adb,qAc,1,['Contenu','Biscuit de fortune','\xC0 propos de GWT']);a.a.ke(qJc,c);return c}else{return b}}
function Fib(a){var b,c;b=rU(a.a.ie(pJc),150);if(b==null){c=hU(Adb,qAc,1,['T\xE9l\xE9charger','Exemples',FFc,'GWiTtez avec le programme']);a.a.ke(pJc,c);return c}else{return b}}
function Eib(a){var b,c;b=rU(a.a.ie(oJc),150);if(b==null){c=hU(Adb,qAc,1,['P\xEAcher dans le d\xE9sert.txt','Comment apprivoiser un perroquet sauvage',"L'\xE9levage des \xE9meus pour les nuls"]);a.a.ke(oJc,c);return c}else{return b}}
function Hib(a){var b,c;b=rU(a.a.ie(rJc),150);if(b==null){c=hU(Adb,qAc,1,["Merci d'avoir s\xE9lectionn\xE9 une option de menu",'Une s\xE9lection vraiment pertinente',"N'avez-vous rien de mieux \xE0 faire que de s\xE9lectionner des options de menu?","Essayez quelque chose d'autre","ceci n'est qu'un menu!",'Un autre clic gaspill\xE9']);a.a.ke(rJc,c);return c}else{return b}}
function xyb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Byb(a);n=new F8b;n.b=true;n.cb.style[lDc]='500px';n.e=true;q=new G8b(true);p=Eib(a.a);for(k=0;k<p.length;++k){h8b(q,new b9b(p[k],o))}d=new G8b(true);d.e=true;h8b(n,new c9b('Fichier',d));e=Dib(a.a);for(k=0;k<e.length;++k){if(k==3){j8b(d,new h9b);h8b(d,new c9b(e[3],q));j8b(d,new h9b)}else{h8b(d,new b9b(e[k],o))}}b=new G8b(true);h8b(n,new c9b('\xC9dition',b));c=Cib(a.a);for(k=0;k<c.length;++k){h8b(b,new b9b(c[k],o))}f=new G8b(true);h8b(n,new e9b(f));g=Fib(a.a);for(k=0;k<g.length;++k){h8b(f,new b9b(g[k],o))}i=new G8b(true);j8b(n,new h9b);h8b(n,new c9b('Aide',i));j=Gib(a.a);for(k=0;k<j.length;++k){h8b(i,new b9b(j[k],o))}Mhc(n.cb,LCc,sJc);D8b(n,sJc);return n}
var sJc='cwMenuBar',kJc='cwMenuBarEditOptions',mJc='cwMenuBarFileOptions',oJc='cwMenuBarFileRecents',pJc='cwMenuBarGWTOptions',qJc='cwMenuBarHelpOptions',rJc='cwMenuBarPrompts';Ceb(686,1,{},Byb);_.nc=function Cyb(){UXb(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.a=0;_.c=null;Ceb(687,1,dBc);_.lc=function Gyb(){khb(this.b,xyb(this.a))};Ceb(1082,102,sAc,F8b);Ceb(1089,103,{101:1,106:1,120:1},b9b,c9b,e9b);Ceb(1090,103,{101:1,107:1,120:1},h9b);var L2=upc(RHc,'CwMenuBar$1',686),y8=upc(PHc,'MenuItemSeparator',1090);SBc(vn)(7);