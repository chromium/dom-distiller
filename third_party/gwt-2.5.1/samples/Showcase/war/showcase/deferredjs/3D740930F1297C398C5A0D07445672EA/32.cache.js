function tF(){tF=_Qc;sF=new EOc}
function uF(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.ne(c)}}
function kmc(a,b,c,d){var e;a.a.Yg(b,c);e=mmc(a.a.i,b,c);cj(e,d,true)}
function yF(){tF();var a;a=q8(sF.ie(l1c),61);if(!a){a=new xF;sF.ke(l1c,a)}return a}
function vF(c,b){try{typeof $wnd[b]!='object'&&AF(b);c.a=$wnd[b]}catch(a){AF(b)}}
function AF(a){throw new FPc(LVc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function wF(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Rd(a);return String(c)}
function xF(){this.b='Dictionary userInfo';vF(this,l1c);if(!this.a){throw new FPc("Cannot find JavaScript object with the name 'userInfo'")}}
function VNb(){var a,b,c,d,e,f,g,i,j,k,n;f=new $yc;g=new Bjc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.cb.dir=eVc;g.cb.style['textAlign']=yVc;Xyc(f,new Bjc('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));Xyc(f,g);j=new cmc;b=j.j;i=yF();e=(n=new MOc,uF(i,n),n);a=0;for(d=ILc(DL(e.a));d.a.te();){c=q8(OLc(d),1);k=wF(i,c);Vlc(j,0,a,c);kmc(b,0,a,'cw-DictionaryExample-header');Vlc(j,1,a,k);kmc(b,1,a,'cw-DictionaryExample-data');++a}Xyc(f,new Bjc('<br><br>'));Xyc(f,j);return f}
var l1c='userInfo';svb(361,1,{61:1},xF);_.Rd=function zF(a){var b;b="Cannot find '"+a+"' in "+this;throw new FPc(b)};_.tS=function BF(){return this.b};_.a=null;_.b=null;var sF;svb(711,1,VRc);_.lc=function _Nb(){ayb(this.a,VNb())};var Udb=kGc(eZc,'Dictionary',361);ISc(vn)(32);