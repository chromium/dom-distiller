function TE(){TE=jAc;SE=new Oxc}
function u5b(a,b,c,d){var e;a.a.Yg(b,c);e=w5b(a.a.i,b,c);cj(e,d,true)}
function UE(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.ne(c)}}
function WE(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Rd(a);return String(c)}
function YE(){TE();var a;a=rU(SE.ie(lMc),61);if(!a){a=new XE;SE.ke(lMc,a)}return a}
function VE(c,b){try{typeof $wnd[b]!='object'&&$E(b);c.a=$wnd[b]}catch(a){$E(b)}}
function $E(a){throw new Pyc(VEc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function XE(){this.b='Dictionary userInfo';VE(this,lMc);if(!this.a){throw new Pyc("Cannot find JavaScript object with the name 'userInfo'")}}
function dxb(){var a,b,c,d,e,f,g,i,j,k,n;f=new iic;g=new L2b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.cb.dir=oEc;g.cb.style['textAlign']=IEc;fic(f,new L2b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));fic(f,g);j=new m5b;b=j.j;i=YE();e=(n=new Wxc,UE(i,n),n);a=0;for(d=Suc(VH(e.a));d.a.te();){c=rU(Yuc(d),1);k=WE(i,c);d5b(j,0,a,c);u5b(b,0,a,'cw-DictionaryExample-header');d5b(j,1,a,k);u5b(b,1,a,'cw-DictionaryExample-data');++a}fic(f,new L2b('<br><br>'));fic(f,j);return f}
var lMc='userInfo';Ceb(351,1,{61:1},XE);_.Rd=function ZE(a){var b;b="Cannot find '"+a+"' in "+this;throw new Pyc(b)};_.tS=function _E(){return this.b};_.a=null;_.b=null;var SE;Ceb(666,1,dBc);_.lc=function jxb(){khb(this.a,dxb())};var LZ=upc(eIc,'Dictionary',351);SBc(vn)(32);