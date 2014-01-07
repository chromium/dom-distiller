function fCc(){gCc.call(this,false)}
function DCc(a,b){FCc.call(this,a,false);this.c=b}
function ECc(a,b){FCc.call(this,a,false);CCc(this,b)}
function GCc(a){FCc.call(this,'GWT',true);CCc(this,a)}
function o0b(a){this.d=a;this.c=uMb(this.d.b)}
function LBc(a,b){return SBc(a,b,a.b.c)}
function Pb(a,b){$b((ie(),de),a,Vkb(nHb,E1c,135,[(oSc(),b?nSc:mSc)]))}
function CCc(a,b){a.e=b;!!a.d&&eCc(a.d,a);if(b){b.db.tabIndex=-1;yf();Pb(a.db,true)}else{yf();Pb(a.db,false)}}
function SBc(a,b,c){if(c<0||c>a.b.c){throw new fSc}a.p&&(b.db[Fad]=2,undefined);KBc(a,c,b.db);EYc(a.b,c,b);return b}
function qMb(a){var b,c;b=dlb(a.b.ie(bbd),149);if(b==null){c=Vkb(sHb,F1c,1,['New','Open',cbd,dbd,'Exit']);a.b.ke(bbd,c);return c}else{return b}}
function pMb(a){var b,c;b=dlb(a.b.ie(abd),149);if(b==null){c=Vkb(sHb,F1c,1,['Undo','Redo','Cut','Copy','Paste']);a.b.ke(abd,c);return c}else{return b}}
function tMb(a){var b,c;b=dlb(a.b.ie(gbd),149);if(b==null){c=Vkb(sHb,F1c,1,['Contents','Fortune Cookie','About GWT']);a.b.ke(gbd,c);return c}else{return b}}
function sMb(a){var b,c;b=dlb(a.b.ie(fbd),149);if(b==null){c=Vkb(sHb,F1c,1,['Download','Examples',m7c,"GWT wit' the program"]);a.b.ke(fbd,c);return c}else{return b}}
function rMb(a){var b,c;b=dlb(a.b.ie(ebd),149);if(b==null){c=Vkb(sHb,F1c,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.ke(ebd,c);return c}else{return b}}
function JCc(){var a;Ni(this,$doc.createElement(L8c));this.db[z4c]='gwt-MenuItemSeparator';a=$doc.createElement(E4c);zoc(this.db,a);a[z4c]='menuSeparatorInner'}
function uMb(a){var b,c;b=dlb(a.b.ie(hbd),149);if(b==null){c=Vkb(sHb,F1c,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.ke(hbd,c);return c}else{return b}}
function k0b(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new o0b(a);n=new fCc;n.c=true;n.db.style[A4c]='500px';n.f=true;q=new gCc(true);p=rMb(a.b);for(k=0;k<p.length;++k){JBc(q,new DCc(p[k],o))}d=new gCc(true);d.f=true;JBc(n,new ECc('File',d));e=qMb(a.b);for(k=0;k<e.length;++k){if(k==3){LBc(d,new JCc);JBc(d,new ECc(e[3],q));LBc(d,new JCc)}else{JBc(d,new DCc(e[k],o))}}b=new gCc(true);JBc(n,new ECc('Edit',b));c=pMb(a.b);for(k=0;k<c.length;++k){JBc(b,new DCc(c[k],o))}f=new gCc(true);JBc(n,new GCc(f));g=sMb(a.b);for(k=0;k<g.length;++k){JBc(f,new DCc(g[k],o))}i=new gCc(true);LBc(n,new JCc);JBc(n,new ECc('Help',i));j=tMb(a.b);for(k=0;k<j.length;++k){JBc(i,new DCc(j[k],o))}iLc(n.db,$3c,ibd);dCc(n,ibd);return n}
var ibd='cwMenuBar',abd='cwMenuBarEditOptions',bbd='cwMenuBarFileOptions',ebd='cwMenuBarFileRecents',fbd='cwMenuBarGWTOptions',gbd='cwMenuBarHelpOptions',hbd='cwMenuBarPrompts';uIb(745,1,{},o0b);_.oc=function p0b(){ppc(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;uIb(746,1,s2c);_.mc=function t0b(){ZKb(this.c,k0b(this.b))};uIb(1140,102,H1c,fCc);uIb(1147,103,{100:1,105:1,119:1},DCc,ECc,GCc);uIb(1148,103,{100:1,106:1,119:1},JCc);var Fwb=JSc(D9c,'CwMenuBar$1',745),rCb=JSc(B9c,'MenuItemSeparator',1148);f3c(wn)(7);