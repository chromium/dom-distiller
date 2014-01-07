function RCc(){SCc.call(this,false)}
function nDc(a,b){pDc.call(this,a,false);this.b=b}
function oDc(a,b){pDc.call(this,a,false);mDc(this,b)}
function qDc(a){pDc.call(this,'GWT',true);mDc(this,a)}
function N0b(a){this.c=a;this.b=TMb(this.c.a)}
function vCc(a,b){return CCc(a,b,a.a.b)}
function Pb(a,b){$b((ie(),de),a,llb(HHb,B2c,136,[(lTc(),b?kTc:jTc)]))}
function mDc(a,b){a.d=b;!!a.c&&QCc(a.c,a);if(b){b.cb.tabIndex=-1;yf();Pb(a.cb,true)}else{yf();Pb(a.cb,false)}}
function CCc(a,b,c){if(c<0||c>a.a.b){throw new cTc}a.o&&(b.cb[zbd]=2,undefined);uCc(a,c,b.cb);BZc(a.a,c,b);return b}
function tDc(){var a;Ni(this,zr($doc,D9c));this.cb[w5c]='gwt-MenuItemSeparator';a=zr($doc,C5c);ppc(this.cb,a);a[w5c]='menuSeparatorInner'}
function PMb(a){var b,c;b=vlb(a.a.ie(Xbd),150);if(b==null){c=llb(MHb,C2c,1,['New','Open',Ybd,Zbd,'Exit']);a.a.ke(Xbd,c);return c}else{return b}}
function OMb(a){var b,c;b=vlb(a.a.ie(Wbd),150);if(b==null){c=llb(MHb,C2c,1,['Undo','Redo','Cut','Copy','Paste']);a.a.ke(Wbd,c);return c}else{return b}}
function SMb(a){var b,c;b=vlb(a.a.ie(acd),150);if(b==null){c=llb(MHb,C2c,1,['Contents','Fortune Cookie','About GWT']);a.a.ke(acd,c);return c}else{return b}}
function RMb(a){var b,c;b=vlb(a.a.ie(_bd),150);if(b==null){c=llb(MHb,C2c,1,['Download','Examples',i8c,"GWT wit' the program"]);a.a.ke(_bd,c);return c}else{return b}}
function QMb(a){var b,c;b=vlb(a.a.ie($bd),150);if(b==null){c=llb(MHb,C2c,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.a.ke($bd,c);return c}else{return b}}
function TMb(a){var b,c;b=vlb(a.a.ie(bcd),150);if(b==null){c=llb(MHb,C2c,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.a.ke(bcd,c);return c}else{return b}}
function J0b(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new N0b(a);n=new RCc;n.b=true;n.cb.style[x5c]='500px';n.e=true;q=new SCc(true);p=QMb(a.a);for(k=0;k<p.length;++k){tCc(q,new nDc(p[k],o))}d=new SCc(true);d.e=true;tCc(n,new oDc('File',d));e=PMb(a.a);for(k=0;k<e.length;++k){if(k==3){vCc(d,new tDc);tCc(d,new oDc(e[3],q));vCc(d,new tDc)}else{tCc(d,new nDc(e[k],o))}}b=new SCc(true);tCc(n,new oDc('Edit',b));c=OMb(a.a);for(k=0;k<c.length;++k){tCc(b,new nDc(c[k],o))}f=new SCc(true);tCc(n,new qDc(f));g=RMb(a.a);for(k=0;k<g.length;++k){tCc(f,new nDc(g[k],o))}i=new SCc(true);vCc(n,new tDc);tCc(n,new oDc('Help',i));j=SMb(a.a);for(k=0;k<j.length;++k){tCc(i,new nDc(j[k],o))}YLc(n.cb,X4c,ccd);PCc(n,ccd);return n}
var ccd='cwMenuBar',Wbd='cwMenuBarEditOptions',Xbd='cwMenuBarFileOptions',$bd='cwMenuBarFileRecents',_bd='cwMenuBarGWTOptions',acd='cwMenuBarHelpOptions',bcd='cwMenuBarPrompts';OIb(748,1,{},N0b);_.nc=function O0b(){eqc(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.a=0;_.c=null;OIb(749,1,p3c);_.lc=function S0b(){wLb(this.b,J0b(this.a))};OIb(1144,102,E2c,RCc);OIb(1151,103,{101:1,106:1,120:1},nDc,oDc,qDc);OIb(1152,103,{101:1,107:1,120:1},tDc);var Xwb=GTc(xad,'CwMenuBar$1',748),KCb=GTc(vad,'MenuItemSeparator',1152);c4c(vn)(7);