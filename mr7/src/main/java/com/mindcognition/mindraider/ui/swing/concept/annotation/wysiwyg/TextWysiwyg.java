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
package com.mindcognition.mindraider.ui.swing.concept.annotation.wysiwyg;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.dialogs.NoteInterlinkingJDialog;
import com.emental.mindraider.ui.listeners.TextWysiwygHyperlinkListener;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.application.model.note.annotation.NoteInterlinking;
import com.mindcognition.mindraider.commons.representation.twiki.TwikiToHtml;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;

/**
 * Obsoleted text WYSIWYG. It is kept (not deleted) in order to reuse its code in the future.
 */
@Deprecated
public class TextWysiwyg extends JEditorPane implements KeyListener {

    /**
     * HTML tail
     */
    private static String HTML_TAIL = " </body>" + "</html>";

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(TextWysiwyg.class);

    /**
     * Concept JPanel reference.
     */
    ConceptJPanel conceptJPanel;

    /**
     * Cursor blink rate.
     */
    public int blinkRate;

    /**
     * Servial vesion ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public TextWysiwyg(final ConceptJPanel conceptJPanel) {
        super("text/html", "<p align='left'> </p>");

        this.conceptJPanel = conceptJPanel;

        // setup documet - IMPORTANT: created using HTML editor kit
        setDocument(new HTMLEditorKit().createDefaultDocument());

        setSelectionColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getSelectionColor());
        setSelectedTextColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getSelectionTextColor());
        blinkRate = getCaret().getBlinkRate();
        addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent keyEvent) {
            }

            public void keyPressed(KeyEvent keyEvent) {

                /*
                 * edit / show mode
                 */
                
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    disableMe();
                } else {
                    enableMe();
                }

                /*
                 * editation
                 */
                
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                }

                if (keyEvent.getKeyCode() == KeyEvent.VK_S && keyEvent.isControlDown()) {
                    saveAndRefresh(conceptJPanel);
                }

                if ((keyEvent.getKeyCode() == KeyEvent.VK_SLASH) && keyEvent.isControlDown()) {
                    new NoteInterlinkingJDialog(null);
                }

                /*
                 * searching
                 */
                
                if (keyEvent.getKeyCode() == KeyEvent.VK_G && keyEvent.isControlDown()) {
                }

            }

            private void saveAndRefresh(final ConceptJPanel conceptJPanel) {
                conceptJPanel.save();
                OutlineJPanel.getInstance().refresh();
                
                int caretPosition = getCaretPosition();
                setText(TextWysiwyg.text2Html(OutlineJPanel.getInstance().conceptJPanel.getConceptResource().getAnnotation()));
                try {
                    setCaretPosition(caretPosition);
                } catch(Exception e) {
                    // it was in the trimed tail - set the caret on the tail
                    setCaretPosition(getDocument().getLength());
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        addHyperlinkListener(new TextWysiwygHyperlinkListener(this));
        addKeyListener(this);
    }

    /**
     * Get HTML head.
     */
    private static String getHtmlHead() {
        return "<html>" + " <head>" + "   <style type='text/css'>" + "     p {"
                + "         margin-top: 0px;" + "         margin-bottom: 0px;" + "         margin-left: 0px;"
                + "     }" + 
                "     body {" + 
                "         color: #"+Gfx.getColorHexString(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getTextColor())+"; " + 
                "         background: #"+Gfx.getColorHexString(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getBackroundColor())+"; " + 
                "         font-family: monospace; " + 
                "         font-size: small;"
                + "     }" + "   </style>" + " </head>" + " <body>";
    }
    
    /**
     * Enable wysiwyg.
     * 
     */
    public void enableMe() {
        setEditable(true);
        setCaretColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getEnabledCaretColor());
        getCaret().setBlinkRate(blinkRate);
        getCaret().setVisible(true);
        
        setSelectionColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getSelectionColor());
        setSelectedTextColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getSelectionTextColor());
    }

    /**
     * Disable wysywig.
     */
    public void disableMe() {
        setEditable(false);
        setCaretColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getBackroundColor());
        getCaret().setBlinkRate(0);
        setCaretColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getDisabledCaretColor());
        getCaret().setVisible(true);
    }

    /**
     * Get text representation of the annotation to be saved to the concept
     * resource.
     * 
     * @return text annotation.
     */
    public String getAnnotation() {
        logger.debug("=-> getAnnotation");
        try {
            String text = getDocument().getText(0, getDocument().getLength());

            // replace leading unicode &nbsp; with regular 32 spaces
            try {
                text=spacesTo32(text);
            } catch (IOException e) {
                logger.debug("Unable to convert spaces to 32...");
            }
            
            String result = injectLinksToText(text);
            
            // trim white spaces away
            result = result.trim();

            logger.debug("<=- getAnnotation:\n" + result + "\n===");
            return result;
        } catch (BadLocationException e) {
            logger.debug("Unable to transform annotation:", e);
            return "";
        }
    }

    /**
     * Translate spaces to regular one.
     * 
     * @param annotation
     * @return
     * @throws IOException 
     */
    private String spacesTo32(String annotation) throws IOException {
        if (annotation == null) {
            return null;
        }
        else {
            BufferedReader bufferedReader = new BufferedReader(
                    new StringReader(annotation));
            
            // TODO string buffer
            String line;
            String bullets="";
            while ((line = bufferedReader.readLine()) != null) {
                if(line.length()==0) {
                    bullets+="\n";
                } else {
                    for (int j = 0; j < line.length(); j++) {
                        if ((int)line.charAt(j) == 160) {
                            bullets += " ";
                        } else {
                            bullets += line.substring(j)+"\n";
                            break;
                        }
                    }
                }
            }
            return bullets;
        }
    }

    /**
     * Inject hyperlinks to text.
     * 
     * @param text
     *            input text.
     * @return text with injecte links.
     */
    private String injectLinksToText(String text) {
        return injectLinksToText(text, null, -1);
    }

    /**
     * Inject hyperlinks to text.
     * 
     * @param text
     *            annotation.
     * @param mrHyperlink
     *            hyperlink to be included (in MR format
     *            {@link NoteInterlinking}). Can be null.
     * @param offset
     *            where to include hyperlink.
     * @return annotation with injected links.
     */
    private String injectLinksToText(String text, String mrHyperlink, int offset) {
        logger.debug("=-> injectLinksToText");
        // convert links to MR notation
        Iterator iterator = ((HTMLDocument) getDocument()).getIterator(HTML.getTag("a"));
        int lastIndex = 0;
        boolean injected = true;
        if (mrHyperlink != null && offset >= 0) {
            injected = false;
        }
        StringBuffer linked = new StringBuffer();
        do {
            if (iterator.getStartOffset() > 0) {
                // injection: is it between the last position and the next
                // injection?
                if (!injected && offset < iterator.getStartOffset()) {
                    injectLinksToTextAppend(mrHyperlink, text, offset, lastIndex, linked);

                    lastIndex = offset;
                    injected = true;
                }

                // process regular links
                String hrefText = text.substring(iterator.getStartOffset(), iterator.getEndOffset());
                Object hrefAttribute = iterator.getAttributes().getAttribute(HTML.Attribute.HREF);
                if(hrefAttribute==null) {
                    linked.append(text.substring(lastIndex, iterator.getStartOffset()));
                } else {
                    String href = hrefAttribute.toString();
                    logger.debug("  [" + iterator.getStartOffset() + "-" + iterator.getEndOffset() + "]: "
                            + hrefText + " # " + href);
                    if (NoteInterlinking.isMindRaiderLink(href)) {
                        injectLinksToTextAppend("%GREEN%[[" + href + "][" + hrefText + "]]%ENDCOLOR%",
                                text,
                                iterator.getStartOffset(),
                                lastIndex,
                                linked);
                    } else {
                        injectLinksToTextAppend("[[" + href + "][" + hrefText + "]]",
                                text,
                                iterator.getStartOffset(),
                                lastIndex,
                                linked);
                    }
                }

                lastIndex = iterator.getEndOffset();
                iterator.next();
            } else {
                break;
            }
        } while (true);

        // check whether injection is in the last part
        if (!injected) {
            injectLinksToTextAppend(mrHyperlink, text, offset, lastIndex, linked);
            lastIndex = offset;
        }

        linked.append(text.substring(lastIndex));
        String result = linked.toString();
        return result;
    }

    /**
     * Inject MR hyperlink to text.
     * 
     * @param mrHyperlink
     * @param text
     * @param startIndex
     * @param lastIndex
     * @param linked
     */
    private void injectLinksToTextAppend(String mrHyperlink,
            String text,
            int startIndex,
            int lastIndex,
            StringBuffer linked) {
        logger.debug("=-> injectLinksToTextAppend: " + mrHyperlink);

        linked.append(text.substring(lastIndex, startIndex));
        linked.append(" " + mrHyperlink + " ");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.text.JTextComponent#setText(java.lang.String)
     */
    public void setText(String a) {
        super.setText(a);
        setCaretPosition(0);
    }

    /**
     * Inject hyperlink to annotation.
     * 
     * @param mrHyperlink
     */
    public void injectHyperlinkToAnnotation(String mrHyperlink) {
        try {
            // Link injection
            // o link must be placed on the caret position
            // o after that caret must be placed there
            
            // Method
            // o use get annotation method (i.e. that one that
            // produces HTML),
            // it clever version that iterates over links and
            // inserts new link
            // on the proper place - there is prepared HEAD and TAIL
            // buffers
            // and between them will be inserted that link

            String text = getDocument().getText(0, getDocument().getLength());
            final int caretPosition = getCaretPosition();

            // link & htmlize
            String linked = injectLinksToText(text, mrHyperlink, caretPosition);
            linked = linked.trim();
            linked = text2Html(linked);

            // set to UI again
            setText(linked);
            setCaretPosition(caretPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inject timestamp.
     */
    public void injectTimestampToAnnotation() {
        try {
            final int caretPosition = getCaretPosition();

            // link & htmlize
            String linked =
                getDocument().getText(0, caretPosition)+
                new Date(System.currentTimeMillis()).toLocaleString()+
                getDocument().getText(caretPosition,getDocument().getLength()-caretPosition);
            linked = linked.trim();
            linked = text2Html(linked);

            // set to UI again
            setText(linked);
            setCaretPosition(caretPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Translate new lines to BRs.
     * 
     * @param sText
     * @return
     */
    public static String text2Html(String sText) {
        logger.debug("=-> text2Html:\n'"+sText+"'");

        // MR interlinking
        sText = NoteInterlinking.translate(sText);

        // break it to lines
        ArrayList<String> text = new ArrayList<String>(Arrays.asList(sText.split("\n")));
        // here comes additonal processing
        text = TwikiToHtml.linksProcess(text);
        text = TwikiToHtml.variablesProcess(text);
        // text=TwikiToHtml.replaceProcess(text,"[ ]", "&nbsp;");

        // concatenate lines as PARAGRAPHS (BR tag is buggy in Sun implementation and doesn't work)
        StringBuffer buffer = new StringBuffer();
        buffer.append(getHtmlHead());
        String bullets;
        for (int i = 0; i < text.size(); ++i) {
            // colorize todo lines: o ! # x
            String line=htmlColorizeToDoLine(text.get(i));
            
            // replace leading spaces with &nbsp;
            bullets="";
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == ' ') {
                    bullets += "&nbsp;";
                } else {
                    bullets += line.substring(j);
                    break;
                }
            }

            // since document.getText() is buggy an only possible roundtrip is
            // usage of paragraph
            buffer.append("<p align='left'>" + bullets + "</p>\n");
        }
        buffer.append(HTML_TAIL);

        logger.debug("<-= text2html:\n"+buffer.toString());
        
        return buffer.toString();
    }
    
    /**
     * Content based colorization of the annotation - mainly todos:
     * <ul>
     *  <li>o normal
     *  <li>x done
     *  <li>! important
     * <ul>
     * 
     * @param annotation
     * @return
     */
    public static String htmlColorizeToDoLine(String line) {
        if (line == null) {
            return null;
        } else {            
            int idx;
            String prefix;
            
            final int EXTRA_INDENTATION=1; // for twiki it is 3
            if (Pattern.matches("^[ ]+o .*", line)) {
                idx = line.indexOf('o');
                line = line.substring(idx + 1);
                prefix = "";
                for (int i = 0; i < idx * EXTRA_INDENTATION; i++) {
                    prefix += " ";
                }
                line = prefix + "o" + line;
            } else {
                if (Pattern.matches("^[ ]+! .*", line)) {
                    prefix = "";
                    while (line.startsWith(" ")) {
                        prefix += " ";
                        line = line.substring(1);
                    }
                    line = prefix + "! <b><font color='#dd6060'>" + line.substring(1) + "</font></b>";
                } else {
                    if (Pattern.matches("^[ ]+x .*", line)) {
                        idx = line.indexOf('x');
                        line = line.substring(idx + 1);
                        prefix = "";
                        for (int i = 0; i < idx * EXTRA_INDENTATION; i++) {
                            prefix += " ";
                        }
                        line = prefix + "x <i><font color='gray'>" + line + "</font></i>";
                    }
                }
            }
            
            // TODO remove \n - test it
            return line+"\n";
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_F5) {
            // insert timestamp on the place where is the cursor
            injectTimestampToAnnotation();
        }
        
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {
    }
}
