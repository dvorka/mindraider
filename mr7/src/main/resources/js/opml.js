/*
 ===========================================================================
   Copyright 2002-2010 Martin Dvorak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ===========================================================================
*/

function somethingWasClicked(evt){
	var evnt, obj, className;

	if (isIE4) evnt = window.event; else evnt = evt;
	obj = getTarget(evnt);
	button = getButton(evnt);

	className = getAttrVal(obj,'class');
	evnt.cancelBubble = true;
	
	if (contextNode) {if (obj != contextNode){unContext(contextNode);} else {return true;}}

	switch (className) {
	case 'outlineText':
		if (evnt.ctrlKey && (getAttrValEx(obj.parentNode, 'type') == 'link')){
			// let ctrl-click through
			window.open(getAttrValEx(obj.parentNode, 'url'));
			return false;
		}
		setContext(obj);
		if (evnt.altKey){
			editNode(obj);
			return false;
		}
		break;
	case 'markerOpen':
		hideThisBro(obj,evnt.shiftKey);
		refreshScreen(isIE4);
		return false;
		break;
	case 'markerClosed':
		showThisBro(obj,evnt.shiftKey);
		refreshScreen(isIE4);
		return false;
		break;
	default:
		evnt.cancelBubble = false;
		break;
	}
}

function mouseOver(evt){
	var evnt, obj, className;

	if (isIE4) evnt = window.event; else evnt = evt;
	obj = getTarget(evnt);

	className = getAttrVal(obj,'class');
	evnt.cancelBubble = true;

	switch (className) {
	case 'outlineText':
		if (obj != contextNode){
			obj.style.color = 'indigo';
			obj.style.background = 'lavender';
			// get rid of the following 'if' block if your mouseovers are slow
			if (getAttrValEx(obj.parentNode, 'type') == 'link'){
				obj.style.cursor = 'hand';
				obj.style.textDecoration = 'underline';
			}
		}
		return false;
		break;
	case 'markerOpen':
		return false;
		break;
	case 'markerClosed':
		return false;
		break;
	default:
		evnt.cancelBubble = false;
		break;
	}
}

function mouseOut(evt){
	var evnt, obj, className;

	if (isIE4) evnt = window.event; else evnt = evt;
	obj = getTarget(evnt);

	className = getAttrVal(obj,'class');
	evnt.cancelBubble = true;

	switch (className) {
	case 'outlineText':
		if (obj != contextNode){clearStyle(obj);}
		return false;
		break;
	case 'markerOpen':
		return false;
		break;
	case 'markerClosed':
		return false;
		break;
	default:
		evnt.cancelBubble = false;
		break;
	}
}


function keyStruck(evt){
	var k;
	if (isIE4) evnt = window.event; else evnt = evt;

	if (isIE4){k = evnt.keyCode;}
	else {k = evnt.which;}
	
	evnt.cancelBubble = true;
	
	switch (k){
	case 27:  // escape
		if (contextNode && inEdit){unEdit(contextNode);return false;}
		break;
	case 13:  // 'Enter'
		if (evnt.ctrlKey){
			if (contextNode && !inEdit){editNode(contextNode);return false;}
		}
		break;
	case 88:  // 'X'
		if (!inEdit && evnt.ctrlKey){
			toggleOPML();
		}
		return false;
		break;
	case 75:  // 'K'
		if (contextNode && !inEdit && evnt.ctrlKey){return(nodeLink(contextNode.parentNode));}
		break;
	case 191: // '/'
		if (contextNode && !inEdit){return(nodeCommentToggle(contextNode.parentNode));}
		break;
	case 46:  // delete
		if (contextNode && !inEdit){
			if (confirm('Do you really want to delete this node and all child nodes?!?')){
				blastNode(contextNode);
				return false;
		}}
		break;
	case 37:  // left arrow
		if (contextNode && !inEdit && evnt.ctrlKey){
			if(nodeLeft(contextNode.parentNode)) refreshScreen(isIE4 || isNav6);return false;}
		break;
	case 38:  // up arrow
		if (contextNode && !inEdit && evnt.ctrlKey){
			if(nodeUp(contextNode.parentNode)) refreshScreen(isIE4 || isNav6);return false;}
		break;
	case 39:  // right arrow
		if (contextNode && !inEdit && evnt.ctrlKey){
			if(nodeRight(contextNode.parentNode)) refreshScreen(isIE4);return false;}
		break;
	case 40:  // down arrow
		if (contextNode && !inEdit && evnt.ctrlKey){
			if(nodeDown(contextNode.parentNode)) refreshScreen(isIE4 || isNav6);return false;}
		break;
	case 45:  // insert
		if (contextNode && !inEdit){
			var newNode = nodeNew(contextNode.parentNode);
			if (newNode){
				refreshScreen(isIE4);
				unContext(contextNode);
				setContext(getOutlineText(newNode));
				editNode(getOutlineText(newNode));
				return false;
		}}
		break;
	default:
		break;
	}
	// alert(k);
	evnt.cancelBubble = false;
	return true;
}

