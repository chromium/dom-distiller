function Tx(){}
function $x(){}
function ry(){}
function Gj(a,b){tj(b,a)}
function Sx(a,b){Wvc(b.a,a)}
function Zx(a,b){Xvc(b.a,a)}
function qy(a,b){Yvc(b.a,a)}
function z5b(a){this.a=a}
function G5b(a){this.a=a}
function owc(a){this.a=a}
function Iwc(a){this.a=a}
function cwc(a){a.f=false;Xoc(a.cb)}
function ewc(){fwc.call(this,new Gwc)}
function Rx(){Rx=b2c;Qx=new kx(s8c,new Tx)}
function Yx(){Yx=b2c;Xx=new kx(N8c,new $x)}
function py(){py=b2c;oy=new kx(Q8c,new ry)}
function Xvc(a,b){bwc(a,(a.a,Yw(b)),Zw(b))}
function Wvc(a,b){awc(a,(a.a,Yw(b)),Zw(b))}
function Yvc(a,b){cwc(a,(a.a,Yw(b),Zw(b)))}
function dwc(a){!a.g&&(a.g=Gpc(new owc(a)));ik(a)}
function Zvc(a){if(a.g){ySc(a.g.a);a.g=null}Yj(a,false)}
function awc(a,b,c){if(!Roc){a.f=true;Yoc(a.cb);a.d=b;a.e=c}}
function Gwc(){vwc();Dwc.call(this);this.cb[c5c]='Caption'}
function qsc(a,b,c){var d;d=psc(a,b);!!d&&(d[k9c]=c.a,undefined)}
function Pvc(a,b){var c,d;d=nqc(a.b,b);c=nqc(d,1);return tr(c)}
function _vc(a,b){GLc(a.cb,D4c,b);Ji(a.a,b+'-caption');GLc(Pvc(a.j,1),b,Nbd)}
function $vc(a,b){var c;c=b.target;if(qr(c)){return Or(vr(Pvc(a.j,0)),c)}return false}
function bwc(a,b,c){var d,e;if(a.f){d=b+Gr(a.cb);e=c+(Ir(a.cb)+$wnd.pageYOffset);if(d<a.b||d>=a.i||e<a.c){return}dk(a,d-a.d,e-a.e)}}
function Yw(a){var b,c;b=a.b;if(b){return c=a.a,(c.clientX||0)-Gr(b)+Kr(b)+(b.ownerDocument,$wnd.pageXOffset)}return a.a.clientX||0}
function Zw(a){var b,c;b=a.b;if(b){return c=a.a,(c.clientY||0)-(Ir(b)+$wnd.pageYOffset)+(b.scrollTop||0)+(b.ownerDocument,$wnd.pageYOffset)}return a.a.clientY||0}
function ck(a){a.w=true;if(!a.s){a.s=$doc.createElement(h5c);a.s.className='gwt-PopupPanelGlass';a.s.style[u6c]=(uu(),v6c);a.s.style[x6c]=0+(rv(),N5c);a.s.style[y6c]=z6c}}
function fwc(a){var b,c;kvc.call(this,false,true,Z3c);rj(a);this.a=a;c=Pvc(this.j,0);Soc(c,this.a.cb);Gj(this,this.a);vr(tr(this.cb))[c5c]='gwt-DialogBox';this.i=Rr($doc);this.b=0;this.c=0;b=new Iwc(this);kj(this,b,(Rx(),Rx(),Qx));kj(this,b,(py(),py(),oy));kj(this,b,(Yx(),Yx(),Xx));kj(this,b,(jy(),jy(),iy));kj(this,b,(dy(),dy(),cy))}
function v5b(){var a,b,c,d,e,f,g,i,j,k,n;a=(g=new ewc,_vc(g,'cwDialogBox'),wwc(g.a,'Sample DialogBox'),i=new cMc,i.e[m9c]=4,Lj(g.j,i),Zj(g),j=new Fwc('This is an example of a standard dialog box component.'),_Lc(i,j),qsc(i,j,(lAc(),fAc)),k=new _nc((tNb(),iNb)),_Lc(i,k),qsc(i,k,fAc),n=new nsc(Dbd,new G5b(g)),_Lc(i,n),gG(),qsc(i,n,kAc),g);ck(a);a.v=true;e=new nsc('Show Dialog Box',new z5b(a));d=new Fwc('<br><br><br>This list box demonstrates that you can drag the popup over it. This obscure corner case renders incorrectly for many other libraries.');c=new VBc;c.cb.size=1;for(b=10;b>0;--b){RBc(c,_bd+b,_bd+b,-1)}f=new cMc;f.e[m9c]=8;_Lc(f,e);_Lc(f,d);_Lc(f,c);return f}
var _bd='item ';DIb(291,279,{},Tx);_.xc=function Ux(a){Sx(this,klb(a,38))};_.Ac=function Vx(){return Qx};var Qx;DIb(292,279,{},$x);_.xc=function _x(a){Zx(this,klb(a,39))};_.Ac=function ay(){return Xx};var Xx;DIb(295,279,{},ry);_.xc=function sy(a){qy(this,klb(a,42))};_.Ac=function ty(){return oy};var oy;DIb(826,1,U2c,z5b);_.Dc=function A5b(a){Vj(this.a);dwc(this.a)};_.a=null;DIb(827,1,X2c);_.lc=function E5b(){lLb(this.a,v5b())};DIb(828,1,U2c,G5b);_.Dc=function H5b(a){Zvc(this.a)};_.a=null;DIb(1094,1090,m2c,ewc);_.Lb=function gwc(){try{oj(this.j)}finally{oj(this.a)}};_.Mb=function hwc(){try{qj(this.j)}finally{qj(this.a)}};_._b=function iwc(){Zvc(this)};_.Qb=function jwc(a){switch(bqc(a.type)){case 4:case 8:case 64:case 16:case 32:if(!this.f&&!$vc(this,a)){return}}pj(this,a)};_.Db=function kwc(a){_vc(this,a)};_.ac=function lwc(a){var b;b=a.d;!a.a&&bqc(a.d.type)==4&&$vc(this,b)&&(b.preventDefault(),undefined);a.c&&(a.d,false)&&(a.a=true)};_.bc=function mwc(){dwc(this)};_.a=null;_.b=0;_.c=0;_.d=0;_.e=0;_.f=false;_.g=null;_.i=0;DIb(1095,1,d3c,owc);_.Kc=function pwc(a){this.a.i=a.a};_.a=null;DIb(1096,1097,k2c,Gwc);DIb(1100,1,{38:1,39:1,40:1,41:1,42:1,54:1},Iwc);_.Gc=function Jwc(a){};_.Hc=function Kwc(a){};_.a=null;var Lxb=mTc(ead,'CwDialogBox$1',826),Nxb=mTc(ead,'CwDialogBox$3',828),xBb=mTc(aad,'DialogBox',1094),vBb=mTc(aad,'DialogBox$CaptionImpl',1096),wBb=mTc(aad,'DialogBox$MouseHandler',1100),uBb=mTc(aad,'DialogBox$1',1095),Hpb=mTc(Aad,'MouseDownEvent',291),Mpb=mTc(Aad,'MouseUpEvent',295),Jpb=mTc(Aad,'MouseMoveEvent',292);K3c(wn)(15);