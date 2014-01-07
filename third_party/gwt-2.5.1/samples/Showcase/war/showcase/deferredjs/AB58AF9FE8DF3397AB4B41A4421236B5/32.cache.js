function YD(){YD=mnc;XD=new Rkc}
function zUb(a,b,c,d){var e;a.b.$f(b,c);e=BUb(a.b.j,b,c);oj(e,d,true)}
function ZD(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.pd(c)}}
function _D(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.fd(a);return String(c)}
function bE(){YD();var a;a=dI(XD.kd(jzc),61);if(!a){a=new aE;XD.md(jzc,a)}return a}
function $D(c,b){try{typeof $wnd[b]!='object'&&dE(b);c.b=$wnd[b]}catch(a){dE(b)}}
function dE(a){throw new Slc(Zrc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function aE(){this.c='Dictionary userInfo';$D(this,jzc);if(!this.b){throw new Slc("Cannot find JavaScript object with the name 'userInfo'")}}
function tkb(){var a,b,c,d,e,f,g,i,j,k,n;f=new j5b;g=new QRb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=rrc;g.db.style['textAlign']=Lrc;g5b(f,new QRb('<b>\u8FD9\u4E2A\u4F8B\u5B50\u4F7F\u7528\u4E0B\u5217Javascript\u7684\u53D8\u91CF\uFF1A <\/b>'));g5b(f,g);j=new rUb;b=j.k;i=bE();e=(n=new Zkc,ZD(i,n),n);a=0;for(d=Vhc(cF(e.b));d.b.vd();){c=dI(_hc(d),1);k=_D(i,c);iUb(j,0,a,c);zUb(b,0,a,'cw-DictionaryExample-header');iUb(j,1,a,k);zUb(b,1,a,'cw-DictionaryExample-data');++a}g5b(f,new QRb('<br><br>'));g5b(f,j);return f}
var jzc='userInfo';X1(345,1,{61:1},aE);_.fd=function cE(a){var b;b="Cannot find '"+a+"' in "+this;throw new Slc(b)};_.tS=function eE(){return this.c};_.b=null;_.c=null;var XD;X1(643,1,hoc);_.qc=function zkb(){A4(this.b,tkb())};var wN=xcc(mvc,'Dictionary',345);Woc(In)(32);