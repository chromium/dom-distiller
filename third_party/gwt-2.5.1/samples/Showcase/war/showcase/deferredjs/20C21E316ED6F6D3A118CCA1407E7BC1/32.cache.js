function jE(){jE=Knc;iE=new nlc}
function kE(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.td(c)}}
function PUb(a,b,c,d){var e;a.b.cg(b,c);e=RUb(a.b.j,b,c);pj(e,d,true)}
function oE(){jE();var a;a=qI(iE.od(Hzc),61);if(!a){a=new nE;iE.qd(Hzc,a)}return a}
function lE(c,b){try{typeof $wnd[b]!='object'&&qE(b);c.b=$wnd[b]}catch(a){qE(b)}}
function qE(a){throw new omc(Csc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function mE(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.kd(a);return String(c)}
function nE(){this.c='Dictionary userInfo';lE(this,Hzc);if(!this.b){throw new omc("Cannot find JavaScript object with the name 'userInfo'")}}
function Mkb(){var a,b,c,d,e,f,g,i,j,k,n;f=new C5b;g=new eSb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=$rc;g.db.style['textAlign']=osc;z5b(f,new eSb('<b>\u8FD9\u4E2A\u4F8B\u5B50\u4F7F\u7528\u4E0B\u5217Javascript\u7684\u53D8\u91CF\uFF1A <\/b>'));z5b(f,g);j=new HUb;b=j.k;i=oE();e=(n=new vlc,kE(i,n),n);a=0;for(d=ric(pF(e.b));d.b.zd();){c=qI(xic(d),1);k=mE(i,c);yUb(j,0,a,c);PUb(b,0,a,'cw-DictionaryExample-header');yUb(j,1,a,k);PUb(b,1,a,'cw-DictionaryExample-data');++a}z5b(f,new eSb('<br><br>'));z5b(f,j);return f}
var Hzc='userInfo';o2(349,1,{61:1},nE);_.kd=function pE(a){var b;b="Cannot find '"+a+"' in "+this;throw new omc(b)};_.tS=function rE(){return this.c};_.b=null;_.c=null;var iE;o2(647,1,Foc);_.qc=function Skb(){T4(this.b,Mkb())};var NN=Ucc(Kvc,'Dictionary',349);spc(Jn)(32);