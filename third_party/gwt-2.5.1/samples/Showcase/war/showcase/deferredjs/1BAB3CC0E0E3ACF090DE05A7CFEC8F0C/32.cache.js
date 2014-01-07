function aF(){aF=cAc;_E=new Hxc}
function p5b(a,b,c,d){var e;a.b.ah(b,c);e=r5b(a.b.j,b,c);oj(e,d,true)}
function bF(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.re(c)}}
function dF(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Vd(a);return String(c)}
function fF(){aF();var a;a=AU(_E.me(oMc),61);if(!a){a=new eF;_E.oe(oMc,a)}return a}
function cF(c,b){try{typeof $wnd[b]!='object'&&hF(b);c.b=$wnd[b]}catch(a){hF(b)}}
function hF(a){throw new Iyc(PEc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function eF(){this.c='Dictionary userInfo';cF(this,oMc);if(!this.b){throw new Iyc("Cannot find JavaScript object with the name 'userInfo'")}}
function ixb(){var a,b,c,d,e,f,g,i,j,k,n;f=new _hc;g=new G2b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=hEc;g.db.style['textAlign']=BEc;Yhc(f,new G2b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));Yhc(f,g);j=new h5b;b=j.k;i=fF();e=(n=new Pxc,bF(i,n),n);a=0;for(d=Luc(cI(e.b));d.b.xe();){c=AU(Ruc(d),1);k=dF(i,c);$4b(j,0,a,c);p5b(b,0,a,'cw-DictionaryExample-header');$4b(j,1,a,k);p5b(b,1,a,'cw-DictionaryExample-data');++a}Yhc(f,new G2b('<br><br>'));Yhc(f,j);return f}
var oMc='userInfo';Meb(350,1,{61:1},eF);_.Vd=function gF(a){var b;b="Cannot find '"+a+"' in "+this;throw new Iyc(b)};_.tS=function iF(){return this.c};_.b=null;_.c=null;var _E;Meb(664,1,ZAc);_.qc=function oxb(){phb(this.b,ixb())};var XZ=npc(hIc,'Dictionary',350);MBc(In)(32);