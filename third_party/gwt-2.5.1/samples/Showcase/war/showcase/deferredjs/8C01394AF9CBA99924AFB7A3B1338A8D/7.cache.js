function nXb(){oXb.call(this,false)}
function LXb(a,b){NXb.call(this,a,false);this.b=b}
function MXb(a,b){NXb.call(this,a,false);KXb(this,b)}
function OXb(a){NXb.call(this,'GWT',true);KXb(this,a)}
function qlb(a){this.c=a;this.b=w5(this.c.a)}
function TWb(a,b){return $Wb(a,b,a.a.b)}
function Pb(a,b){$b((ie(),de),a,vH(k0,Xmc,135,[(Hbc(),b?Gbc:Fbc)]))}
function KXb(a,b){a.d=b;!!a.c&&mXb(a.c,a);if(b){b.cb.tabIndex=-1;yf();Pb(a.cb,true)}else{yf();Pb(a.cb,false)}}
function $Wb(a,b,c){if(c<0||c>a.a.b){throw new ybc}a.o&&(b.cb[uvc]=2,undefined);SWb(a,c,b.cb);Xhc(a.a,c,b);return b}
function s5(a){var b,c;b=FH(a.a.ld(Svc),149);if(b==null){c=vH(p0,Ymc,1,['New','Open',Tvc,Uvc,'Exit']);a.a.nd(Svc,c);return c}else{return b}}
function r5(a){var b,c;b=FH(a.a.ld(Rvc),149);if(b==null){c=vH(p0,Ymc,1,['Undo','Redo','Cut','Copy','Paste']);a.a.nd(Rvc,c);return c}else{return b}}
function v5(a){var b,c;b=FH(a.a.ld(Xvc),149);if(b==null){c=vH(p0,Ymc,1,['Contents','Fortune Cookie','About GWT']);a.a.nd(Xvc,c);return c}else{return b}}
function u5(a){var b,c;b=FH(a.a.ld(Wvc),149);if(b==null){c=vH(p0,Ymc,1,['Download','Examples',csc,"GWT wit' the program"]);a.a.nd(Wvc,c);return c}else{return b}}
function t5(a){var b,c;b=FH(a.a.ld(Vvc),149);if(b==null){c=vH(p0,Ymc,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.a.nd(Vvc,c);return c}else{return b}}
function RXb(){var a;Ni(this,$doc.createElement(ztc));this.cb[Spc]='gwt-MenuItemSeparator';a=$doc.createElement(Xpc);GJb(this.cb,a);a[Spc]='menuSeparatorInner'}
function w5(a){var b,c;b=FH(a.a.ld(Yvc),149);if(b==null){c=vH(p0,Ymc,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.a.nd(Yvc,c);return c}else{return b}}
function mlb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new qlb(a);n=new nXb;n.b=true;n.cb.style[Tpc]='500px';n.e=true;q=new oXb(true);p=t5(a.a);for(k=0;k<p.length;++k){RWb(q,new LXb(p[k],o))}d=new oXb(true);d.e=true;RWb(n,new MXb('File',d));e=s5(a.a);for(k=0;k<e.length;++k){if(k==3){TWb(d,new RXb);RWb(d,new MXb(e[3],q));TWb(d,new RXb)}else{RWb(d,new LXb(e[k],o))}}b=new oXb(true);RWb(n,new MXb('Edit',b));c=r5(a.a);for(k=0;k<c.length;++k){RWb(b,new LXb(c[k],o))}f=new oXb(true);RWb(n,new OXb(f));g=u5(a.a);for(k=0;k<g.length;++k){RWb(f,new LXb(g[k],o))}i=new oXb(true);TWb(n,new RXb);RWb(n,new MXb('Help',i));j=v5(a.a);for(k=0;k<j.length;++k){RWb(i,new LXb(j[k],o))}u4b(n.cb,rpc,Zvc);lXb(n,Zvc);return n}
var Zvc='cwMenuBar',Rvc='cwMenuBarEditOptions',Svc='cwMenuBarFileOptions',Vvc='cwMenuBarFileRecents',Wvc='cwMenuBarGWTOptions',Xvc='cwMenuBarHelpOptions',Yvc='cwMenuBarPrompts';r1(662,1,{},qlb);_.nc=function rlb(){vKb(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.a=0;_.c=null;r1(663,1,Lnc);_.lc=function vlb(){_3(this.b,mlb(this.a))};r1(1060,102,$mc,nXb);r1(1067,103,{100:1,105:1,119:1},LXb,MXb,OXb);r1(1068,103,{100:1,106:1,119:1},RXb);var AR=acc(suc,'CwMenuBar$1',662),nX=acc(quc,'MenuItemSeparator',1068);yoc(wn)(7);