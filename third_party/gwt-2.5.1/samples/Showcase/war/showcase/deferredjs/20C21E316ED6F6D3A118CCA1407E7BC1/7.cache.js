function bYb(){cYb.call(this,false)}
function zYb(a,b){BYb.call(this,a,false);this.c=b}
function AYb(a,b){BYb.call(this,a,false);yYb(this,b)}
function CYb(a){BYb.call(this,'GWT',true);yYb(this,a)}
function hmb(a){this.d=a;this.c=o6(this.d.b)}
function GXb(a,b){return OXb(a,b,a.b.c)}
function _b(a,b){kc((ue(),pe),a,gI(h1,Rnc,135,[(zcc(),b?ycc:xcc)]))}
function OXb(a,b,c){if(c<0||c>a.b.c){throw new qcc}a.p&&(b.db[vwc]=2,undefined);FXb(a,c,b.db);Qic(a.b,c,b);return b}
function yYb(a,b){a.e=b;!!a.d&&aYb(a.d,a);if(b){(cVb(),b.db).tabIndex=-1;Kf();_b(a.db,true)}else{Kf();_b(a.db,false)}}
function FYb(){var a;Zi(this,$doc.createElement(Cuc));this.db[Nqc]='gwt-MenuItemSeparator';a=$doc.createElement(Sqc);sKb(this.db,a);a[Nqc]='menuSeparatorInner'}
function m6(a){var b,c;b=qI(a.b.od(Wwc),149);if(b==null){c=gI(m1,Snc,1,['\u4E0B\u8F7D',etc,gtc,'GWT \u9AD8\u624B\u7A0B\u5E8F']);a.b.qd(Wwc,c);return c}else{return b}}
function n6(a){var b,c;b=qI(a.b.od(Xwc),149);if(b==null){c=gI(m1,Snc,1,['\u5185\u5BB9','\u5E78\u8FD0\u997C','\u5173\u4E8EGWT']);a.b.qd(Xwc,c);return c}else{return b}}
function j6(a){var b,c;b=qI(a.b.od(Swc),149);if(b==null){c=gI(m1,Snc,1,['\u64A4\u6D88','\u91CD\u590D','\u526A\u5207','\u590D\u5236','\u7C98\u8D34']);a.b.qd(Swc,c);return c}else{return b}}
function k6(a){var b,c;b=qI(a.b.od(Twc),149);if(b==null){c=gI(m1,Snc,1,['\u65B0\u5EFA','\u6253\u5F00',Uwc,'\u8FD1\u671F\u6587\u4EF6','\u9000\u51FA']);a.b.qd(Twc,c);return c}else{return b}}
function l6(a){var b,c;b=qI(a.b.od(Vwc),149);if(b==null){c=gI(m1,Snc,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.qd(Vwc,c);return c}else{return b}}
function o6(a){var b,c;b=qI(a.b.od(Ywc),149);if(b==null){c=gI(m1,Snc,1,['\u611F\u8C22\u60A8\u9009\u62E9\u83DC\u5355\u9879','\u9009\u5F97\u5F88\u4E0D\u9519','\u9664\u4E86\u9009\u62E9\u83DC\u5355\u9879\u4E4B\u5916\u96BE\u9053\u6CA1\u6709\u66F4\u597D\u7684\u9009\u62E9\uFF1F','\u8BD5\u8BD5\u522B\u7684\u5427','\u8FD9\u4E0D\u8FC7\u662F\u4E2A\u83DC\u5355\u800C\u5DF2\uFF01','\u53C8\u6D6A\u8D39\u4E86\u4E00\u6B21\u70B9\u51FB']);a.b.qd(Ywc,c);return c}else{return b}}
function dmb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new hmb(a);n=new bYb;n.c=true;n.db.style[Oqc]='500px';n.f=true;q=new cYb(true);p=l6(a.b);for(k=0;k<p.length;++k){EXb(q,new zYb(p[k],o))}d=new cYb(true);d.f=true;EXb(n,new AYb('\u6587\u4EF6',d));e=k6(a.b);for(k=0;k<e.length;++k){if(k==3){GXb(d,new FYb);EXb(d,new AYb(e[3],q));GXb(d,new FYb)}else{EXb(d,new zYb(e[k],o))}}b=new cYb(true);EXb(n,new AYb('\u7F16\u8F91',b));c=j6(a.b);for(k=0;k<c.length;++k){EXb(b,new zYb(c[k],o))}f=new cYb(true);EXb(n,new CYb(f));g=m6(a.b);for(k=0;k<g.length;++k){EXb(f,new zYb(g[k],o))}i=new cYb(true);GXb(n,new FYb);EXb(n,new AYb('\u5E2E\u52A9',i));j=n6(a.b);for(k=0;k<j.length;++k){EXb(i,new zYb(j[k],o))}e5b(n.db,mqc,Zwc);_Xb(n,Zwc);return n}
var Zwc='cwMenuBar',Swc='cwMenuBarEditOptions',Twc='cwMenuBarFileOptions',Vwc='cwMenuBarFileRecents',Wwc='cwMenuBarGWTOptions',Xwc='cwMenuBarHelpOptions',Ywc='cwMenuBarPrompts';o2(666,1,{},hmb);_.sc=function imb(){iLb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;o2(667,1,Foc);_.qc=function mmb(){T4(this.c,dmb(this.b))};o2(1061,104,Unc,bYb);o2(1068,105,{100:1,105:1,119:1},zYb,AYb,CYb);o2(1069,105,{100:1,106:1,119:1},FYb);var wS=Ucc(vvc,'CwMenuBar$1',666),iY=Ucc(tvc,'MenuItemSeparator',1069);spc(Jn)(7);