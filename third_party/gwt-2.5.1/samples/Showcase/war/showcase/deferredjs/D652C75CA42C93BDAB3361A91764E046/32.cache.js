function BE(){BE=mzc;AE=new Rwc}
function K4b(a,b,c,d){var e;a.b.Yg(b,c);e=M4b(a.b.j,b,c);dj(e,d,true)}
function CE(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ne(c)}}
function EE(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Rd(a);return String(c)}
function GE(){BE();var a;a=_T(AE.ie(rLc),61);if(!a){a=new FE;AE.ke(rLc,a)}return a}
function DE(c,b){try{typeof $wnd[b]!='object'&&IE(b);c.b=$wnd[b]}catch(a){IE(b)}}
function IE(a){throw new Sxc(ZDc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function FE(){this.c='Dictionary userInfo';DE(this,rLc);if(!this.b){throw new Sxc("Cannot find JavaScript object with the name 'userInfo'")}}
function Gwb(){var a,b,c,d,e,f,g,i,j,k,n;f=new uhc;g=new X1b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=rDc;g.db.style['textAlign']=LDc;rhc(f,new X1b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));rhc(f,g);j=new C4b;b=j.k;i=GE();e=(n=new Zwc,CE(i,n),n);a=0;for(d=Vtc(DH(e.b));d.b.te();){c=_T(_tc(d),1);k=EE(i,c);t4b(j,0,a,c);K4b(b,0,a,'cw-DictionaryExample-header');t4b(j,1,a,k);K4b(b,1,a,'cw-DictionaryExample-data');++a}rhc(f,new X1b('<br><br>'));rhc(f,j);return f}
var rLc='userInfo';ieb(349,1,{61:1},FE);_.Rd=function HE(a){var b;b="Cannot find '"+a+"' in "+this;throw new Sxc(b)};_.tS=function JE(){return this.c};_.b=null;_.c=null;var AE;ieb(663,1,gAc);_.mc=function Mwb(){Ngb(this.b,Gwb())};var uZ=xoc(kHc,'Dictionary',349);VAc(wn)(32);