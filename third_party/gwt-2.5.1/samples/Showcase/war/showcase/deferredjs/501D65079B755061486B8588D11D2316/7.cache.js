function T8b(){U8b.call(this,false)}
function p9b(a,b){r9b.call(this,a,false);this.c=b}
function q9b(a,b){r9b.call(this,a,false);o9b(this,b)}
function s9b(a){r9b.call(this,'GWT',true);o9b(this,a)}
function Zyb(a){this.d=a;this.c=djb(this.d.b)}
function w8b(a,b){return E8b(a,b,a.b.c)}
function _b(a,b){kc((ue(),pe),a,DU(Ydb,HAc,135,[(ppc(),b?opc:npc)]))}
function E8b(a,b,c){if(c<0||c>a.b.c){throw new gpc}a.p&&(b.db[oJc]=2,undefined);v8b(a,c,b.db);Gvc(a.b,c,b);return b}
function o9b(a,b){a.e=b;!!a.d&&S8b(a.d,a);if(b){(U5b(),b.db).tabIndex=-1;Kf();_b(a.db,true)}else{Kf();_b(a.db,false)}}
function $ib(a){var b,c;b=NU(a.b.qe(LJc),149);if(b==null){c=DU(beb,IAc,1,[MJc,'R\xE9tablir','Couper','Copier','Coller']);a.b.se(LJc,c);return c}else{return b}}
function _ib(a){var b,c;b=NU(a.b.qe(NJc),149);if(b==null){c=DU(beb,IAc,1,['Nouveau','Ouvrir',OJc,'R\xE9cent','Quitter']);a.b.se(NJc,c);return c}else{return b}}
function cjb(a){var b,c;b=NU(a.b.qe(RJc),149);if(b==null){c=DU(beb,IAc,1,['Contenu','Biscuit de fortune','\xC0 propos de GWT']);a.b.se(RJc,c);return c}else{return b}}
function bjb(a){var b,c;b=NU(a.b.qe(QJc),149);if(b==null){c=DU(beb,IAc,1,['T\xE9l\xE9charger','Exemples',cGc,'GWiTtez avec le programme']);a.b.se(QJc,c);return c}else{return b}}
function ajb(a){var b,c;b=NU(a.b.qe(PJc),149);if(b==null){c=DU(beb,IAc,1,['P\xEAcher dans le d\xE9sert.txt','Comment apprivoiser un perroquet sauvage',"L'\xE9levage des \xE9meus pour les nuls"]);a.b.se(PJc,c);return c}else{return b}}
function v9b(){var a;Zi(this,$doc.createElement(xHc));this.db[DDc]='gwt-MenuItemSeparator';a=$doc.createElement(IDc);iXb(this.db,a);a[DDc]='menuSeparatorInner'}
function djb(a){var b,c;b=NU(a.b.qe(SJc),149);if(b==null){c=DU(beb,IAc,1,["Merci d'avoir s\xE9lectionn\xE9 une option de menu",'Une s\xE9lection vraiment pertinente',"N'avez-vous rien de mieux \xE0 faire que de s\xE9lectionner des options de menu?","Essayez quelque chose d'autre","ceci n'est qu'un menu!",'Un autre clic gaspill\xE9']);a.b.se(SJc,c);return c}else{return b}}
function Vyb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Zyb(a);n=new T8b;n.c=true;n.db.style[EDc]='500px';n.f=true;q=new U8b(true);p=ajb(a.b);for(k=0;k<p.length;++k){u8b(q,new p9b(p[k],o))}d=new U8b(true);d.f=true;u8b(n,new q9b('Fichier',d));e=_ib(a.b);for(k=0;k<e.length;++k){if(k==3){w8b(d,new v9b);u8b(d,new q9b(e[3],q));w8b(d,new v9b)}else{u8b(d,new p9b(e[k],o))}}b=new U8b(true);u8b(n,new q9b('\xC9dition',b));c=$ib(a.b);for(k=0;k<c.length;++k){u8b(b,new p9b(c[k],o))}f=new U8b(true);u8b(n,new s9b(f));g=bjb(a.b);for(k=0;k<g.length;++k){u8b(f,new p9b(g[k],o))}i=new U8b(true);w8b(n,new v9b);u8b(n,new q9b('Aide',i));j=cjb(a.b);for(k=0;k<j.length;++k){u8b(i,new p9b(j[k],o))}Whc(n.db,cDc,TJc);R8b(n,TJc);return n}
var TJc='cwMenuBar',LJc='cwMenuBarEditOptions',NJc='cwMenuBarFileOptions',PJc='cwMenuBarFileRecents',QJc='cwMenuBarGWTOptions',RJc='cwMenuBarHelpOptions',SJc='cwMenuBarPrompts';dfb(688,1,{},Zyb);_.sc=function $yb(){$Xb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;dfb(689,1,vBc);_.qc=function czb(){Ihb(this.c,Vyb(this.b))};dfb(1083,104,KAc,T8b);dfb(1090,105,{100:1,105:1,119:1},p9b,q9b,s9b);dfb(1091,105,{100:1,106:1,119:1},v9b);var l3=Kpc(qIc,'CwMenuBar$1',688),Z8=Kpc(oIc,'MenuItemSeparator',1091);iCc(Jn)(7);