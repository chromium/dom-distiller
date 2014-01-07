function A8b(){B8b.call(this,false)}
function Y8b(a,b){$8b.call(this,a,false);this.c=b}
function Z8b(a,b){$8b.call(this,a,false);X8b(this,b)}
function _8b(a){$8b.call(this,'GWT',true);X8b(this,a)}
function Gyb(a){this.d=a;this.c=Mib(this.d.b)}
function e8b(a,b){return l8b(a,b,a.b.c)}
function $b(a,b){jc((te(),oe),a,qU(Fdb,jAc,135,[(Uoc(),b?Toc:Soc)]))}
function X8b(a,b){a.e=b;!!a.d&&z8b(a.d,a);if(b){b.db.tabIndex=-1;Jf();$b(a.db,true)}else{Jf();$b(a.db,false)}}
function l8b(a,b,c){if(c<0||c>a.b.c){throw new Loc}a.p&&(b.db[SIc]=2,undefined);d8b(a,c,b.db);ivc(a.b,c,b);return b}
function Hib(a){var b,c;b=AU(a.b.me(nJc),149);if(b==null){c=qU(Kdb,kAc,1,[oJc,'R\xE9tablir','Couper','Copier','Coller']);a.b.oe(nJc,c);return c}else{return b}}
function Iib(a){var b,c;b=AU(a.b.me(pJc),149);if(b==null){c=qU(Kdb,kAc,1,['Nouveau','Ouvrir',qJc,'R\xE9cent','Quitter']);a.b.oe(pJc,c);return c}else{return b}}
function Lib(a){var b,c;b=AU(a.b.me(tJc),149);if(b==null){c=qU(Kdb,kAc,1,['Contenu','Biscuit de fortune','\xC0 propos de GWT']);a.b.oe(tJc,c);return c}else{return b}}
function Kib(a){var b,c;b=AU(a.b.me(sJc),149);if(b==null){c=qU(Kdb,kAc,1,['T\xE9l\xE9charger','Exemples',zFc,'GWiTtez avec le programme']);a.b.oe(sJc,c);return c}else{return b}}
function Jib(a){var b,c;b=AU(a.b.me(rJc),149);if(b==null){c=qU(Kdb,kAc,1,['P\xEAcher dans le d\xE9sert.txt','Comment apprivoiser un perroquet sauvage',"L'\xE9levage des \xE9meus pour les nuls"]);a.b.oe(rJc,c);return c}else{return b}}
function c9b(){var a;Yi(this,$doc.createElement(_Gc));this.db[eDc]='gwt-MenuItemSeparator';a=$doc.createElement(jDc);UWb(this.db,a);a[eDc]='menuSeparatorInner'}
function Mib(a){var b,c;b=AU(a.b.me(uJc),149);if(b==null){c=qU(Kdb,kAc,1,["Merci d'avoir s\xE9lectionn\xE9 une option de menu",'Une s\xE9lection vraiment pertinente',"N'avez-vous rien de mieux \xE0 faire que de s\xE9lectionner des options de menu?","Essayez quelque chose d'autre","ceci n'est qu'un menu!",'Un autre clic gaspill\xE9']);a.b.oe(uJc,c);return c}else{return b}}
function Cyb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Gyb(a);n=new A8b;n.c=true;n.db.style[fDc]='500px';n.f=true;q=new B8b(true);p=Jib(a.b);for(k=0;k<p.length;++k){c8b(q,new Y8b(p[k],o))}d=new B8b(true);d.f=true;c8b(n,new Z8b('Fichier',d));e=Iib(a.b);for(k=0;k<e.length;++k){if(k==3){e8b(d,new c9b);c8b(d,new Z8b(e[3],q));e8b(d,new c9b)}else{c8b(d,new Y8b(e[k],o))}}b=new B8b(true);c8b(n,new Z8b('\xC9dition',b));c=Hib(a.b);for(k=0;k<c.length;++k){c8b(b,new Y8b(c[k],o))}f=new B8b(true);c8b(n,new _8b(f));g=Kib(a.b);for(k=0;k<g.length;++k){c8b(f,new Y8b(g[k],o))}i=new B8b(true);e8b(n,new c9b);c8b(n,new Z8b('Aide',i));j=Lib(a.b);for(k=0;k<j.length;++k){c8b(i,new Y8b(j[k],o))}Dhc(n.db,FCc,vJc);y8b(n,vJc);return n}
var vJc='cwMenuBar',nJc='cwMenuBarEditOptions',pJc='cwMenuBarFileOptions',rJc='cwMenuBarFileRecents',sJc='cwMenuBarGWTOptions',tJc='cwMenuBarHelpOptions',uJc='cwMenuBarPrompts';Meb(684,1,{},Gyb);_.sc=function Hyb(){JXb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;Meb(685,1,ZAc);_.qc=function Lyb(){phb(this.c,Cyb(this.b))};Meb(1080,104,mAc,A8b);Meb(1087,105,{100:1,105:1,119:1},Y8b,Z8b,_8b);Meb(1088,105,{100:1,106:1,119:1},c9b);var W2=npc(UHc,'CwMenuBar$1',684),I8=npc(SHc,'MenuItemSeparator',1088);MBc(In)(7);