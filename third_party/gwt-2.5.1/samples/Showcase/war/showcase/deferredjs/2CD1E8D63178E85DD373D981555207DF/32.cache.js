function $F(){$F=v2c;ZF=new $_c}
function _F(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.ne(c)}}
function Gzc(a,b,c,d){var e;a.a.Yg(b,c);e=Izc(a.a.i,b,c);cj(e,d,true)}
function dG(){$F();var a;a=vlb(ZF.ie(Red),61);if(!a){a=new cG;ZF.ke(Red,a)}return a}
function aG(c,b){try{typeof $wnd[b]!='object'&&fG(b);c.a=$wnd[b]}catch(a){fG(b)}}
function fG(a){throw new _0c(f7c+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function bG(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Rd(a);return String(c)}
function cG(){this.b='Dictionary userInfo';aG(this,Red);if(!this.a){throw new _0c("Cannot find JavaScript object with the name 'userInfo'")}}
function p_b(){var a,b,c,d,e,f,g,i,j,k,n;f=new uMc;g=new Xwc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.cb.dir=A6c;g.cb.style['textAlign']=U6c;rMc(f,new Xwc('<b>This example interacts with the following JavaScript variable:<\/b>'));rMc(f,g);j=new yzc;b=j.j;i=dG();e=(n=new g0c,_F(i,n),n);a=0;for(d=cZc(xN(e.a));d.a.te();){c=vlb(iZc(d),1);k=bG(i,c);pzc(j,0,a,c);Gzc(b,0,a,'cw-DictionaryExample-header');pzc(j,1,a,k);Gzc(b,1,a,'cw-DictionaryExample-data');++a}rMc(f,new Xwc('<br><br>'));rMc(f,j);return f}
var Red='userInfo';OIb(366,1,{61:1},cG);_.Rd=function eG(a){var b;b="Cannot find '"+a+"' in "+this;throw new _0c(b)};_.tS=function gG(){return this.b};_.a=null;_.b=null;var ZF;OIb(728,1,p3c);_.lc=function v_b(){wLb(this.a,p_b())};var crb=GTc(Mad,n8c,366);c4c(vn)(32);