function MCc(){NCc.call(this,false)}
function iDc(a,b){kDc.call(this,a,false);this.c=b}
function jDc(a,b){kDc.call(this,a,false);hDc(this,b)}
function lDc(a){kDc.call(this,'GWT',true);hDc(this,a)}
function S0b(a){this.d=a;this.c=YMb(this.d.b)}
function qCc(a,b){return xCc(a,b,a.b.c)}
function $b(a,b){jc((te(),oe),a,ulb(RHb,v2c,135,[(eTc(),b?dTc:cTc)]))}
function hDc(a,b){a.e=b;!!a.d&&LCc(a.d,a);if(b){b.db.tabIndex=-1;Jf();$b(a.db,true)}else{Jf();$b(a.db,false)}}
function xCc(a,b,c){if(c<0||c>a.b.c){throw new XSc}a.p&&(b.db[Cbd]=2,undefined);pCc(a,c,b.db);uZc(a.b,c,b);return b}
function UMb(a){var b,c;b=Elb(a.b.me($bd),149);if(b==null){c=ulb(WHb,w2c,1,['New','Open',_bd,acd,'Exit']);a.b.oe($bd,c);return c}else{return b}}
function TMb(a){var b,c;b=Elb(a.b.me(Zbd),149);if(b==null){c=ulb(WHb,w2c,1,['Undo','Redo','Cut','Copy','Paste']);a.b.oe(Zbd,c);return c}else{return b}}
function XMb(a){var b,c;b=Elb(a.b.me(dcd),149);if(b==null){c=ulb(WHb,w2c,1,['Contents','Fortune Cookie','About GWT']);a.b.oe(dcd,c);return c}else{return b}}
function WMb(a){var b,c;b=Elb(a.b.me(ccd),149);if(b==null){c=ulb(WHb,w2c,1,['Download','Examples',c8c,"GWT wit' the program"]);a.b.oe(ccd,c);return c}else{return b}}
function VMb(a){var b,c;b=Elb(a.b.me(bcd),149);if(b==null){c=ulb(WHb,w2c,1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.oe(bcd,c);return c}else{return b}}
function oDc(){var a;Yi(this,$doc.createElement(H9c));this.db[q5c]='gwt-MenuItemSeparator';a=$doc.createElement(v5c);epc(this.db,a);a[q5c]='menuSeparatorInner'}
function YMb(a){var b,c;b=Elb(a.b.me(ecd),149);if(b==null){c=ulb(WHb,w2c,1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.oe(ecd,c);return c}else{return b}}
function O0b(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new S0b(a);n=new MCc;n.c=true;n.db.style[r5c]='500px';n.f=true;q=new NCc(true);p=VMb(a.b);for(k=0;k<p.length;++k){oCc(q,new iDc(p[k],o))}d=new NCc(true);d.f=true;oCc(n,new jDc('File',d));e=UMb(a.b);for(k=0;k<e.length;++k){if(k==3){qCc(d,new oDc);oCc(d,new jDc(e[3],q));qCc(d,new oDc)}else{oCc(d,new iDc(e[k],o))}}b=new NCc(true);oCc(n,new jDc('Edit',b));c=TMb(a.b);for(k=0;k<c.length;++k){oCc(b,new iDc(c[k],o))}f=new NCc(true);oCc(n,new lDc(f));g=WMb(a.b);for(k=0;k<g.length;++k){oCc(f,new iDc(g[k],o))}i=new NCc(true);qCc(n,new oDc);oCc(n,new jDc('Help',i));j=XMb(a.b);for(k=0;k<j.length;++k){oCc(i,new iDc(j[k],o))}PLc(n.db,R4c,fcd);KCc(n,fcd);return n}
var fcd='cwMenuBar',Zbd='cwMenuBarEditOptions',$bd='cwMenuBarFileOptions',bcd='cwMenuBarFileRecents',ccd='cwMenuBarGWTOptions',dcd='cwMenuBarHelpOptions',ecd='cwMenuBarPrompts';YIb(746,1,{},S0b);_.sc=function T0b(){Vpc(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.b=0;_.d=null;YIb(747,1,j3c);_.qc=function X0b(){BLb(this.c,O0b(this.b))};YIb(1142,104,y2c,MCc);YIb(1149,105,{100:1,105:1,119:1},iDc,jDc,lDc);YIb(1150,105,{100:1,106:1,119:1},oDc);var gxb=zTc(Aad,'CwMenuBar$1',746),UCb=zTc(yad,'MenuItemSeparator',1150);Y3c(In)(7);