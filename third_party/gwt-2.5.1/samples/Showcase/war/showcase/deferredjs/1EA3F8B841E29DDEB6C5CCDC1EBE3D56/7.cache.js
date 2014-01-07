function zCc(){ACc.call(this,false)}
function XCc(a,b){ZCc.call(this,a,false);this.b=b}
function YCc(a,b){ZCc.call(this,a,false);WCc(this,b)}
function $Cc(a){ZCc.call(this,'GWT',true);WCc(this,a)}
function C0b(a){this.c=a;this.b=IMb(this.c.a)}
function dCc(a,b){return kCc(a,b,a.a.b)}
function Pb(a,b){$b((ie(),de),a,alb(wHb,h2c,136,[(TSc(),b?SSc:RSc)]))}
function WCc(a,b){a.d=b;!!a.c&&yCc(a.c,a);if(b){b.cb.tabIndex=-1;yf();Pb(a.cb,true)}else{yf();Pb(a.cb,false)}}
function kCc(a,b,c){if(c<0||c>a.a.b){throw new KSc}a.o&&(b.cb[ebd]=2,undefined);cCc(a,c,b.cb);hZc(a.a,c,b);return b}
function EMb(a){var b,c;b=klb(a.a.ie(Cbd),150);if(b==null){c=alb(BHb,i2c,1,['New','Open',Dbd,Ebd,'Exit']);a.a.ke(Cbd,c);return c}else{return b}}
function DMb(a){var b,c;b=klb(a.a.ie(Bbd),150);if(b==null){c=alb(BHb,i2c,1,['Undo','Redo','Cut','Copy','Paste']);a.a.ke(Bbd,c);return c}else{return b}}
function HMb(a){var b,c;b=klb(a.a.ie(Hbd),150);if(b==null){c=alb(BHb,i2c,1,['Contents','Fortune Cookie','About GWT']);a.a.ke(Hbd,c);return c}else{return b}}
function GMb(a){var b,c;b=klb(a.a.ie(Gbd),150);if(b==null){c=alb(BHb,i2c,1,['Download','Examples',O7c,"GWT wit' the program"]);a.a.ke(Gbd,c);return c}else{return b}}
function FMb(a){var b,c;b=klb(a.a.ie(Fbd),150);if(b==null){c=alb(BHb,i2c,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.a.ke(Fbd,c);return c}else{return b}}
function bDc(){var a;Ni(this,$doc.createElement(j9c));this.cb[c5c]='gwt-MenuItemSeparator';a=$doc.createElement(h5c);Soc(this.cb,a);a[c5c]='menuSeparatorInner'}
function IMb(a){var b,c;b=klb(a.a.ie(Ibd),150);if(b==null){c=alb(BHb,i2c,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.a.ke(Ibd,c);return c}else{return b}}
function y0b(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new C0b(a);n=new zCc;n.b=true;n.cb.style[d5c]='500px';n.e=true;q=new ACc(true);p=FMb(a.a);for(k=0;k<p.length;++k){bCc(q,new XCc(p[k],o))}d=new ACc(true);d.e=true;bCc(n,new YCc('File',d));e=EMb(a.a);for(k=0;k<e.length;++k){if(k==3){dCc(d,new bDc);bCc(d,new YCc(e[3],q));dCc(d,new bDc)}else{bCc(d,new XCc(e[k],o))}}b=new ACc(true);bCc(n,new YCc('Edit',b));c=DMb(a.a);for(k=0;k<c.length;++k){bCc(b,new XCc(c[k],o))}f=new ACc(true);bCc(n,new $Cc(f));g=GMb(a.a);for(k=0;k<g.length;++k){bCc(f,new XCc(g[k],o))}i=new ACc(true);dCc(n,new bDc);bCc(n,new YCc('Help',i));j=HMb(a.a);for(k=0;k<j.length;++k){bCc(i,new XCc(j[k],o))}GLc(n.cb,D4c,Jbd);xCc(n,Jbd);return n}
var Jbd='cwMenuBar',Bbd='cwMenuBarEditOptions',Cbd='cwMenuBarFileOptions',Fbd='cwMenuBarFileRecents',Gbd='cwMenuBarGWTOptions',Hbd='cwMenuBarHelpOptions',Ibd='cwMenuBarPrompts';DIb(749,1,{},C0b);_.nc=function D0b(){Hpc(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.a=0;_.c=null;DIb(750,1,X2c);_.lc=function H0b(){lLb(this.b,y0b(this.a))};DIb(1147,102,k2c,zCc);DIb(1154,103,{101:1,106:1,120:1},XCc,YCc,$Cc);DIb(1155,103,{101:1,107:1,120:1},bDc);var Mwb=mTc(cad,'CwMenuBar$1',749),zCb=mTc(aad,'MenuItemSeparator',1155);K3c(wn)(7);