function FXb(){GXb.call(this,false)}
function bYb(a,b){dYb.call(this,a,false);this.b=b}
function cYb(a,b){dYb.call(this,a,false);aYb(this,b)}
function eYb(a){dYb.call(this,'GWT',true);aYb(this,a)}
function Blb(a){this.c=a;this.b=H5(this.c.a)}
function jXb(a,b){return qXb(a,b,a.a.b)}
function Pb(a,b){$b((ie(),de),a,GH(v0,pnc,135,[(_bc(),b?$bc:Zbc)]))}
function aYb(a,b){a.d=b;!!a.c&&EXb(a.c,a);if(b){b.cb.tabIndex=-1;yf();Pb(a.cb,true)}else{yf();Pb(a.cb,false)}}
function qXb(a,b,c){if(c<0||c>a.a.b){throw new Sbc}a.o&&(b.cb[Pvc]=2,undefined);iXb(a,c,b.cb);pic(a.a,c,b);return b}
function hYb(){var a;Ni(this,zr($doc,Ttc));this.cb[kqc]='gwt-MenuItemSeparator';a=zr($doc,qqc);dKb(this.cb,a);a[kqc]='menuSeparatorInner'}
function D5(a){var b,c;b=QH(a.a.ld(lwc),149);if(b==null){c=GH(A0,qnc,1,['New','Open',mwc,nwc,'Exit']);a.a.nd(lwc,c);return c}else{return b}}
function C5(a){var b,c;b=QH(a.a.ld(kwc),149);if(b==null){c=GH(A0,qnc,1,['Undo','Redo','Cut','Copy','Paste']);a.a.nd(kwc,c);return c}else{return b}}
function G5(a){var b,c;b=QH(a.a.ld(qwc),149);if(b==null){c=GH(A0,qnc,1,['Contents','Fortune Cookie','About GWT']);a.a.nd(qwc,c);return c}else{return b}}
function F5(a){var b,c;b=QH(a.a.ld(pwc),149);if(b==null){c=GH(A0,qnc,1,['Download','Examples',ysc,"GWT wit' the program"]);a.a.nd(pwc,c);return c}else{return b}}
function E5(a){var b,c;b=QH(a.a.ld(owc),149);if(b==null){c=GH(A0,qnc,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.a.nd(owc,c);return c}else{return b}}
function H5(a){var b,c;b=QH(a.a.ld(rwc),149);if(b==null){c=GH(A0,qnc,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.a.nd(rwc,c);return c}else{return b}}
function xlb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Blb(a);n=new FXb;n.b=true;n.cb.style[lqc]='500px';n.e=true;q=new GXb(true);p=E5(a.a);for(k=0;k<p.length;++k){hXb(q,new bYb(p[k],o))}d=new GXb(true);d.e=true;hXb(n,new cYb('File',d));e=D5(a.a);for(k=0;k<e.length;++k){if(k==3){jXb(d,new hYb);hXb(d,new cYb(e[3],q));jXb(d,new hYb)}else{hXb(d,new bYb(e[k],o))}}b=new GXb(true);hXb(n,new cYb('Edit',b));c=C5(a.a);for(k=0;k<c.length;++k){hXb(b,new bYb(c[k],o))}f=new GXb(true);hXb(n,new eYb(f));g=F5(a.a);for(k=0;k<g.length;++k){hXb(f,new bYb(g[k],o))}i=new GXb(true);jXb(n,new hYb);hXb(n,new cYb('Help',i));j=G5(a.a);for(k=0;k<j.length;++k){hXb(i,new bYb(j[k],o))}M4b(n.cb,Lpc,swc);DXb(n,swc);return n}
var swc='cwMenuBar',kwc='cwMenuBarEditOptions',lwc='cwMenuBarFileOptions',owc='cwMenuBarFileRecents',pwc='cwMenuBarGWTOptions',qwc='cwMenuBarHelpOptions',rwc='cwMenuBarPrompts';C1(661,1,{},Blb);_.nc=function Clb(){UKb(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.a=0;_.c=null;C1(662,1,doc);_.lc=function Glb(){k4(this.b,xlb(this.a))};C1(1057,102,snc,FXb);C1(1064,103,{100:1,105:1,119:1},bYb,cYb,eYb);C1(1065,103,{100:1,106:1,119:1},hYb);var LR=ucc(Nuc,'CwMenuBar$1',661),yX=ucc(Luc,'MenuItemSeparator',1065);Soc(vn)(7);