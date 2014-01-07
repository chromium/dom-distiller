function Nx(){}
function Ux(){}
function ly(){}
function Gj(a,b){tj(b,a)}
function Mx(a,b){m1b(b.b,a)}
function Tx(a,b){n1b(b.b,a)}
function ky(a,b){o1b(b.b,a)}
function _Cb(a){this.b=a}
function gDb(a){this.b=a}
function G1b(a){this.b=a}
function $1b(a){this.b=a}
function u1b(a){a.g=false;sWb(a.db)}
function w1b(){x1b.call(this,new Y1b)}
function o1b(a,b){u1b(a,(a.b,Sw(b),Tw(b)))}
function m1b(a,b){s1b(a,(a.b,Sw(b)),Tw(b))}
function n1b(a,b){t1b(a,(a.b,Sw(b)),Tw(b))}
function v1b(a){!a.i&&(a.i=cXb(new G1b(a)));ik(a)}
function Lx(){Lx=mzc;Kx=new ex(kFc,new Nx)}
function Sx(){Sx=mzc;Rx=new ex(FFc,new Ux)}
function jy(){jy=mzc;iy=new ex(IFc,new ly)}
function p1b(a){if(a.i){Jnc(a.i.b);a.i=null}Yj(a,false)}
function s1b(a,b,c){if(!mWb){a.g=true;tWb(a.db);a.e=b;a.f=c}}
function Y1b(){N1b();V1b.call(this);this.db[nCc]='Caption'}
function f1b(a,b){var c,d;d=KXb(a.c,b);c=KXb(d,1);return sr(c)}
function IZb(a,b,c){var d;d=HZb(a,b);!!d&&(d[eGc]=c.b,undefined)}
function r1b(a,b){Ygc(a.db,OBc,b);Ji(a.b,b+'-caption');Ygc(f1b(a.k,1),b,CIc)}
function q1b(a,b){var c;c=b.target;if(pr(c)){return Dr(ur(f1b(a.k,0)),c)}return false}
function t1b(a,b,c){var d,e;if(a.g){d=b+Fr(a.db);e=c+Gr(a.db);if(d<a.c||d>=a.j||e<a.d){return}dk(a,d-a.e,e-a.f)}}
function Tw(a){var b,c;b=a.c;if(b){return c=a.b,(c.clientY||0)-Gr(b)+(b.scrollTop||0)+Nr(b.ownerDocument)}return a.b.clientY||0}
function Sw(a){var b,c;b=a.c;if(b){return c=a.b,(c.clientX||0)-Fr(b)+(b.scrollLeft||0)+Mr(b.ownerDocument)}return a.b.clientX||0}
function ck(a){a.x=true;if(!a.t){a.t=$doc.createElement(sCc);a.t.className='gwt-PopupPanelGlass';a.t.style[IDc]=(ou(),JDc);a.t.style[LDc]=0+(lv(),XCc);a.t.style[MDc]=NDc}}
function x1b(a){var b,c;C0b.call(this,false,true,iBc);rj(a);this.b=a;c=f1b(this.k,0);nWb(c,this.b.db);Gj(this,this.b);ur(sr(this.db))[nCc]='gwt-DialogBox';this.j=Jr($doc);this.c=0;this.d=0;b=new $1b(this);kj(this,b,(Lx(),Lx(),Kx));kj(this,b,(jy(),jy(),iy));kj(this,b,(Sx(),Sx(),Rx));kj(this,b,(dy(),dy(),cy));kj(this,b,(Zx(),Zx(),Yx))}
function XCb(){var a,b,c,d,e,f,g,i,j,k,n;a=(g=new w1b,r1b(g,'cwDialogBox'),O1b(g.b,'Exemple de bo\xEEte de dialogue'),i=new uhc,i.f[gGc]=4,Lj(g.k,i),Zj(g),j=new X1b('Ceci est un exemple de composant de bo\xEEte de dialogue standard.'),rhc(i,j),IZb(i,j,(H5b(),B5b)),k=new wVb((Vib(),Kib)),rhc(i,k),IZb(i,k,B5b),n=new FZb(tIc,new gDb(g)),rhc(i,n),UE(),IZb(i,n,G5b),g);ck(a);a.w=true;e=new FZb('Afficher la bo\xEEte de dialogue',new _Cb(a));d=new X1b('<br><br><br>Cette zone de liste montre que vous pouvez faire glisser une fen\xEAtre pop-up devant-elle. Ce probl\xE8me complexe se r\xE9p\xE8te pour de nombreuses autres biblioth\xE8ques.');c=new p7b;c.db.size=1;for(b=10;b>0;--b){l7b(c,WIc+b,WIc+b,-1)}f=new uhc;f.f[gGc]=8;rhc(f,e);rhc(f,d);rhc(f,c);return f}
var WIc='\xE9l\xE9ment ';ieb(289,277,{},Nx);_.xc=function Ox(a){Mx(this,_T(a,38))};_.Ac=function Px(){return Kx};var Kx;ieb(290,277,{},Ux);_.xc=function Vx(a){Tx(this,_T(a,39))};_.Ac=function Wx(){return Rx};var Rx;ieb(293,277,{},ly);_.xc=function my(a){ky(this,_T(a,42))};_.Ac=function ny(){return iy};var iy;ieb(760,1,dAc,_Cb);_.Dc=function aDb(a){Vj(this.b);v1b(this.b)};_.b=null;ieb(761,1,gAc);_.mc=function eDb(){Ngb(this.b,XCb())};ieb(762,1,dAc,gDb);_.Dc=function hDb(a){p1b(this.b)};_.b=null;ieb(1023,1019,xzc,w1b);_.Mb=function y1b(){try{oj(this.k)}finally{oj(this.b)}};_.Nb=function z1b(){try{qj(this.k)}finally{qj(this.b)}};_.ac=function A1b(){p1b(this)};_.Rb=function B1b(a){switch(yXb(a.type)){case 4:case 8:case 64:case 16:case 32:if(!this.g&&!q1b(this,a)){return}}pj(this,a)};_.Eb=function C1b(a){r1b(this,a)};_.bc=function D1b(a){var b;b=a.e;!a.b&&yXb(a.e.type)==4&&q1b(this,b)&&(b.preventDefault(),undefined);a.d&&(a.e,false)&&(a.b=true)};_.cc=function E1b(){v1b(this)};_.b=null;_.c=0;_.d=0;_.e=0;_.f=0;_.g=false;_.i=null;_.j=0;ieb(1024,1,oAc,G1b);_.Kc=function H1b(a){this.b.j=a.b};_.b=null;ieb(1025,1026,vzc,Y1b);ieb(1029,1,{38:1,39:1,40:1,41:1,42:1,54:1},$1b);_.Gc=function _1b(a){};_.Hc=function a2b(a){};_.b=null;var s3=xoc(ZGc,'CwDialogBox$1',760),u3=xoc(ZGc,'CwDialogBox$3',762),b7=xoc(VGc,'DialogBox',1023),_6=xoc(VGc,'DialogBox$CaptionImpl',1025),a7=xoc(VGc,'DialogBox$MouseHandler',1029),$6=xoc(VGc,'DialogBox$1',1024),yY=xoc(tHc,'MouseDownEvent',289),DY=xoc(tHc,'MouseUpEvent',293),AY=xoc(tHc,'MouseMoveEvent',290);VAc(wn)(15);