/*------------------------------------------
outline manipulation routines
-------------------------------------------*/

function hideThisBro(mark,doAll){
	var curNode, nextNode, collapsed, i, j
	
	collapsed = false;
	for (i = 0; i < mark.parentNode.childNodes.length; i++){
		curNode = mark.parentNode.childNodes.item(i);
		if (getAttrVal(curNode, 'class') == 'outline'){
			if (doAll){
				nextNode = getOutlineMarker(curNode);
				if(nextNode){
					window.status = 'collapsing nodes...'
					hideThisBro(nextNode,true);
					window.status = '';
			}}
			curNode.style.display = 'none';
			collapsed = true;
	}}
	if (collapsed) setAttrVal(mark,'class','markerClosed');
	return collapsed;
}

function showThisBro(mark,doAll){
	var curNode, nextNode, i, j;
	
	for (i = 0; i < mark.parentNode.childNodes.length; i++){
		curNode = mark.parentNode.childNodes.item(i)
		if (getAttrVal(curNode, 'class') == 'outline'){
			if (doAll){
				nextNode = getOutlineMarker(curNode);
				if (nextNode){
					window.status = 'expanding nodes...';
					showThisBro(nextNode,true);
					window.status = '';
			}}
			curNode.style.display = 'block';
	}}
	setAttrVal(mark,'class','markerOpen');
}

function applyExpansionState(){
	// not implemented
}

function editNode(obj){
	if (isIE55) {htmlEdit(obj);}
	else {
		var newVal = window.prompt('edit',obj.innerHTML);
		if(newVal != null){obj.innerHTML = newVal;}
		refreshScreen(isIE4);
	}
}

function htmlEdit(obj){
	obj.contentEditable = true;
	inEdit = true;
}

function unEdit(obj){
	obj.contentEditable = false;
	inEdit = false;	
	refreshScreen(isIE4);
}

function setContext(obj){
	obj.style.color = 'black';
	obj.style.border = '1px silver dotted';
	obj.style.backgroundColor = 'blanchedalmond';
	obj.style.textDecoration = 'none';
	obj.style.cursor = 'default';
	contextNode = obj;
}

function unContext(obj){
	clearStyle(obj);
	if (isIE55){if (inEdit) {unEdit(obj);}}
	contextNode = null;
}

function clearStyle(obj){
	if (isNav6){obj.removeAttribute('style');}
	else {obj.style.cssText = '';}
}

function blastNode(obj){
	// deletes node and all children
	obj.parentNode.parentNode.removeChild(obj.parentNode);
	contextNode = null;
	refreshScreen(isIE4);
}

function nodeUp(obj){
	var sibNode = getPreviousSibling(obj,'outline');
	if (!sibNode){return false;}
	sibNode.parentNode.insertBefore(obj,sibNode);
	return true;
}

