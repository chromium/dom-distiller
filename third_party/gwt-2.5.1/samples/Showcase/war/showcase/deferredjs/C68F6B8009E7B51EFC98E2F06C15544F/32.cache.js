function xD(){xD=wmc;wD=new _jc}
function UTb(a,b,c,d){var e;a.b.Wf(b,c);e=WTb(a.b.j,b,c);dj(e,d,true)}
function yD(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ld(c)}}
function AD(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.bd(a);return String(c)}
function CD(){xD();var a;a=EH(wD.fd(myc),61);if(!a){a=new BD;wD.hd(myc,a)}return a}
function zD(c,b){try{typeof $wnd[b]!='object'&&ED(b);c.b=$wnd[b]}catch(a){ED(b)}}
function ED(a){throw new alc(hrc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function BD(){this.c='Dictionary userInfo';zD(this,myc);if(!this.b){throw new alc("Cannot find JavaScript object with the name 'userInfo'")}}
function Rjb(){var a,b,c,d,e,f,g,i,j,k,n;f=new E4b;g=new fRb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.db.dir=Bqc;g.db.style['textAlign']=Vqc;B4b(f,new fRb('<b>\u8FD9\u4E2A\u4F8B\u5B50\u4F7F\u7528\u4E0B\u5217Javascript\u7684\u53D8\u91CF\uFF1A <\/b>'));B4b(f,g);j=new MTb;b=j.k;i=CD();e=(n=new hkc,yD(i,n),n);a=0;for(d=dhc(DE(e.b));d.b.rd();){c=EH(jhc(d),1);k=AD(i,c);DTb(j,0,a,c);UTb(b,0,a,'cw-DictionaryExample-header');DTb(j,1,a,k);UTb(b,1,a,'cw-DictionaryExample-data');++a}B4b(f,new fRb('<br><br>'));B4b(f,j);return f}
var myc='userInfo';t1(344,1,{61:1},BD);_.bd=function DD(a){var b;b="Cannot find '"+a+"' in "+this;throw new alc(b)};_.tS=function FD(){return this.c};_.b=null;_.c=null;var wD;t1(642,1,qnc);_.mc=function Xjb(){Y3(this.b,Rjb())};var VM=Hbc(puc,'Dictionary',344);doc(wn)(32);