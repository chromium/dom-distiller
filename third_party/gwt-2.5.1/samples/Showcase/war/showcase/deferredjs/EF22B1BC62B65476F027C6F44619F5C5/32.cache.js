function PF(){PF=qRc;OF=new VOc}
function vmc(a,b,c,d){var e;a.b.eh(b,c);e=xmc(a.b.j,b,c);pj(e,d,true)}
function QF(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ve(c)}}
function SF(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Zd(a);return String(c)}
function UF(){PF();var a;a=M8(OF.qe(M1c),61);if(!a){a=new TF;OF.se(M1c,a)}return a}
function RF(c,b){try{typeof $wnd[b]!='object'&&WF(b);c.b=$wnd[b]}catch(a){WF(b)}}
function WF(a){throw new WPc(iWc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function TF(){this.c='Dictionary userInfo';RF(this,M1c);if(!this.b){throw new WPc("Cannot find JavaScript object with the name 'userInfo'")}}
function rOb(){var a,b,c,d,e,f,g,i,j,k,n;f=new izc;g=new Mjc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=GVc;g.db.style['textAlign']=WVc;fzc(f,new Mjc('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));fzc(f,g);j=new nmc;b=j.k;i=UF();e=(n=new bPc,QF(i,n),n);a=0;for(d=ZLc(ZL(e.b));d.b.Be();){c=M8(dMc(d),1);k=SF(i,c);emc(j,0,a,c);vmc(b,0,a,'cw-DictionaryExample-header');emc(j,1,a,k);vmc(b,1,a,'cw-DictionaryExample-data');++a}fzc(f,new Mjc('<br><br>'));fzc(f,j);return f}
var M1c='userInfo';Vvb(364,1,{61:1},TF);_.Zd=function VF(a){var b;b="Cannot find '"+a+"' in "+this;throw new WPc(b)};_.tS=function XF(){return this.c};_.b=null;_.c=null;var OF;Vvb(713,1,lSc);_.qc=function xOb(){yyb(this.b,rOb())};var veb=AGc(FZc,'Dictionary',364);$Sc(Jn)(32);