function nodeDown(obj){
	var sibNode = getNextSibling(obj,'outline');
	if (!sibNode){return false;}
	obj.parentNode.insertBefore(sibNode,obj);
	return true;
}

function nodeRight(obj){
	var sibNode = getPreviousSibling(obj,'outline');
	if (!sibNode){return false;}
	sibNode.insertBefore(obj,null);
	return true;
}

function nodeLeft(obj){
	var pNode = obj.parentNode;
	if (pNode){
		if (getAttrVal(pNode,'class') == 'outline'){
			var sibNode = getNextSibling(pNode,'outline');
			pNode.parentNode.insertBefore(obj,sibNode);
			return true;
		}
	}
	return false;
}

function nodeNew(obj){
	/* inserts a sibling node with text 'New Node'
	   directly preceding 'obj', and returns the
	   new node.
	*/
	var oNode, pNode, newNode, peerNode
	
	oNode = document.createElement('div');
	peerNode = obj;
	pNode = obj.parentNode;
	newNode = pNode.insertBefore(oNode,peerNode);
	if (newNode){
		setAttrVal(newNode,'class','outline');
		newNode.innerHTML = '<span class="markerOpen">' + document.getElementById('markerNormal').innerHTML + '</span><span class="outlineText">New Node</span>'
		return newNode;
	}
	return null;
}

function nodeLink(obj){
	var url = '';

	var isLink = (getAttrValEx(obj,'type') == 'link');
	var isComment = (getAttrValEx(obj,'isComment') == 'true');
	if (isLink){
		url = getAttrValEx(obj,'url');
	}
	url = window.prompt('Enter URL:',url);
	if (url == null) return false;
	if (url == '') {
		if (!isLink) return false;
		if (window.confirm('Remove this link and switch node to a normal node?')){
			setAttrValEx(obj,'url',null);
			setAttrValEx(obj,'type',null);
			getOutlineMarker(obj).innerHTML = document.getElementById('markerNormal').innerHTML;
			return false;
		}
	}
	setAttrValEx(obj,'url',url);
	if (!isLink){
		setAttrValEx(obj,'type','link');
		getOutlineMarker(obj).innerHTML = document.getElementById('markerLink').innerHTML;
		if (isComment){setAttrValEx(obj,'isComment',null);}
		return false;
	}
	return true;
}

function nodeCommentToggle(obj){
	var isLink = (getAttrValEx(obj,'type') == 'link');
	var isComment = (getAttrValEx(obj,'isComment') == 'true');

	if (isLink) {alert('Cannot comment a link.  First delete URL reference for this link (use ctrl-k)'); return false;}
	if (isComment){
		getOutlineMarker(obj).innerHTML = document.getElementById('markerNormal').innerHTML;
		setAttrValEx(obj,'isComment','true');
	}else{
		setAttrValEx(obj,'isComment','true');
		getOutlineMarker(obj).innerHTML = document.getElementById('markerComment').innerHTML;
	}
	return false;
}


/* ------------------------------------------------
	outline traversal functions
--------------------------------------------------*/

function getOutlineMarker(obj){
	var nextNode;
	
	for(j=0; j < obj.childNodes.length; j++){
		nextNode = obj.childNodes.item(j);
		className = 'unknown';
		className = getAttrVal(nextNode, 'class');
		if (className == 'markerOpen' || className == 'markerClosed'){
			return nextNode;
		}
	}
	return null;
}

function getOutlineText(obj){
	var nextNode;
	
	for(j=0; j < obj.childNodes.length; j++){
		nextNode = obj.childNodes.item(j);
		className = 'unknown';
		className = getAttrVal(nextNode, 'class');
		if (className == 'outlineText'){
			return nextNode;
		}
	}
	return null;
}

function getPreviousSibling(obj, className){
	var sibNode
	
	sibNode = obj;
	while (sibNode.previousSibling != null){
		sibNode = sibNode.previousSibling;
		if (getAttrVal(sibNode,'class') == className){return sibNode;}
	}
	return null;
}

