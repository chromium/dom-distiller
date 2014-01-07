function nF(){nF=AAc;mF=new dyc}
function oF(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ve(c)}}
function F5b(a,b,c,d){var e;a.b.eh(b,c);e=H5b(a.b.j,b,c);pj(e,d,true)}
function sF(){nF();var a;a=NU(mF.qe(MMc),61);if(!a){a=new rF;mF.se(MMc,a)}return a}
function pF(c,b){try{typeof $wnd[b]!='object'&&uF(b);c.b=$wnd[b]}catch(a){uF(b)}}
function uF(a){throw new ezc(sFc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function qF(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Zd(a);return String(c)}
function rF(){this.c='Dictionary userInfo';pF(this,MMc);if(!this.b){throw new ezc("Cannot find JavaScript object with the name 'userInfo'")}}
function Bxb(){var a,b,c,d,e,f,g,i,j,k,n;f=new sic;g=new W2b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=QEc;g.db.style['textAlign']=eFc;pic(f,new W2b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));pic(f,g);j=new x5b;b=j.k;i=sF();e=(n=new lyc,oF(i,n),n);a=0;for(d=hvc(pI(e.b));d.b.Be();){c=NU(nvc(d),1);k=qF(i,c);o5b(j,0,a,c);F5b(b,0,a,'cw-DictionaryExample-header');o5b(j,1,a,k);F5b(b,1,a,'cw-DictionaryExample-data');++a}pic(f,new W2b('<br><br>'));pic(f,j);return f}
var MMc='userInfo';dfb(354,1,{61:1},rF);_.Zd=function tF(a){var b;b="Cannot find '"+a+"' in "+this;throw new ezc(b)};_.tS=function vF(){return this.c};_.b=null;_.c=null;var mF;dfb(668,1,vBc);_.qc=function Hxb(){Ihb(this.b,Bxb())};var m$=Kpc(FIc,'Dictionary',354);iCc(Jn)(32);