function iF(){iF=HQc;hF=new kOc}
function jF(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.ne(c)}}
function Ulc(a,b,c,d){var e;a.a.Yg(b,c);e=Wlc(a.a.i,b,c);dj(e,d,true)}
function nF(){iF();var a;a=f8(hF.ie(R0c),61);if(!a){a=new mF;hF.ke(R0c,a)}return a}
function kF(c,b){try{typeof $wnd[b]!='object'&&pF(b);c.a=$wnd[b]}catch(a){pF(b)}}
function pF(a){throw new lPc(pVc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function lF(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Rd(a);return String(c)}
function mF(){this.b='Dictionary userInfo';kF(this,R0c);if(!this.a){throw new lPc("Cannot find JavaScript object with the name 'userInfo'")}}
function KNb(){var a,b,c,d,e,f,g,i,j,k,n;f=new Iyc;g=new jjc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.cb.dir=JUc;g.cb.style['textAlign']=bVc;Fyc(f,new jjc('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));Fyc(f,g);j=new Mlc;b=j.j;i=nF();e=(n=new sOc,jF(i,n),n);a=0;for(d=oLc(sL(e.a));d.a.te();){c=f8(uLc(d),1);k=lF(i,c);Dlc(j,0,a,c);Ulc(b,0,a,'cw-DictionaryExample-header');Dlc(j,1,a,k);Ulc(b,1,a,'cw-DictionaryExample-data');++a}Fyc(f,new jjc('<br><br>'));Fyc(f,j);return f}
var R0c='userInfo';hvb(362,1,{61:1},mF);_.Rd=function oF(a){var b;b="Cannot find '"+a+"' in "+this;throw new lPc(b)};_.tS=function qF(){return this.b};_.a=null;_.b=null;var hF;hvb(712,1,BRc);_.lc=function QNb(){Rxb(this.a,KNb())};var Jdb=SFc(LYc,'Dictionary',362);oSc(wn)(32);