function getNextSibling(obj, className){
	var sibNode
	
	sibNode = obj;
	while (sibNode.nextSibling != null){
		sibNode = sibNode.nextSibling;
		if (getAttrVal(sibNode,'class') == className){return sibNode;}
	}
	return null;
}


/*------------------------------------------
manipulate attributes.  the 'Ex' functions
are a way of storing 'attributes' that are
not DHTML attributes; 'Ex' attributes are
defined as any child 'SPAN' element with
class='outlineAttribute', title=attribName,
and the innerHTML content being the value.
-------------------------------------------*/

function getAttrVal(obj,aname){
	try{
	if (obj.attributes){
		if (isNav6) {if (obj.attributes.getNamedItem(aname)){
			return (obj.attributes.getNamedItem(aname).nodeValue);
		}}
		if (isIE4) {if (obj.attributes.item(aname)){if (obj.attributes.item(aname).nodeValue){
			return (obj.attributes.item(aname).nodeValue);
		}}}
	}}
	catch(er){
	}
	return('unknown');
}

function setAttrVal(obj,aname,aval){
	if (obj.attributes){
		if (isNav6){
			return(obj.setAttribute(aname,aval));
		}
		if (isIE4){
			
			try{obj.attributes.item(aname).nodeValue = aval;}
			catch (er){}
		}
	}
	return (false);
}


function getAttrValEx(obj,aname){
	for (i = 0; i < obj.childNodes.length; i++){
		curNode = obj.childNodes.item(i)
		if (curNode.nodeName == 'SPAN'){
			if (getAttrVal(curNode, 'class') == 'outlineAttribute'){
				if (getAttrVal(curNode, 'title') == aname) return curNode.innerHTML;
	}}}
	return ('unknown');
}

function setAttrValEx(obj,aname,aval){
	for (i = 0; i < obj.childNodes.length; i++){
		curNode = obj.childNodes.item(i)
		if (curNode.nodeName == 'SPAN'){
			if (getAttrVal(curNode, 'class') == 'outlineAttribute'){
				if (getAttrVal(curNode, 'title') == aname){
					if (aval == null){
						curNode.parentNode.removeChild(curNode);
						return true;
					}else{
						curNode.innerHTML = aval;
						return true;
					}
	}}}}
	if (aval == null) return true;
	// node does not exist -- create it now
	var oNode, pNode, newNode, peerNode
	
	oNode = document.createElement('span');
	peerNode = obj.childNodes.item(0);
	pNode = obj;
	newNode = pNode.insertBefore(oNode,peerNode);
	if (newNode){
		setAttrVal(newNode,'class','outlineAttribute');
		setAttrVal(newNode,'title',aname);
		newNode.innerHTML = aval;
		return true;
	}
	return false;
}


/*------------------------------------------
get information about an event and mask
browser differences to caller.
-------------------------------------------*/


function getTarget(evnt){
	if (isIE4){obj = evnt.srcElement;}
	else{
		obj = evnt.target;
		while (obj.nodeName != 'DIV' && obj.nodeName != 'SPAN' && obj.parentNode){obj = obj.parentNode;}
	}
	return (obj);
}

function getButton(evnt){
	if (isIE4) {return ((evnt.button == 1)?'left':'right');}
	else{return ((evnt.which == 1)?'left':'right');}
}


/*------------------------------------------
screen refresh functions to compensate for
a very peculiar IE bug (and an even stranger
Netscape 6 bug!)
-------------------------------------------*/
function refreshScreen(condition){
	if (condition){
		if (contextNode){
			scTopPrevious = ((isNav6)?window.scrollY:window.screenTop);
		}

		squeeze();
		window.setTimeout('relax()',5);
	}
	return true;
}

