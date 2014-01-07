function uD(){uD=Rmc;tD=new ukc}
function cUb(a,b,c,d){var e;a.a._f(b,c);e=eUb(a.a.i,b,c);dj(e,d,true)}
function vD(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.qd(c)}}
function xD(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.gd(a);return String(c)}
function zD(){uD();var a;a=FH(tD.ld(Gyc),60);if(!a){a=new yD;tD.nd(Gyc,a)}return a}
function wD(c,b){try{typeof $wnd[b]!='object'&&BD(b);c.a=$wnd[b]}catch(a){BD(b)}}
function BD(a){throw new vlc(yrc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function yD(){this.b='Dictionary userInfo';wD(this,Gyc);if(!this.a){throw new vlc("Cannot find JavaScript object with the name 'userInfo'")}}
function Ujb(){var a,b,c,d,e,f,g,i,j,k,n;f=new S4b;g=new tRb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.cb.dir=Tqc;g.cb.style['textAlign']=krc;P4b(f,new tRb('<b>This example interacts with the following JavaScript variable:<\/b>'));P4b(f,g);j=new WTb;b=j.j;i=zD();e=(n=new Ckc,vD(i,n),n);a=0;for(d=yhc(AE(e.a));d.a.wd();){c=FH(Ehc(d),1);k=xD(i,c);NTb(j,0,a,c);cUb(b,0,a,'cw-DictionaryExample-header');NTb(j,1,a,k);cUb(b,1,a,'cw-DictionaryExample-data');++a}P4b(f,new tRb('<br><br>'));P4b(f,j);return f}
var Gyc='userInfo';r1(344,1,{60:1},yD);_.gd=function AD(a){var b;b="Cannot find '"+a+"' in "+this;throw new vlc(b)};_.tS=function CD(){return this.b};_.a=null;_.b=null;var tD;r1(642,1,Lnc);_.lc=function $jb(){_3(this.a,Ujb())};var RM=acc(Huc,hsc,344);yoc(wn)(32);