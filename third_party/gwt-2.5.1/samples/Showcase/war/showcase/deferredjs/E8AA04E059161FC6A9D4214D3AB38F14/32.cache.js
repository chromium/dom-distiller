function nD(){nD=mmc;mD=new Rjc}
function KTb(a,b,c,d){var e;a.b._f(b,c);e=MTb(a.b.j,b,c);dj(e,d,true)}
function oD(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.qd(c)}}
function qD(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.gd(a);return String(c)}
function sD(){nD();var a;a=yH(mD.ld(gyc),60);if(!a){a=new rD;mD.nd(gyc,a)}return a}
function pD(c,b){try{typeof $wnd[b]!='object'&&uD(b);c.b=$wnd[b]}catch(a){uD(b)}}
function uD(a){throw new Skc(Yqc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function rD(){this.c='Dictionary userInfo';pD(this,gyc);if(!this.b){throw new Skc("Cannot find JavaScript object with the name 'userInfo'")}}
function Gjb(){var a,b,c,d,e,f,g,i,j,k,n;f=new u4b;g=new XQb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=rqc;g.db.style['textAlign']=Kqc;r4b(f,new XQb('<b>This example interacts with the following JavaScript variable:<\/b>'));r4b(f,g);j=new CTb;b=j.k;i=sD();e=(n=new Zjc,oD(i,n),n);a=0;for(d=Vgc(tE(e.b));d.b.wd();){c=yH(_gc(d),1);k=qD(i,c);tTb(j,0,a,c);KTb(b,0,a,'cw-DictionaryExample-header');tTb(j,1,a,k);KTb(b,1,a,'cw-DictionaryExample-data');++a}r4b(f,new XQb('<br><br>'));r4b(f,j);return f}
var gyc='userInfo';i1(341,1,{60:1},rD);_.gd=function tD(a){var b;b="Cannot find '"+a+"' in "+this;throw new Skc(b)};_.tS=function vD(){return this.c};_.b=null;_.c=null;var mD;i1(638,1,gnc);_.mc=function Mjb(){N3(this.b,Gjb())};var LM=xbc(guc,Hrc,341);Vnc(wn)(32);