function squeeze(){
	var curNode
	
	curNode = document.getElementById('outlineRoot');
	curNode.style.visibility = 'hidden';
	curNode.style.marginLeft = '10%';
	curNode.style.marginRight = '10%';
	
	if (isNav6){
		document.getElementsByTagName('body').item(0).style.visibility = 'hidden';
		curNode.style.display = 'none';
	}
}

function relax(){
	var curNode, scLeft, scTop
	
	curNode = document.getElementById('outlineRoot');
	if (isNav6) {
		curNode.style.display = 'block';
		document.getElementsByTagName('body').item(0).style.visibility = '';
	}
		
	curNode.style.marginLeft = '';
	curNode.style.marginRight = '';
	curNode.style.visibility = '';
	
	if (contextNode){
		scLeft = ((isNav6)?window.scrollX:window.screenLeft);
		scTop = ((isNav6)?window.scrollY:window.screenTop);
		if (scTop != scTopPrevious) window.scrollTo(scLeft,scTopPrevious);
	}
}


/*------------------------------------------
     routines for round-trip back to OPML
-------------------------------------------*/

function toggleOPML(){
	if (inOPMLView){
		document.getElementById('opmlForm').style.display = 'none';
		document.getElementById('opmlText').value = '';
		document.getElementById('outlineRoot').style.display = 'block';
		inOPMLView = false;
	} else {
		document.getElementById('outlineRoot').style.display = 'none';
		document.getElementById('opmlForm').style.display = 'block';
		document.getElementById('opmlText').value = genOPML();
		inOPMLView = true;
	}
}

function genOPML(){
	// generates OPML from the current document
	var nRoot, nCurrent, i, strOPML
	window.status = 'saving OPML structure...';
	strOPML = '<?xml version="1.0" encoding="ISO-8859-1"?>' + crlf;
	strOPML = strOPML + '<opml version="1.0">' + crlf;
	strOPML = strOPML + tab + '<head>' + crlf;
	nRoot = document.getElementById('outlineRoot');
	for (i = 0; i < nRoot.childNodes.length; i++){
		nCurrent = nRoot.childNodes.item(i);
		if (getAttrVal(nCurrent,'class') == 'outlineAttribute'){
			strAttrDecl = '<' + getAttrVal(nCurrent,'title') + '>' + xmlEncode(htmlToISO(nCurrent.innerHTML)) + '</' + getAttrVal(nCurrent,'title') + '>';
			strOPML = strOPML + tab + tab + strAttrDecl + crlf;
		}
	}
	strOPML = strOPML + tab + '</head>' + crlf;
	strOPML = strOPML + tab + '<body>' + crlf;
	for (i = 0; i < nRoot.childNodes.length; i++){
		nCurrent = nRoot.childNodes.item(i);
		if (getAttrVal(nCurrent,'class') == 'outline'){
			strOPML = strOPML + genSubOPML(nCurrent,String(tab + tab));
		}
	}
	strOPML = strOPML + tab + '</body>' + crlf;
	strOPML = strOPML + '</opml>' + crlf;
	window.status = '';
	return strOPML;
}

function genSubOPML(nRoot,prepend){
	var strSubOPML, nCurrent, i
	strSubOPML = prepend + '<outline text="' + xmlEncode(htmlToISO(getOutlineText(nRoot).innerHTML)) + '"';
	for (i = 0; i < nRoot.childNodes.length; i++){
		nCurrent = nRoot.childNodes.item(i);
		if (getAttrVal(nCurrent,'class') == 'outlineAttribute'){
			strAttrDecl = ' ' + getAttrVal(nCurrent,'title') + '="' + xmlEncode(htmlToISO(nCurrent.innerHTML)) + '"';
			strSubOPML = strSubOPML + strAttrDecl;
		}
	}
	var bChildren = false;
	for (i = 0; i < nRoot.childNodes.length; i++){
		nCurrent = nRoot.childNodes.item(i);
		if (getAttrVal(nCurrent,'class') == 'outline'){
			if (!bChildren){
				bChildren = true;
				strSubOPML = strSubOPML + '>' + crlf;
			}
		
			strSubOPML = strSubOPML + genSubOPML(nCurrent,String(tab + prepend));
		}
	}
	if (bChildren){strSubOPML = strSubOPML + tab + prepend + '</outline>' + crlf;}
	else {strSubOPML = strSubOPML + '/>' + crlf;}
	return strSubOPML;	
}

