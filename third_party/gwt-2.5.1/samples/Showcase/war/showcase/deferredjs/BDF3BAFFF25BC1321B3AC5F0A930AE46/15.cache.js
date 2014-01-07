function ly(){}
function sy(){}
function Ly(){}
function Gj(a,b){tj(b,a)}
function ky(a,b){aTb(b.a,a)}
function ry(a,b){bTb(b.a,a)}
function Ky(a,b){cTb(b.a,a)}
function Hrb(a){this.a=a}
function Orb(a){this.a=a}
function OTb(a){this.a=a}
function uTb(a){this.a=a}
function iTb(a){a.f=false;WLb(a.cb)}
function kTb(){lTb.call(this,new MTb)}
function aTb(a,b){gTb(a,(a.a,qx(b)),rx(b))}
function bTb(a,b){hTb(a,(a.a,qx(b)),rx(b))}
function cTb(a,b){iTb(a,(a.a,qx(b),rx(b)))}
function jTb(a){!a.g&&(a.g=GMb(new uTb(a)));ik(a)}
function jy(){jy=Hpc;iy=new Ex(eEc,new ly)}
function qy(){qy=Hpc;py=new Ex(qFc,new sy)}
function Jy(){Jy=Hpc;Iy=new Ex(XDc,new Ly)}
function MTb(){BTb();JTb.call(this);this.cb[qtc]=A$c}
function dTb(a){if(a.g){cec(a.g.a);a.g=null}Yj(a,false)}
function gTb(a,b,c){if(!QLb){a.f=true;XLb(a.cb);a.d=b;a.e=c}}
function wPb(a,b,c){var d;d=vPb(a,b);!!d&&(d[GGc]=c.a,undefined)}
function VSb(a,b){var c,d;d=kNb(a.b,b);c=d.children[1];return tr(c)}
function fTb(a,b){Y6b(a.cb,jsc,b);Ki(a.a,b+y$c);Y6b(VSb(a.j,1),b,TYc)}
function eTb(a,b){var c;c=b.srcElement;if(qr(c)){return Nr(vr(VSb(a.j,0)),c)}return false}
function qx(a){var b,c;b=a.b;if(b){return c=a.a,(c.clientX||0)-Qr(b)+Sr(b)+cs(b.ownerDocument)}return a.a.clientX||0}
function rx(a){var b,c;b=a.b;if(b){return c=a.a,(c.clientY||0)-Rr(b)+(b.scrollTop||0)+ds(b.ownerDocument)}return a.a.clientY||0}
function hTb(a,b,c){var d,e;if(a.f){d=b+Qr(a.cb);e=c+Rr(a.cb);if(d<a.b||d>=a.i||e<a.c){return}dk(a,d-a.d,e-a.e)}}
function ck(a){a.w=true;if(!a.s){a.s=Ar($doc,Ctc);a.s.className=r$c;a.s.style[Uxc]=(Ou(),Yxc);a.s.style[Vxc]=0+(Lv(),Quc);a.s.style[Wxc]=fyc}}
function lTb(a){var b,c;qSb.call(this,false,true,Frc);rj(a);this.a=a;c=VSb(this.j,0);RLb(c,this.a.cb);Gj(this,this.a);vr(tr(this.cb))[qtc]=z$c;this.i=_r($doc);this.b=Yr($doc);this.c=Zr($doc);b=new OTb(this);kj(this,b,(jy(),jy(),iy));kj(this,b,(Jy(),Jy(),Iy));kj(this,b,(qy(),qy(),py));kj(this,b,(Dy(),Dy(),Cy));kj(this,b,(xy(),xy(),wy))}
function Drb(){var a,b,c,d,e,f,g,i,j,k,n;a=(g=new kTb,fTb(g,s$c),CTb(g.a,t$c),i=new u7b,i.e[IGc]=4,Lj(g.j,i),Zj(g),j=new LTb(u$c),r7b(i,j),wPb(i,j,(uXb(),oXb)),k=new XKb((W5(),o7(),f7(),b7)),r7b(i,k),wPb(i,k,oXb),n=new tPb(aYc,new Orb(g)),r7b(i,n),fE(),wPb(i,n,tXb),g);ck(a);a.v=true;e=new tPb(v$c,new Hrb(a));d=new LTb(w$c);c=new cZb;c.cb.size=1;for(b=10;b>0;--b){$Yb(c,x$c+b,x$c+b,-1)}f=new u7b;f.e[IGc]=8;r7b(f,e);r7b(f,d);r7b(f,c);return f}
var y$c='-caption',w$c='<br><br><br>This list box demonstrates that you can drag the popup over it. This obscure corner case renders incorrectly for many other libraries.',A$c='Caption',B$c='CwDialogBox$1',C$c='CwDialogBox$3',D$c='DialogBox',G$c='DialogBox$1',E$c='DialogBox$CaptionImpl',F$c='DialogBox$MouseHandler',H$c='MouseDownEvent',J$c='MouseMoveEvent',I$c='MouseUpEvent',t$c='Sample DialogBox',v$c='Show Dialog Box',u$c='This is an example of a standard dialog box component.',s$c='cwDialogBox',z$c='gwt-DialogBox',r$c='gwt-PopupPanelGlass',x$c='item ';U1(292,280,{},ly);_.yc=function my(a){ky(this,ZH(a,38))};_.Bc=function ny(){return iy};var iy;U1(293,280,{},sy);_.yc=function ty(a){ry(this,ZH(a,39))};_.Bc=function uy(){return py};var py;U1(296,280,{},Ly);_.yc=function My(a){Ky(this,ZH(a,42))};_.Bc=function Ny(){return Iy};var Iy;U1(743,1,zqc,Hrb);_.Ec=function Irb(a){Vj(this.a);jTb(this.a)};_.a=null;U1(744,1,Cqc);_.mc=function Mrb(){_4(this.a,Drb())};U1(745,1,zqc,Orb);_.Ec=function Prb(a){dTb(this.a)};_.a=null;U1(1010,1006,Spc,kTb);_.Mb=function mTb(){try{oj(this.j)}finally{oj(this.a)}};_.Nb=function nTb(){try{qj(this.j)}finally{qj(this.a)}};_.ac=function oTb(){dTb(this)};_.Rb=function pTb(a){switch(cNb(a.type)){case 4:case 8:case 64:case 16:case 32:if(!this.f&&!eTb(this,a)){return}}pj(this,a)};_.Eb=function qTb(a){fTb(this,a)};_.bc=function rTb(a){var b;b=a.d;!a.a&&cNb(a.d.type)==4&&eTb(this,b)&&Fr(b);a.c&&(a.d,false)&&(a.a=true)};_.cc=function sTb(){jTb(this)};_.a=null;_.b=0;_.c=0;_.d=0;_.e=0;_.f=false;_.g=null;_.i=0;U1(1011,1,rqc,uTb);_.Lc=function vTb(a){this.a.i=a.a};_.a=null;U1(1012,1013,Qpc,MTb);U1(1016,1,{38:1,39:1,40:1,41:1,42:1,54:1},OTb);_.Hc=function PTb(a){};_.Ic=function QTb(a){};_.a=null;var WS=Sec(wKc,B$c,743),YS=Sec(wKc,C$c,745),JW=Sec(HJc,D$c,1010),HW=Sec(HJc,E$c,1012),IW=Sec(HJc,F$c,1016),GW=Sec(HJc,G$c,1011),vM=Sec(cPc,H$c,292),AM=Sec(cPc,I$c,296),xM=Sec(cPc,J$c,293);orc(wn)(15);