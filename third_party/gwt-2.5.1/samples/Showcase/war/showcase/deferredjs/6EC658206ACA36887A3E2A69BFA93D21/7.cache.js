function TXb(){UXb.call(this,false)}
function pYb(a,b){rYb.call(this,a,false);this.c=b}
function qYb(a,b){rYb.call(this,a,false);oYb(this,b)}
function sYb(a){rYb.call(this,'GWT',true);oYb(this,a)}
function Zlb(a){this.d=a;this.c=d6(this.d.b)}
function wXb(a,b){return EXb(a,b,a.b.c)}
function _b(a,b){kc((ue(),pe),a,aI(Y0,Hnc,134,[(pcc(),b?occ:ncc)]))}
function EXb(a,b,c){if(c<0||c>a.b.c){throw new gcc}a.p&&(b.db[owc]=2,undefined);vXb(a,c,b.db);Gic(a.b,c,b);return b}
function oYb(a,b){a.e=b;!!a.d&&SXb(a.d,a);if(b){(UUb(),b.db).tabIndex=-1;Kf();_b(a.db,true)}else{Kf();_b(a.db,false)}}
function _5(a){var b,c;b=kI(a.b.td(Mwc),148);if(b==null){c=aI(b1,Inc,1,['New','Open',Nwc,Owc,'Exit']);a.b.vd(Mwc,c);return c}else{return b}}
function $5(a){var b,c;b=kI(a.b.td(Lwc),148);if(b==null){c=aI(b1,Inc,1,['Undo','Redo','Cut','Copy','Paste']);a.b.vd(Lwc,c);return c}else{return b}}
function c6(a){var b,c;b=kI(a.b.td(Rwc),148);if(b==null){c=aI(b1,Inc,1,['Contents','Fortune Cookie','About GWT']);a.b.vd(Rwc,c);return c}else{return b}}
function b6(a){var b,c;b=kI(a.b.td(Qwc),148);if(b==null){c=aI(b1,Inc,1,['Download','Examples',Xsc,"GWT wit' the program"]);a.b.vd(Qwc,c);return c}else{return b}}
function a6(a){var b,c;b=kI(a.b.td(Pwc),148);if(b==null){c=aI(b1,Inc,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.vd(Pwc,c);return c}else{return b}}
function vYb(){var a;Zi(this,$doc.createElement(tuc));this.db[Dqc]='gwt-MenuItemSeparator';a=$doc.createElement(Iqc);iKb(this.db,a);a[Dqc]='menuSeparatorInner'}
function d6(a){var b,c;b=kI(a.b.td(Swc),148);if(b==null){c=aI(b1,Inc,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.vd(Swc,c);return c}else{return b}}
function Vlb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Zlb(a);n=new TXb;n.c=true;n.db.style[Eqc]='500px';n.f=true;q=new UXb(true);p=a6(a.b);for(k=0;k<p.length;++k){uXb(q,new pYb(p[k],o))}d=new UXb(true);d.f=true;uXb(n,new qYb('File',d));e=_5(a.b);for(k=0;k<e.length;++k){if(k==3){wXb(d,new vYb);uXb(d,new qYb(e[3],q));wXb(d,new vYb)}else{uXb(d,new pYb(e[k],o))}}b=new UXb(true);uXb(n,new qYb('Edit',b));c=$5(a.b);for(k=0;k<c.length;++k){uXb(b,new pYb(c[k],o))}f=new UXb(true);uXb(n,new sYb(f));g=b6(a.b);for(k=0;k<g.length;++k){uXb(f,new pYb(g[k],o))}i=new UXb(true);wXb(n,new vYb);uXb(n,new qYb('Help',i));j=c6(a.b);for(k=0;k<j.length;++k){uXb(i,new pYb(j[k],o))}W4b(n.db,cqc,Twc);RXb(n,Twc);return n}
var Twc='cwMenuBar',Lwc='cwMenuBarEditOptions',Mwc='cwMenuBarFileOptions',Pwc='cwMenuBarFileRecents',Qwc='cwMenuBarGWTOptions',Rwc='cwMenuBarHelpOptions',Swc='cwMenuBarPrompts';d2(663,1,{},Zlb);_.sc=function $lb(){$Kb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;d2(664,1,voc);_.qc=function cmb(){I4(this.c,Vlb(this.b))};d2(1058,104,Knc,TXb);d2(1065,105,{99:1,104:1,118:1},pYb,qYb,sYb);d2(1066,105,{99:1,105:1,118:1},vYb);var lS=Kcc(mvc,'CwMenuBar$1',663),ZX=Kcc(kvc,'MenuItemSeparator',1066);ipc(Jn)(7);