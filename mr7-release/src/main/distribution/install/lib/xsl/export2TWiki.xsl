<?xml version="1.0"?>
<<!--
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
                version="1.0">

    <xsl:output method="text"/>

    <xsl:template match="/">
        <xsl:text></xsl:text>
        <xsl:apply-templates select="resource/data/conceptTreeProperty">
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="resource/data/conceptTreeProperty">
      <xsl:text>---+ </xsl:text>
      <xsl:value-of select="./resource/data/labelProperty/text()"/>
      <xsl:text>
      
*Table of Contents*
%TOC%

</xsl:text>

        <xsl:apply-templates select="conceptTreeProperty">
          <xsl:with-param name="SECTION" select="'---++'"/>
        </xsl:apply-templates>
    </xsl:template>



    <xsl:template match="conceptTreeProperty">
      <xsl:param name="SECTION"/>
<xsl:value-of select="$SECTION"/>
<xsl:text> </xsl:text>
      <xsl:value-of select="./resource/data/labelProperty/text()"/>
      <xsl:text>
</xsl:text>
      <xsl:value-of select="./resource/data/annotationProperty/text()"/>

      <xsl:apply-templates select="./conceptTreeProperty">
        <xsl:with-param name="SECTION" select="concat($SECTION,'+')"/>
      </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>
