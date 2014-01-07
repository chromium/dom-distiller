function CF(){CF=UQc;BF=new xOc}
function fmc(a,b,c,d){var e;a.b.ah(b,c);e=hmc(a.b.j,b,c);oj(e,d,true)}
function DF(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.re(c)}}
function FF(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Vd(a);return String(c)}
function HF(){CF();var a;a=z8(BF.me(o1c),61);if(!a){a=new GF;BF.oe(o1c,a)}return a}
function EF(c,b){try{typeof $wnd[b]!='object'&&JF(b);c.b=$wnd[b]}catch(a){JF(b)}}
function JF(a){throw new yPc(FVc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function GF(){this.c='Dictionary userInfo';EF(this,o1c);if(!this.b){throw new yPc("Cannot find JavaScript object with the name 'userInfo'")}}
function $Nb(){var a,b,c,d,e,f,g,i,j,k,n;f=new Ryc;g=new wjc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=ZUc;g.db.style['textAlign']=rVc;Oyc(f,new wjc('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));Oyc(f,g);j=new Zlc;b=j.k;i=HF();e=(n=new FOc,DF(i,n),n);a=0;for(d=BLc(ML(e.b));d.b.xe();){c=z8(HLc(d),1);k=FF(i,c);Qlc(j,0,a,c);fmc(b,0,a,'cw-DictionaryExample-header');Qlc(j,1,a,k);fmc(b,1,a,'cw-DictionaryExample-data');++a}Oyc(f,new wjc('<br><br>'));Oyc(f,j);return f}
var o1c='userInfo';Cvb(360,1,{61:1},GF);_.Vd=function IF(a){var b;b="Cannot find '"+a+"' in "+this;throw new yPc(b)};_.tS=function KF(){return this.c};_.b=null;_.c=null;var BF;Cvb(709,1,PRc);_.qc=function eOb(){fyb(this.b,$Nb())};var eeb=dGc(hZc,'Dictionary',360);CSc(In)(32);