function dXb(){eXb.call(this,false)}
function BXb(a,b){DXb.call(this,a,false);this.c=b}
function CXb(a,b){DXb.call(this,a,false);AXb(this,b)}
function EXb(a){DXb.call(this,'GWT',true);AXb(this,a)}
function mlb(a){this.d=a;this.c=t5(this.d.b)}
function JWb(a,b){return QWb(a,b,a.b.c)}
function Pb(a,b){$b((ie(),de),a,uH(m0,Cmc,135,[(mbc(),b?lbc:kbc)]))}
function AXb(a,b){a.e=b;!!a.d&&cXb(a.d,a);if(b){b.db.tabIndex=-1;yf();Pb(a.db,true)}else{yf();Pb(a.db,false)}}
function QWb(a,b,c){if(c<0||c>a.b.c){throw new dbc}a.p&&(b.db[avc]=2,undefined);IWb(a,c,b.db);Chc(a.b,c,b);return b}
function HXb(){var a;Ni(this,$doc.createElement(itc));this.db[xpc]='gwt-MenuItemSeparator';a=$doc.createElement(Cpc);xJb(this.db,a);a[xpc]='menuSeparatorInner'}
function r5(a){var b,c;b=EH(a.b.fd(Bvc),149);if(b==null){c=uH(r0,Dmc,1,['\u4E0B\u8F7D',Lrc,Nrc,'GWT \u9AD8\u624B\u7A0B\u5E8F']);a.b.hd(Bvc,c);return c}else{return b}}
function s5(a){var b,c;b=EH(a.b.fd(Cvc),149);if(b==null){c=uH(r0,Dmc,1,['\u5185\u5BB9','\u5E78\u8FD0\u997C','\u5173\u4E8EGWT']);a.b.hd(Cvc,c);return c}else{return b}}
function o5(a){var b,c;b=EH(a.b.fd(xvc),149);if(b==null){c=uH(r0,Dmc,1,['\u64A4\u6D88','\u91CD\u590D','\u526A\u5207','\u590D\u5236','\u7C98\u8D34']);a.b.hd(xvc,c);return c}else{return b}}
function p5(a){var b,c;b=EH(a.b.fd(yvc),149);if(b==null){c=uH(r0,Dmc,1,['\u65B0\u5EFA','\u6253\u5F00',zvc,'\u8FD1\u671F\u6587\u4EF6','\u9000\u51FA']);a.b.hd(yvc,c);return c}else{return b}}
function q5(a){var b,c;b=EH(a.b.fd(Avc),149);if(b==null){c=uH(r0,Dmc,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.hd(Avc,c);return c}else{return b}}
function t5(a){var b,c;b=EH(a.b.fd(Dvc),149);if(b==null){c=uH(r0,Dmc,1,['\u611F\u8C22\u60A8\u9009\u62E9\u83DC\u5355\u9879','\u9009\u5F97\u5F88\u4E0D\u9519','\u9664\u4E86\u9009\u62E9\u83DC\u5355\u9879\u4E4B\u5916\u96BE\u9053\u6CA1\u6709\u66F4\u597D\u7684\u9009\u62E9\uFF1F','\u8BD5\u8BD5\u522B\u7684\u5427','\u8FD9\u4E0D\u8FC7\u662F\u4E2A\u83DC\u5355\u800C\u5DF2\uFF01','\u53C8\u6D6A\u8D39\u4E86\u4E00\u6B21\u70B9\u51FB']);a.b.hd(Dvc,c);return c}else{return b}}
function ilb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new mlb(a);n=new dXb;n.c=true;n.db.style[ypc]='500px';n.f=true;q=new eXb(true);p=q5(a.b);for(k=0;k<p.length;++k){HWb(q,new BXb(p[k],o))}d=new eXb(true);d.f=true;HWb(n,new CXb('\u6587\u4EF6',d));e=p5(a.b);for(k=0;k<e.length;++k){if(k==3){JWb(d,new HXb);HWb(d,new CXb(e[3],q));JWb(d,new HXb)}else{HWb(d,new BXb(e[k],o))}}b=new eXb(true);HWb(n,new CXb('\u7F16\u8F91',b));c=o5(a.b);for(k=0;k<c.length;++k){HWb(b,new BXb(c[k],o))}f=new eXb(true);HWb(n,new EXb(f));g=r5(a.b);for(k=0;k<g.length;++k){HWb(f,new BXb(g[k],o))}i=new eXb(true);JWb(n,new HXb);HWb(n,new CXb('\u5E2E\u52A9',i));j=s5(a.b);for(k=0;k<j.length;++k){HWb(i,new BXb(j[k],o))}g4b(n.db,Yoc,Evc);bXb(n,Evc);return n}
var Evc='cwMenuBar',xvc='cwMenuBarEditOptions',yvc='cwMenuBarFileOptions',Avc='cwMenuBarFileRecents',Bvc='cwMenuBarGWTOptions',Cvc='cwMenuBarHelpOptions',Dvc='cwMenuBarPrompts';t1(661,1,{},mlb);_.oc=function nlb(){nKb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;t1(662,1,qnc);_.mc=function rlb(){Y3(this.c,ilb(this.b))};t1(1056,102,Fmc,dXb);t1(1063,103,{100:1,105:1,119:1},BXb,CXb,EXb);t1(1064,103,{100:1,106:1,119:1},HXb);var ER=Hbc(auc,'CwMenuBar$1',661),qX=Hbc($tc,'MenuItemSeparator',1064);doc(wn)(7);