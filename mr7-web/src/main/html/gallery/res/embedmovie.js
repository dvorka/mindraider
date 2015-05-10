/* embedmovie.js
 * Author: Laszlo Molnar / Hungary
 * Date: 01/11/2006
 * Version: 0.5
 */
 
var mtype = new Array (".avi.mp3", ".qt.mov.mpg.mpeg.mpe.mp4.aiff", ".wmv.wma.asf", ".swf.flv", ".divx.xvid" );
var cheight = new Array (0, 16, 64, 0, 20);
var audio = ".mp3.wav.wma.aiff.mid.rm.ram";

function addParam(name, value) {
  return '<param name="' + name + '" value="' + value + '" />\n';
}

function embedMovie(src, width, height, autoplay, hide, scaletofit) {
  var isExplorer = (navigator.appName.indexOf('Explorer') != -1);
  var ext = src.substr(src.lastIndexOf('.')).toLowerCase();
  var isAudio = (audio.indexOf(ext) != -1);

  for(i = 0; i < mtype.length; i++)
    if(mtype[i].indexOf(ext) != -1) break;

  if(i == 0) { i = (navigator.userAgent.indexOf('Macintosh') != -1)? 1 : 2; }

  if(!scaletofit && audio.indexOf(ext) != -1) height = 0;
    
  if(hide) width = height = 0;
  else height += (i < mtype.length)? cheight[i] : 45;

  switch (i) {
  
  case 1: // QuickTime Movie

    if(isExplorer) {
      document.write('<object classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" ');
      document.write('codebase="http://www.apple.com/qtactivex/qtplugin.cab#version=6,0,2,0" '); }
    else
      document.write('<object type="video/quicktime" data="' + src + '"');

    document.write(' width="' + width + '" height="' + height + '" id="QuickTimePlayer">\n');
    document.write(addParam("src", src));
    document.write(addParam("autoplay", autoplay));
    document.write(addParam("bgcolor", "black"));
    if(scaletofit) document.write(addParam("scale", "tofit"));
    document.write('</object>\n');
    break;

  case 2: // Windows Media Player 

    if(src.indexOf('/') == -1) src = './' + src;

    if(isExplorer)
      document.write('<object classid="CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6" ');
    else
      document.write('<object type="video/x-ms-wmv" data="' + src + '" ');

    document.write('width="' + width + '" height="' + height + '" id="MediaPlayer">\n');
    if(isExplorer) document.write(addParam("URL", src));
    document.write(addParam("src", src));
    document.write(addParam("AutoStart", autoplay? '1':'0'));
    if(scaletofit) document.write(addParam("StretchToFit", '1'));
    document.write(addParam(isExplorer? "ShowControls":"Controller", '1'));
    document.write('</object>\n');
    break;

  case 3: // Flash Animation
    if(isExplorer) {
      document.write('<object classid="CLSID:D27CDB6E-AE6D-11CF-96B8-444553540000" ');
      document.write('codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" '); }
    else
      document.write('<object type="application/x-shockwave-flash" data="' + src + '" ');

    document.write('width="' + width + '" height="' + height + '" id="FlashPlayer" align="middle" />\n');
    if(ext.charAt(1) == 'f')
      document.write(addParam("movie", src + '&autoStart=' + autoplay));
    else
      document.write(addParam("movie", src));
    document.write(addParam("allowScriptAccess", "sameDomain"));
    document.write(addParam("quality", "high"));
    document.write('</object>\n');
    break;

  case 4: // DivX Movie
    if(isExplorer) {
      document.write('<object classid="CLSID:67DABFBF-D0AB-41fa-9C46-CC0F21721616" ');
      document.write('codebase="http://go.divx.com/plugin/DivXBrowserPlugin.cab" '); }
    else
      document.write('<object type="video/divx" data="' + src + '" ');

    document.write('width="' + width + '" height="' + height + '" pluginspage="http://go.divx.com/plugin/download/" id="DivxPlayer">\n');
    document.write(addParam("mode", "zero"));
    document.write(addParam("autoPlay", autoplay));
    document.write(addParam("allowContextMenu", "false"));
    if(src.charAt(0) == "." || src.charAt(0) == "/" || src.indexOf("http:") == 0)
      document.write(addParam("src", src));
    else
      document.write(addParam("src", "./" + src));
    document.write('</object>\n');
    break;
	
  default: // Undefined
    document.write('<embed src="' + src + '" autostart="' + autoplay + '" width="' + width + '" height="' + height + '" loop="false"></embed>');
  }
}

