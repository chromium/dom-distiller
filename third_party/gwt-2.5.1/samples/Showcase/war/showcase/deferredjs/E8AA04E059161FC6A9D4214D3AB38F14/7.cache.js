function VWb(){WWb.call(this,false)}
function rXb(a,b){tXb.call(this,a,false);this.c=b}
function sXb(a,b){tXb.call(this,a,false);qXb(this,b)}
function uXb(a){tXb.call(this,'GWT',true);qXb(this,a)}
function clb(a){this.d=a;this.c=i5(this.d.b)}
function zWb(a,b){return GWb(a,b,a.b.c)}
function Pb(a,b){$b((ie(),de),a,oH(b0,smc,134,[(cbc(),b?bbc:abc)]))}
function qXb(a,b){a.e=b;!!a.d&&UWb(a.d,a);if(b){b.db.tabIndex=-1;yf();Pb(a.db,true)}else{yf();Pb(a.db,false)}}
function GWb(a,b,c){if(c<0||c>a.b.c){throw new Vac}a.p&&(b.db[Vuc]=2,undefined);yWb(a,c,b.db);shc(a.b,c,b);return b}
function e5(a){var b,c;b=yH(a.b.ld(rvc),148);if(b==null){c=oH(g0,tmc,1,['New','Open',svc,tvc,'Exit']);a.b.nd(rvc,c);return c}else{return b}}
function d5(a){var b,c;b=yH(a.b.ld(qvc),148);if(b==null){c=oH(g0,tmc,1,['Undo','Redo','Cut','Copy','Paste']);a.b.nd(qvc,c);return c}else{return b}}
function h5(a){var b,c;b=yH(a.b.ld(wvc),148);if(b==null){c=oH(g0,tmc,1,['Contents','Fortune Cookie','About GWT']);a.b.nd(wvc,c);return c}else{return b}}
function g5(a){var b,c;b=yH(a.b.ld(vvc),148);if(b==null){c=oH(g0,tmc,1,['Download','Examples',Crc,"GWT wit' the program"]);a.b.nd(vvc,c);return c}else{return b}}
function f5(a){var b,c;b=yH(a.b.ld(uvc),148);if(b==null){c=oH(g0,tmc,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.nd(uvc,c);return c}else{return b}}
function xXb(){var a;Ni(this,$doc.createElement(_sc));this.db[npc]='gwt-MenuItemSeparator';a=$doc.createElement(spc);nJb(this.db,a);a[npc]='menuSeparatorInner'}
function i5(a){var b,c;b=yH(a.b.ld(xvc),148);if(b==null){c=oH(g0,tmc,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.nd(xvc,c);return c}else{return b}}
function $kb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new clb(a);n=new VWb;n.c=true;n.db.style[opc]='500px';n.f=true;q=new WWb(true);p=f5(a.b);for(k=0;k<p.length;++k){xWb(q,new rXb(p[k],o))}d=new WWb(true);d.f=true;xWb(n,new sXb('File',d));e=e5(a.b);for(k=0;k<e.length;++k){if(k==3){zWb(d,new xXb);xWb(d,new sXb(e[3],q));zWb(d,new xXb)}else{xWb(d,new rXb(e[k],o))}}b=new WWb(true);xWb(n,new sXb('Edit',b));c=d5(a.b);for(k=0;k<c.length;++k){xWb(b,new rXb(c[k],o))}f=new WWb(true);xWb(n,new uXb(f));g=g5(a.b);for(k=0;k<g.length;++k){xWb(f,new rXb(g[k],o))}i=new WWb(true);zWb(n,new xXb);xWb(n,new sXb('Help',i));j=h5(a.b);for(k=0;k<j.length;++k){xWb(i,new rXb(j[k],o))}Y3b(n.db,Ooc,yvc);TWb(n,yvc);return n}
var yvc='cwMenuBar',qvc='cwMenuBarEditOptions',rvc='cwMenuBarFileOptions',uvc='cwMenuBarFileRecents',vvc='cwMenuBarGWTOptions',wvc='cwMenuBarHelpOptions',xvc='cwMenuBarPrompts';i1(658,1,{},clb);_.oc=function dlb(){dKb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;i1(659,1,gnc);_.mc=function hlb(){N3(this.c,$kb(this.b))};i1(1053,102,vmc,VWb);i1(1060,103,{99:1,104:1,118:1},rXb,sXb,uXb);i1(1061,103,{99:1,105:1,118:1},xXb);var tR=xbc(Ttc,'CwMenuBar$1',658),fX=xbc(Rtc,'MenuItemSeparator',1061);Vnc(wn)(7);