/*------------------------------------------
              initialization
-------------------------------------------*/

function initUI(){
	// inserts any extra UI stuff you want available
	var el = document.createElement('FORM');
	el.style.display = 'none';
	setAttrVal(el,'id','opmlForm');
	setAttrVal(el,'method','post');
	el.innerHTML = '<textarea id="opmlText" rows="30" cols="70" wrap="off"></textarea><br /><input type="submit" value="post" />';
	document.body.insertBefore(el,null);
}

/*------------------------------------------
load event handlers
-------------------------------------------*/
var isNav6, isIE4, isIE55, contextNode, inEdit, scTopPrevious
var inOPMLView = false;

if (parseInt(navigator.appVersion.charAt(0)) >= 4) {
   isNav6 = (navigator.appName == "Netscape");
   isIE4 = (navigator.appName.indexOf("Microsoft") > -1);
}
isIE55 = (isIE4 && ((navigator.appVersion.indexOf("5.5") > -1) || (navigator.appVersion.indexOf("6") > -1)));

if (isNav6) {
	document.captureEvents(Event.MOUSEDOWN);
	document.captureEvents(Event.MOUSEOVER);
	document.captureEvents(Event.MOUSEOUT);
	document.captureEvents(Event.KEYPRESS);
}
document.onmousedown = somethingWasClicked;
document.onmouseover = mouseOver;
document.onmouseout = mouseOut;
document.onkeyup = keyStruck;
window.onload = initUI;

var crlf = "\r\n";
var tab = "\t";


