function bF(){bF=cQc;aF=new HNc}
function Alc(a,b,c,d){var e;a.b.Yg(b,c);e=Clc(a.b.j,b,c);dj(e,d,true)}
function cF(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ne(c)}}
function eF(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Rd(a);return String(c)}
function gF(){bF();var a;a=$7(aF.ie(r0c),61);if(!a){a=new fF;aF.ke(r0c,a)}return a}
function dF(c,b){try{typeof $wnd[b]!='object'&&iF(b);c.b=$wnd[b]}catch(a){iF(b)}}
function iF(a){throw new IOc(PUc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function fF(){this.c='Dictionary userInfo';dF(this,r0c);if(!this.b){throw new IOc("Cannot find JavaScript object with the name 'userInfo'")}}
function wNb(){var a,b,c,d,e,f,g,i,j,k,n;f=new kyc;g=new Nic('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=hUc;g.db.style['textAlign']=BUc;hyc(f,new Nic('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));hyc(f,g);j=new slc;b=j.k;i=gF();e=(n=new PNc,cF(i,n),n);a=0;for(d=LKc(lL(e.b));d.b.te();){c=$7(RKc(d),1);k=eF(i,c);jlc(j,0,a,c);Alc(b,0,a,'cw-DictionaryExample-header');jlc(j,1,a,k);Alc(b,1,a,'cw-DictionaryExample-data');++a}hyc(f,new Nic('<br><br>'));hyc(f,j);return f}
var r0c='userInfo';$ub(359,1,{61:1},fF);_.Rd=function hF(a){var b;b="Cannot find '"+a+"' in "+this;throw new IOc(b)};_.tS=function jF(){return this.c};_.b=null;_.c=null;var aF;$ub(708,1,YQc);_.mc=function CNb(){Dxb(this.b,wNb())};var Ddb=nFc(kYc,'Dictionary',359);LRc(wn)(32);