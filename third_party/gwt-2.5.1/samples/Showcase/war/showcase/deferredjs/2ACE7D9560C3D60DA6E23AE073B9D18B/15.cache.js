function Tx(){}
function $x(){}
function ry(){}
function Gj(a,b){tj(b,a)}
function Sx(a,b){K1b(b.a,a)}
function Zx(a,b){L1b(b.a,a)}
function qy(a,b){M1b(b.a,a)}
function nDb(a){this.a=a}
function uDb(a){this.a=a}
function c2b(a){this.a=a}
function w2b(a){this.a=a}
function S1b(a){a.f=false;LWb(a.cb)}
function U1b(){V1b.call(this,new u2b)}
function M1b(a,b){S1b(a,(a.a,Yw(b),Zw(b)))}
function K1b(a,b){Q1b(a,(a.a,Yw(b)),Zw(b))}
function L1b(a,b){R1b(a,(a.a,Yw(b)),Zw(b))}
function Rx(){Rx=Rzc;Qx=new kx(MFc,new Tx)}
function Yx(){Yx=Rzc;Xx=new kx(fGc,new $x)}
function py(){py=Rzc;oy=new kx(iGc,new ry)}
function T1b(a){!a.g&&(a.g=uXb(new c2b(a)));ik(a)}
function N1b(a){if(a.g){moc(a.g.a);a.g=null}Yj(a,false)}
function Q1b(a,b,c){if(!FWb){a.f=true;MWb(a.cb);a.d=b;a.e=c}}
function D1b(a,b){var c,d;d=bYb(a.b,b);c=bYb(d,1);return tr(c)}
function e$b(a,b,c){var d;d=d$b(a,b);!!d&&(d[EGc]=c.a,undefined)}
function u2b(){j2b();r2b.call(this);this.cb[SCc]='Caption'}
function P1b(a,b){uhc(a.cb,rCc,b);Ji(a.a,b+'-caption');uhc(D1b(a.j,1),b,bJc)}
function O1b(a,b){var c;c=b.target;if(qr(c)){return Or(vr(D1b(a.j,0)),c)}return false}
function R1b(a,b,c){var d,e;if(a.f){d=b+Gr(a.cb);e=c+(Ir(a.cb)+$wnd.pageYOffset);if(d<a.b||d>=a.i||e<a.c){return}dk(a,d-a.d,e-a.e)}}
function Yw(a){var b,c;b=a.b;if(b){return c=a.a,(c.clientX||0)-Gr(b)+Kr(b)+(b.ownerDocument,$wnd.pageXOffset)}return a.a.clientX||0}
function Zw(a){var b,c;b=a.b;if(b){return c=a.a,(c.clientY||0)-(Ir(b)+$wnd.pageYOffset)+(b.scrollTop||0)+(b.ownerDocument,$wnd.pageYOffset)}return a.a.clientY||0}
function ck(a){a.w=true;if(!a.s){a.s=$doc.createElement(XCc);a.s.className='gwt-PopupPanelGlass';a.s.style[iEc]=(uu(),jEc);a.s.style[lEc]=0+(rv(),BDc);a.s.style[mEc]=nEc}}
function V1b(a){var b,c;$0b.call(this,false,true,NBc);rj(a);this.a=a;c=D1b(this.j,0);GWb(c,this.a.cb);Gj(this,this.a);vr(tr(this.cb))[SCc]='gwt-DialogBox';this.i=Rr($doc);this.b=0;this.c=0;b=new w2b(this);kj(this,b,(Rx(),Rx(),Qx));kj(this,b,(py(),py(),oy));kj(this,b,(Yx(),Yx(),Xx));kj(this,b,(jy(),jy(),iy));kj(this,b,(dy(),dy(),cy))}
function jDb(){var a,b,c,d,e,f,g,i,j,k,n;a=(g=new U1b,P1b(g,'cwDialogBox'),k2b(g.a,'Exemple de bo\xEEte de dialogue'),i=new Shc,i.e[GGc]=4,Lj(g.j,i),Zj(g),j=new t2b('Ceci est un exemple de composant de bo\xEEte de dialogue standard.'),Phc(i,j),e$b(i,j,(_5b(),V5b)),k=new PVb((hjb(),Yib)),Phc(i,k),e$b(i,k,V5b),n=new b$b(UIc,new uDb(g)),Phc(i,n),_E(),e$b(i,n,$5b),g);ck(a);a.v=true;e=new b$b('Afficher la bo\xEEte de dialogue',new nDb(a));d=new t2b('<br><br><br>Cette zone de liste montre que vous pouvez faire glisser une fen\xEAtre pop-up devant-elle. Ce probl\xE8me complexe se r\xE9p\xE8te pour de nombreuses autres biblioth\xE8ques.');c=new J7b;c.cb.size=1;for(b=10;b>0;--b){F7b(c,vJc+b,vJc+b,-1)}f=new Shc;f.e[GGc]=8;Phc(f,e);Phc(f,d);Phc(f,c);return f}
var vJc='\xE9l\xE9ment ';reb(291,279,{},Tx);_.xc=function Ux(a){Sx(this,gU(a,38))};_.Ac=function Vx(){return Qx};var Qx;reb(292,279,{},$x);_.xc=function _x(a){Zx(this,gU(a,39))};_.Ac=function ay(){return Xx};var Xx;reb(295,279,{},ry);_.xc=function sy(a){qy(this,gU(a,42))};_.Ac=function ty(){return oy};var oy;reb(764,1,IAc,nDb);_.Dc=function oDb(a){Vj(this.a);T1b(this.a)};_.a=null;reb(765,1,LAc);_.lc=function sDb(){_gb(this.a,jDb())};reb(766,1,IAc,uDb);_.Dc=function vDb(a){N1b(this.a)};_.a=null;reb(1032,1028,aAc,U1b);_.Lb=function W1b(){try{oj(this.j)}finally{oj(this.a)}};_.Mb=function X1b(){try{qj(this.j)}finally{qj(this.a)}};_._b=function Y1b(){N1b(this)};_.Qb=function Z1b(a){switch(RXb(a.type)){case 4:case 8:case 64:case 16:case 32:if(!this.f&&!O1b(this,a)){return}}pj(this,a)};_.Db=function $1b(a){P1b(this,a)};_.ac=function _1b(a){var b;b=a.d;!a.a&&RXb(a.d.type)==4&&O1b(this,b)&&(b.preventDefault(),undefined);a.c&&(a.d,false)&&(a.a=true)};_.bc=function a2b(){T1b(this)};_.a=null;_.b=0;_.c=0;_.d=0;_.e=0;_.f=false;_.g=null;_.i=0;reb(1033,1,TAc,c2b);_.Kc=function d2b(a){this.a.i=a.a};_.a=null;reb(1034,1035,$zc,u2b);reb(1038,1,{38:1,39:1,40:1,41:1,42:1,54:1},w2b);_.Gc=function x2b(a){};_.Hc=function y2b(a){};_.a=null;var z3=apc(yHc,'CwDialogBox$1',764),B3=apc(yHc,'CwDialogBox$3',766),l7=apc(uHc,'DialogBox',1032),j7=apc(uHc,'DialogBox$CaptionImpl',1034),k7=apc(uHc,'DialogBox$MouseHandler',1038),i7=apc(uHc,'DialogBox$1',1033),DY=apc(UHc,'MouseDownEvent',291),IY=apc(UHc,'MouseUpEvent',295),FY=apc(UHc,'MouseMoveEvent',292);yBc(wn)(15);