/*-------------------------------
   Encoding functions
--------------------------------*/
function xmlEncode(strng){
	return String(strng).replace(/\&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/\"/g,'&quot;').replace(/\'/g,'&apos;');
}

function htmlToISO(strEncoded){
	// converts html entity-encoded string to plain ISO
	var strOut = String(strEncoded);
	var b = strOut.lastIndexOf('&');
	while (b > -1){
		var e = ((b < (strOut.length - 1))?strOut.indexOf(';',b + 1):-1);
		if (e > b){
			var strEntity = strOut.substr(b + 1,e-b-1);
			var nCode = oEntityDictionary.lookup(strEntity);
			if (nCode){
				var re = new RegExp('\&' + strEntity + '\;','g');
				strOut = strOut.replace(re,String.fromCharCode(nCode));
		}}
		b = ((b > 0)?strOut.lastIndexOf('&', b - 1) : -1);
	}
	return strOut;
}


/*--------------------------------
  Create a Dictionary and load
---------------------------------*/
function cDictionary() {
  this.add = m_add;
  this.lookup = m_lookup;
  this.remove = m_remove;
}

function m_lookup(strKeyName) {
  return(this[strKeyName]);
}

function m_add() {
  this[m_add.arguments[0]] = m_add.arguments[1];
}

function m_remove(strKeyName) {
  this[m_remove.arguments[0]] = null;
}

var oEntityDictionary = new cDictionary();

oEntityDictionary.add('quot',34);
oEntityDictionary.add('amp',38);
oEntityDictionary.add('apos',39);
oEntityDictionary.add('lt',60);
oEntityDictionary.add('gt',62);
oEntityDictionary.add('nbsp',160);
oEntityDictionary.add('iexcl',161);
oEntityDictionary.add('cent',162);
oEntityDictionary.add('pound',163);
oEntityDictionary.add('curren',164);
oEntityDictionary.add('yen',165);
oEntityDictionary.add('brvbar',166);
oEntityDictionary.add('sect',167);
oEntityDictionary.add('uml',168);
oEntityDictionary.add('copy',169);
oEntityDictionary.add('ordf',170);
oEntityDictionary.add('laquo',171);
oEntityDictionary.add('not',172);
oEntityDictionary.add('shy',173);
oEntityDictionary.add('reg',174);
oEntityDictionary.add('macr',175);
oEntityDictionary.add('deg',176);
oEntityDictionary.add('plusmn',177);
oEntityDictionary.add('sup2',178);
oEntityDictionary.add('sup3',179);
oEntityDictionary.add('acute',180);
oEntityDictionary.add('micro',181);
oEntityDictionary.add('para',182);
oEntityDictionary.add('middot',183);
oEntityDictionary.add('cedil',184);
oEntityDictionary.add('sup1',185);
oEntityDictionary.add('ordm',186);
oEntityDictionary.add('raquo',187);
oEntityDictionary.add('frac14',188);
oEntityDictionary.add('frac12',189);
oEntityDictionary.add('frac34',190);
oEntityDictionary.add('iquest',191);
oEntityDictionary.add('Agrave',192);
oEntityDictionary.add('Aacute',193);
oEntityDictionary.add('Acirc',194);
oEntityDictionary.add('Atilde',195);
oEntityDictionary.add('Auml',196);
oEntityDictionary.add('Aring',197);
oEntityDictionary.add('AElig',198);
oEntityDictionary.add('Ccedil',199);
oEntityDictionary.add('Egrave',200);
oEntityDictionary.add('Eacute',201);
oEntityDictionary.add('Ecirc',202); 
oEntityDictionary.add('Euml',203);
oEntityDictionary.add('Igrave',204);
oEntityDictionary.add('Iacute',205);
oEntityDictionary.add('Icirc',206);
oEntityDictionary.add('Iuml',207);
oEntityDictionary.add('ETH',208);
oEntityDictionary.add('Ntilde',209);
oEntityDictionary.add('Ograve',210);
oEntityDictionary.add('Oacute',211);
oEntityDictionary.add('Ocirc',212);
oEntityDictionary.add('Otilde',213);
oEntityDictionary.add('Ouml',214);
oEntityDictionary.add('times',215);
oEntityDictionary.add('Oslash',216);
oEntityDictionary.add('Ugrave',217);
oEntityDictionary.add('Uacute',218);
oEntityDictionary.add('Ucirc',219);
oEntityDictionary.add('Uuml',220);
oEntityDictionary.add('Yacute',221);
oEntityDictionary.add('THORN',222);
oEntityDictionary.add('szlig',223);
oEntityDictionary.add('agrave',224);
oEntityDictionary.add('aacute',225);
oEntityDictionary.add('acirc',226);
oEntityDictionary.add('atilde',227);
oEntityDictionary.add('auml',228);
oEntityDictionary.add('aring',229);
oEntityDictionary.add('aelig',230);
oEntityDictionary.add('ccedil',231);
oEntityDictionary.add('egrave',232);
oEntityDictionary.add('eacute',233);
oEntityDictionary.add('ecirc',234);
oEntityDictionary.add('euml',235);
oEntityDictionary.add('igrave',236);
oEntityDictionary.add('iacute',237);
oEntityDictionary.add('icirc',238);
oEntityDictionary.add('iuml',239);
oEntityDictionary.add('eth',240);
oEntityDictionary.add('ntilde',241);
oEntityDictionary.add('ograve',242);
oEntityDictionary.add('oacute',243);
oEntityDictionary.add('ocirc',244);
oEntityDictionary.add('otilde',245);
oEntityDictionary.add('ouml',246);
oEntityDictionary.add('divide',247);
oEntityDictionary.add('oslash',248);
oEntityDictionary.add('ugrave',249);
oEntityDictionary.add('uacute',250);
oEntityDictionary.add('ucirc',251);
oEntityDictionary.add('uuml',252);
oEntityDictionary.add('yacute',253);
oEntityDictionary.add('thorn',254);
oEntityDictionary.add('yuml',255);
