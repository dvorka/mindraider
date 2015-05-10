<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!--
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
-->

<xsl:output method = "html" version = "4.0" encoding="ISO-8859-1" indent = "no" />
<xsl:strip-space elements = "*" />

<xsl:variable name="markerNormal">&#9658;</xsl:variable>
<xsl:variable name="markerComment">&#8810;</xsl:variable>
<xsl:variable name="markerLink">&#9788;</xsl:variable>

<xsl:template match = "/opml" >
<html>
  <head>
   <title><xsl:value-of select="head/title" /></title>
   <script language="JavaScript" src="./opml.js" />
   <link rel="stylesheet" href="./opml.css" />
  </head>
  <body>
    <h1>OPML Based Notebook Outline</h1>
    <div id="outlineRoot" class="outlineRoot">
        <xsl:for-each select="head/*" >
        <span class="outlineAttribute" title="{name()}"><xsl:value-of select="." /></span>
        </xsl:for-each>
  	<xsl:apply-templates select="body"/>
    </div>
    <span id="markerNormal" style="display:none"><xsl:value-of select="$markerNormal" /></span>
    <span id="markerComment" style="display:none"><xsl:value-of select="$markerComment" /></span>
    <span id="markerLink" style="display:none"><xsl:value-of select="$markerLink" /></span>
    <br/>
    <div style="margin-top: 5px; font-size: smaller; color: #dddddd">OPML XSL/CSS/JS by <a style="color: #bbbbbb" href="mailto:joshuaa@netcrucible.com">Joshua Allen</a>
    </div>
    <br/>
    <br/>    
  </body>
</html>
</xsl:template>

<xsl:template match = "outline" >
  <div class="outline">
       <xsl:attribute name="style">
         <xsl:if test="parent::outline">display:none</xsl:if>       
       </xsl:attribute>
       <xsl:for-each select="@*[name() !='text']" >
       <span class="outlineAttribute" title="{name()}"><xsl:value-of select="." /></span>
       </xsl:for-each>
       <span>
            <xsl:attribute name="class">
              <xsl:choose>
                <xsl:when test="./*">markerClosed</xsl:when>
                <xsl:when test="contains(@url,'.opml') or contains(@url,'.OPML')">markerClosed</xsl:when>
                <xsl:otherwise>markerOpen</xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
            <xsl:choose>
            	<xsl:when test="@isComment = 'true'"><xsl:value-of select="$markerComment" /></xsl:when>
            	<xsl:when test="@type = 'link' and not(contains(@url,'.opml') or contains(@url,'.OPML'))"><xsl:value-of select="$markerLink" /></xsl:when>
            	<xsl:otherwise><xsl:value-of select="$markerNormal" /></xsl:otherwise>
            </xsl:choose>
       </span>
       <span class="outlineText">
       <xsl:value-of select = "@text" disable-output-escaping = "yes" /></span>
       <xsl:apply-templates />
  </div>
</xsl:template>

</xsl:stylesheet>
