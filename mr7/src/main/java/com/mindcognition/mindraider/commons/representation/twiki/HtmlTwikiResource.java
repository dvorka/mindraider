package com.mindcognition.mindraider.commons.representation.twiki;

/**
 * Titre : Twiki translator
 * </p>
 * <p>
 * Description : Projet permettant de traduire la syntaxe twiki en html et html
 * en twiki
 * </p>
 * <p>
 * Copyright : Copyright (c) 2003 Fr�d�ric Luddeni
 * </p>
 * <p>
 * Soci�t� : IutForce
 * </p>
 * 
 * @author Fr�d�ric Luddeni (frederic.luddeni@wanadoo.fr)
 * @version 1.06
 * 
 * <PRE>
 * 
 * Version 1.01: ---------- - Modif: * WIKIWORDCLASS => TWIKI.WIKIWORD en
 * TWIKIWIKIWORD Version 1: ---------- Creation de la classe avec l'attribut
 * WIKIWORDCLASS.
 * 
 * </PRE>
 */
public class HtmlTwikiResource {

	public static String WIKIWORDCLASS = "TWIKIWIKIWORD";

    public static String WIKIWORDSIMPLECLASS = WIKIWORDCLASS+".SIMPLE";

    public static String WIKIWORDWITHLEGENDCLASS = WIKIWORDCLASS+".LEGEND";
}