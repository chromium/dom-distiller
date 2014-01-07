function n8b(){o8b.call(this,false)}
function L8b(a,b){N8b.call(this,a,false);this.b=b}
function M8b(a,b){N8b.call(this,a,false);K8b(this,b)}
function O8b(a){N8b.call(this,'GWT',true);K8b(this,a)}
function qyb(a){this.c=a;this.b=wib(this.c.a)}
function T7b(a,b){return $7b(a,b,a.a.b)}
function Pb(a,b){$b((ie(),de),a,YT(kdb,Xzc,136,[(Hoc(),b?Goc:Foc)]))}
function K8b(a,b){a.d=b;!!a.c&&m8b(a.c,a);if(b){b.cb.tabIndex=-1;yf();Pb(a.cb,true)}else{yf();Pb(a.cb,false)}}
function $7b(a,b,c){if(c<0||c>a.a.b){throw new yoc}a.o&&(b.cb[uIc]=2,undefined);S7b(a,c,b.cb);Xuc(a.a,c,b);return b}
function rib(a){var b,c;b=gU(a.a.ie(RIc),150);if(b==null){c=YT(pdb,Yzc,1,[SIc,'R\xE9tablir','Couper','Copier','Coller']);a.a.ke(RIc,c);return c}else{return b}}
function sib(a){var b,c;b=gU(a.a.ie(TIc),150);if(b==null){c=YT(pdb,Yzc,1,['Nouveau','Ouvrir',UIc,'R\xE9cent','Quitter']);a.a.ke(TIc,c);return c}else{return b}}
function vib(a){var b,c;b=gU(a.a.ie(XIc),150);if(b==null){c=YT(pdb,Yzc,1,['Contenu','Biscuit de fortune','\xC0 propos de GWT']);a.a.ke(XIc,c);return c}else{return b}}
function uib(a){var b,c;b=gU(a.a.ie(WIc),150);if(b==null){c=YT(pdb,Yzc,1,['T\xE9l\xE9charger','Exemples',jFc,'GWiTtez avec le programme']);a.a.ke(WIc,c);return c}else{return b}}
function tib(a){var b,c;b=gU(a.a.ie(VIc),150);if(b==null){c=YT(pdb,Yzc,1,['P\xEAcher dans le d\xE9sert.txt','Comment apprivoiser un perroquet sauvage',"L'\xE9levage des \xE9meus pour les nuls"]);a.a.ke(VIc,c);return c}else{return b}}
function R8b(){var a;Ni(this,$doc.createElement(DGc));this.cb[SCc]='gwt-MenuItemSeparator';a=$doc.createElement(XCc);GWb(this.cb,a);a[SCc]='menuSeparatorInner'}
function wib(a){var b,c;b=gU(a.a.ie(YIc),150);if(b==null){c=YT(pdb,Yzc,1,["Merci d'avoir s\xE9lectionn\xE9 une option de menu",'Une s\xE9lection vraiment pertinente',"N'avez-vous rien de mieux \xE0 faire que de s\xE9lectionner des options de menu?","Essayez quelque chose d'autre","ceci n'est qu'un menu!",'Un autre clic gaspill\xE9']);a.a.ke(YIc,c);return c}else{return b}}
function myb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new qyb(a);n=new n8b;n.b=true;n.cb.style[TCc]='500px';n.e=true;q=new o8b(true);p=tib(a.a);for(k=0;k<p.length;++k){R7b(q,new L8b(p[k],o))}d=new o8b(true);d.e=true;R7b(n,new M8b('Fichier',d));e=sib(a.a);for(k=0;k<e.length;++k){if(k==3){T7b(d,new R8b);R7b(d,new M8b(e[3],q));T7b(d,new R8b)}else{R7b(d,new L8b(e[k],o))}}b=new o8b(true);R7b(n,new M8b('\xC9dition',b));c=rib(a.a);for(k=0;k<c.length;++k){R7b(b,new L8b(c[k],o))}f=new o8b(true);R7b(n,new O8b(f));g=uib(a.a);for(k=0;k<g.length;++k){R7b(f,new L8b(g[k],o))}i=new o8b(true);T7b(n,new R8b);R7b(n,new M8b('Aide',i));j=vib(a.a);for(k=0;k<j.length;++k){R7b(i,new L8b(j[k],o))}uhc(n.cb,rCc,ZIc);l8b(n,ZIc);return n}
var ZIc='cwMenuBar',RIc='cwMenuBarEditOptions',TIc='cwMenuBarFileOptions',VIc='cwMenuBarFileRecents',WIc='cwMenuBarGWTOptions',XIc='cwMenuBarHelpOptions',YIc='cwMenuBarPrompts';reb(687,1,{},qyb);_.nc=function ryb(){vXb(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.a=0;_.c=null;reb(688,1,LAc);_.lc=function vyb(){_gb(this.b,myb(this.a))};reb(1085,102,$zc,n8b);reb(1092,103,{101:1,106:1,120:1},L8b,M8b,O8b);reb(1093,103,{101:1,107:1,120:1},R8b);var A2=apc(wHc,'CwMenuBar$1',687),n8=apc(uHc,'MenuItemSeparator',1093);yBc(wn)(7);