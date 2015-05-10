function embedGuestbook(src, width, height, flashvars) {
	if (navigator.appName.indexOf('Explorer') != -1) {
		document.write('<object classid="CLSID:D27CDB6E-AE6D-11CF-96B8-444553540000" ');
		document.write('codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" '); }
	else
		document.write('<object type="application/x-shockwave-flash" data="' + src + '" ');

	document.write('width="' + width + '" height="' + height + '" id="GuestBook" align="middle" />\n');
	document.write('<param name="flashvars" value="' + flashvars + '"/>');
	document.write('<param name="movie" value="' + src + '"/>');
	document.write('<\/object>');
}

