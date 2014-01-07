function IE(){IE=Rzc;HE=new uxc}
function c5b(a,b,c,d){var e;a.a.Yg(b,c);e=e5b(a.a.i,b,c);dj(e,d,true)}
function JE(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.ne(c)}}
function LE(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Rd(a);return String(c)}
function NE(){IE();var a;a=gU(HE.ie(RLc),61);if(!a){a=new ME;HE.ke(RLc,a)}return a}
function KE(c,b){try{typeof $wnd[b]!='object'&&PE(b);c.a=$wnd[b]}catch(a){PE(b)}}
function PE(a){throw new vyc(zEc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function ME(){this.b='Dictionary userInfo';KE(this,RLc);if(!this.a){throw new vyc("Cannot find JavaScript object with the name 'userInfo'")}}
function Uwb(){var a,b,c,d,e,f,g,i,j,k,n;f=new Shc;g=new t2b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.cb.dir=TDc;g.cb.style['textAlign']=lEc;Phc(f,new t2b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));Phc(f,g);j=new W4b;b=j.j;i=NE();e=(n=new Cxc,JE(i,n),n);a=0;for(d=yuc(KH(e.a));d.a.te();){c=gU(Euc(d),1);k=LE(i,c);N4b(j,0,a,c);c5b(b,0,a,'cw-DictionaryExample-header');N4b(j,1,a,k);c5b(b,1,a,'cw-DictionaryExample-data');++a}Phc(f,new t2b('<br><br>'));Phc(f,j);return f}
var RLc='userInfo';reb(352,1,{61:1},ME);_.Rd=function OE(a){var b;b="Cannot find '"+a+"' in "+this;throw new vyc(b)};_.tS=function QE(){return this.b};_.a=null;_.b=null;var HE;reb(667,1,LAc);_.lc=function $wb(){_gb(this.a,Uwb())};var AZ=apc(LHc,'Dictionary',352);yBc(wn)(32);