function uG(){uG=M2c;tG=new p0c}
function vG(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ve(c)}}
function Rzc(a,b,c,d){var e;a.b.eh(b,c);e=Tzc(a.b.j,b,c);pj(e,d,true)}
function zG(){uG();var a;a=Rlb(tG.qe(qfd),61);if(!a){a=new yG;tG.se(qfd,a)}return a}
function wG(c,b){try{typeof $wnd[b]!='object'&&BG(b);c.b=$wnd[b]}catch(a){BG(b)}}
function BG(a){throw new q1c(E7c+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function xG(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Zd(a);return String(c)}
function yG(){this.c='Dictionary userInfo';wG(this,qfd);if(!this.b){throw new q1c("Cannot find JavaScript object with the name 'userInfo'")}}
function N_b(){var a,b,c,d,e,f,g,i,j,k,n;f=new EMc;g=new gxc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=a7c;g.db.style['textAlign']=q7c;BMc(f,new gxc('<b>This example interacts with the following JavaScript variable:<\/b>'));BMc(f,g);j=new Jzc;b=j.k;i=zG();e=(n=new x0c,vG(i,n),n);a=0;for(d=tZc(TN(e.b));d.b.Be();){c=Rlb(zZc(d),1);k=xG(i,c);Azc(j,0,a,c);Rzc(b,0,a,'cw-DictionaryExample-header');Azc(j,1,a,k);Rzc(b,1,a,'cw-DictionaryExample-data');++a}BMc(f,new gxc('<br><br>'));BMc(f,j);return f}
var qfd='userInfo';pJb(369,1,{61:1},yG);_.Zd=function AG(a){var b;b="Cannot find '"+a+"' in "+this;throw new q1c(b)};_.tS=function CG(){return this.c};_.b=null;_.c=null;var tG;pJb(730,1,H3c);_.qc=function T_b(){ULb(this.b,N_b())};var Frb=WTc(lbd,M8c,369);u4c(Jn)(32);