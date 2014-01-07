function hG(){hG=o2c;gG=new T_c}
function Bzc(a,b,c,d){var e;a.b.ah(b,c);e=Dzc(a.b.j,b,c);oj(e,d,true)}
function iG(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.re(c)}}
function kG(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Vd(a);return String(c)}
function mG(){hG();var a;a=Elb(gG.me(Ued),61);if(!a){a=new lG;gG.oe(Ued,a)}return a}
function jG(c,b){try{typeof $wnd[b]!='object'&&oG(b);c.b=$wnd[b]}catch(a){oG(b)}}
function oG(a){throw new U0c(_6c+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function lG(){this.c='Dictionary userInfo';jG(this,Ued);if(!this.b){throw new U0c("Cannot find JavaScript object with the name 'userInfo'")}}
function u_b(){var a,b,c,d,e,f,g,i,j,k,n;f=new lMc;g=new Swc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=t6c;g.db.style['textAlign']=N6c;iMc(f,new Swc('<b>This example interacts with the following JavaScript variable:<\/b>'));iMc(f,g);j=new tzc;b=j.k;i=mG();e=(n=new __c,iG(i,n),n);a=0;for(d=XYc(GN(e.b));d.b.xe();){c=Elb(bZc(d),1);k=kG(i,c);kzc(j,0,a,c);Bzc(b,0,a,'cw-DictionaryExample-header');kzc(j,1,a,k);Bzc(b,1,a,'cw-DictionaryExample-data');++a}iMc(f,new Swc('<br><br>'));iMc(f,j);return f}
var Ued='userInfo';YIb(365,1,{61:1},lG);_.Vd=function nG(a){var b;b="Cannot find '"+a+"' in "+this;throw new U0c(b)};_.tS=function pG(){return this.c};_.b=null;_.c=null;var gG;YIb(726,1,j3c);_.qc=function A_b(){BLb(this.b,u_b())};var orb=zTc(Pad,h8c,365);Y3c(In)(32);