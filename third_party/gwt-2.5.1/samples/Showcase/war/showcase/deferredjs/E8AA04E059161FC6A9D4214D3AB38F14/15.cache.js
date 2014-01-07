function Nx(){}
function Ux(){}
function ly(){}
function Gj(a,b){tj(b,a)}
function Mx(a,b){mQb(b.b,a)}
function Tx(a,b){nQb(b.b,a)}
function ky(a,b){oQb(b.b,a)}
function _pb(a){this.b=a}
function gqb(a){this.b=a}
function GQb(a){this.b=a}
function $Qb(a){this.b=a}
function uQb(a){a.g=false;sJb(a.db)}
function wQb(){xQb.call(this,new YQb)}
function Lx(){Lx=mmc;Kx=new ex(gsc,new Nx)}
function Sx(){Sx=mmc;Rx=new ex(Bsc,new Ux)}
function jy(){jy=mmc;iy=new ex(Esc,new ly)}
function mQb(a,b){sQb(a,(a.b,Sw(b)),Tw(b))}
function nQb(a,b){tQb(a,(a.b,Sw(b)),Tw(b))}
function oQb(a,b){uQb(a,(a.b,Sw(b),Tw(b)))}
function vQb(a){!a.i&&(a.i=cKb(new GQb(a)));ik(a)}
function pQb(a){if(a.i){Jac(a.i.b);a.i=null}Yj(a,false)}
function YQb(){NQb();VQb.call(this);this.db[npc]='Caption'}
function sQb(a,b,c){if(!mJb){a.g=true;tJb(a.db);a.e=b;a.f=c}}
function fQb(a,b){var c,d;d=KKb(a.c,b);c=KKb(d,1);return sr(c)}
function IMb(a,b,c){var d;d=HMb(a,b);!!d&&(d[atc]=c.b,undefined)}
function rQb(a,b){Y3b(a.db,Ooc,b);Ji(a.b,b+'-caption');Y3b(fQb(a.k,1),b,Cvc)}
function qQb(a,b){var c;c=b.target;if(pr(c)){return Dr(ur(fQb(a.k,0)),c)}return false}
function tQb(a,b,c){var d,e;if(a.g){d=b+Fr(a.db);e=c+Gr(a.db);if(d<a.c||d>=a.j||e<a.d){return}dk(a,d-a.e,e-a.f)}}
function Tw(a){var b,c;b=a.c;if(b){return c=a.b,(c.clientY||0)-Gr(b)+(b.scrollTop||0)+Nr(b.ownerDocument)}return a.b.clientY||0}
function Sw(a){var b,c;b=a.c;if(b){return c=a.b,(c.clientX||0)-Fr(b)+(b.scrollLeft||0)+Mr(b.ownerDocument)}return a.b.clientX||0}
function ck(a){a.x=true;if(!a.t){a.t=$doc.createElement(spc);a.t.className='gwt-PopupPanelGlass';a.t.style[Hqc]=(ou(),Iqc);a.t.style[Kqc]=0+(lv(),Xpc);a.t.style[Lqc]=Mqc}}
function xQb(a){var b,c;CPb.call(this,false,true,ioc);rj(a);this.b=a;c=fQb(this.k,0);nJb(c,this.b.db);Gj(this,this.b);ur(sr(this.db))[npc]='gwt-DialogBox';this.j=Jr($doc);this.c=0;this.d=0;b=new $Qb(this);kj(this,b,(Lx(),Lx(),Kx));kj(this,b,(jy(),jy(),iy));kj(this,b,(Sx(),Sx(),Rx));kj(this,b,(dy(),dy(),cy));kj(this,b,(Zx(),Zx(),Yx))}
function Xpb(){var a,b,c,d,e,f,g,i,j,k,n;a=(g=new wQb,rQb(g,'cwDialogBox'),OQb(g.b,'Sample DialogBox'),i=new u4b,i.f[ctc]=4,Lj(g.k,i),Zj(g),j=new XQb('This is an example of a standard dialog box component.'),r4b(i,j),IMb(i,j,(HUb(),BUb)),k=new wIb((V5(),K5)),r4b(i,k),IMb(i,k,BUb),n=new FMb(svc,new gqb(g)),r4b(i,n),GD(),IMb(i,n,GUb),g);ck(a);a.w=true;e=new FMb('Show Dialog Box',new _pb(a));d=new XQb('<br><br><br>This list box demonstrates that you can drag the popup over it. This obscure corner case renders incorrectly for many other libraries.');c=new pWb;c.db.size=1;for(b=10;b>0;--b){lWb(c,Qvc+b,Qvc+b,-1)}f=new u4b;f.f[ctc]=8;r4b(f,e);r4b(f,d);r4b(f,c);return f}
var Qvc='item ';i1(289,277,{},Nx);_.xc=function Ox(a){Mx(this,yH(a,38))};_.Ac=function Px(){return Kx};var Kx;i1(290,277,{},Ux);_.xc=function Vx(a){Tx(this,yH(a,39))};_.Ac=function Wx(){return Rx};var Rx;i1(293,277,{},ly);_.xc=function my(a){ky(this,yH(a,42))};_.Ac=function ny(){return iy};var iy;i1(735,1,dnc,_pb);_.Dc=function aqb(a){Vj(this.b);vQb(this.b)};_.b=null;i1(736,1,gnc);_.mc=function eqb(){N3(this.b,Xpb())};i1(737,1,dnc,gqb);_.Dc=function hqb(a){pQb(this.b)};_.b=null;i1(998,994,xmc,wQb);_.Mb=function yQb(){try{oj(this.k)}finally{oj(this.b)}};_.Nb=function zQb(){try{qj(this.k)}finally{qj(this.b)}};_.ac=function AQb(){pQb(this)};_.Rb=function BQb(a){switch(yKb(a.type)){case 4:case 8:case 64:case 16:case 32:if(!this.g&&!qQb(this,a)){return}}pj(this,a)};_.Eb=function CQb(a){rQb(this,a)};_.bc=function DQb(a){var b;b=a.e;!a.b&&yKb(a.e.type)==4&&qQb(this,b)&&(b.preventDefault(),undefined);a.d&&(a.e,false)&&(a.b=true)};_.cc=function EQb(){vQb(this)};_.b=null;_.c=0;_.d=0;_.e=0;_.f=0;_.g=false;_.i=null;_.j=0;i1(999,1,onc,GQb);_.Kc=function HQb(a){this.b.j=a.b};_.b=null;i1(1000,1001,vmc,YQb);i1(1004,1,{38:1,39:1,40:1,41:1,42:1,54:1},$Qb);_.Gc=function _Qb(a){};_.Hc=function aRb(a){};_.b=null;var sS=xbc(Vtc,'CwDialogBox$1',735),uS=xbc(Vtc,'CwDialogBox$3',737),bW=xbc(Rtc,'DialogBox',998),_V=xbc(Rtc,'DialogBox$CaptionImpl',1000),aW=xbc(Rtc,'DialogBox$MouseHandler',1004),$V=xbc(Rtc,'DialogBox$1',999),XL=xbc(puc,'MouseDownEvent',289),aM=xbc(puc,'MouseUpEvent',293),ZL=xbc(puc,'MouseMoveEvent',290);Vnc(wn)(15);