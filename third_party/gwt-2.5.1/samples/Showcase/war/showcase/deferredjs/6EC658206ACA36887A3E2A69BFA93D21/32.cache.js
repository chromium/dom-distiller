function _D(){_D=Anc;$D=new dlc}
function FUb(a,b,c,d){var e;a.b.hg(b,c);e=HUb(a.b.j,b,c);pj(e,d,true)}
function aE(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.yd(c)}}
function cE(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.pd(a);return String(c)}
function eE(){_D();var a;a=kI($D.td(Bzc),60);if(!a){a=new dE;$D.vd(Bzc,a)}return a}
function bE(c,b){try{typeof $wnd[b]!='object'&&gE(b);c.b=$wnd[b]}catch(a){gE(b)}}
function gE(a){throw new emc(rsc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function dE(){this.c='Dictionary userInfo';bE(this,Bzc);if(!this.b){throw new emc("Cannot find JavaScript object with the name 'userInfo'")}}
function Bkb(){var a,b,c,d,e,f,g,i,j,k,n;f=new s5b;g=new WRb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=Qrc;g.db.style['textAlign']=dsc;p5b(f,new WRb('<b>This example interacts with the following JavaScript variable:<\/b>'));p5b(f,g);j=new xUb;b=j.k;i=eE();e=(n=new llc,aE(i,n),n);a=0;for(d=hic(fF(e.b));d.b.Ed();){c=kI(nic(d),1);k=cE(i,c);oUb(j,0,a,c);FUb(b,0,a,'cw-DictionaryExample-header');oUb(j,1,a,k);FUb(b,1,a,'cw-DictionaryExample-data');++a}p5b(f,new WRb('<br><br>'));p5b(f,j);return f}
var Bzc='userInfo';d2(346,1,{60:1},dE);_.pd=function fE(a){var b;b="Cannot find '"+a+"' in "+this;throw new emc(b)};_.tS=function hE(){return this.c};_.b=null;_.c=null;var $D;d2(643,1,voc);_.qc=function Hkb(){I4(this.b,Bkb())};var DN=Kcc(Bvc,atc,346);ipc(Jn)(32);