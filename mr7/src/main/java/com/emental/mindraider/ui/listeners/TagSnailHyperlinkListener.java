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
package com.emental.mindraider.ui.listeners;

import java.util.ArrayList;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.search.SearchResultEntry;
import com.emental.mindraider.ui.dialogs.SearchResultsJDialog;
import com.mindcognition.mindraider.application.model.tag.TagCustodian;
import com.mindcognition.mindraider.application.model.tag.TaggedResourceEntry;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.utils.Launcher;

public class TagSnailHyperlinkListener implements HyperlinkListener {

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(TagSnailHyperlinkListener.class);

    /*
     * (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            logger.debug("HyperlinkEvent: "+e.getClass().getName());
            if (e instanceof HyperlinkEvent) {
                logger.debug("It is tag snail hyperlink event: "+e.getURL());
                if(e.getURL()!=null) {
                    String url = e.getURL().toString();
                    if(TagCustodian.ALL_TAGS_URL.equals(url)) {
                        // render root page again
                        MindRaider.tagCustodian.redraw(MindRaider.tagCustodian.getHtml(TagCustodian.HtmlOutput.ALL_TAGS));
                        return;
                    }
                    if(url.startsWith(MindRaiderConstants.MR_OWL_TAG_NS) || url.startsWith(MindRaiderConstants.MR_OWL_FLAG_NS)) {
                        // reload the page for the particular tag
                        
                        // TODO reload removed - for now it makes no sense - associated tags & other fields are not loaded anyway
                        //MindRaider.tagNavigator.redraw(MindRaider.tagNavigator.getHtml(TagNavigator.HtmlOutput.CHOSEN_TAG,url));
                        
                        // this is tag - show tagged resources in find window - build search item
                        TaggedResourceEntry[] taggedResources = MindRaider.tagCustodian.getTaggedResourcesByUri(url);
                        if(taggedResources!=null && taggedResources.length>0) {
                            ArrayList<SearchResultEntry> result=new ArrayList<SearchResultEntry>();
                            for(TaggedResourceEntry entry: taggedResources) {
                                result.add(
                                        new SearchResultEntry(
                                                entry.notebookLabel,
                                                entry.conceptLabel,
                                                entry.conceptPath));
                            }
                            new SearchResultsJDialog(
                                    Messages.getString("TagSnailHyperlinkListener.taggedResources"),
                                    result);
                        }
                        
                        return;
                    }
                    if(url.startsWith("http")) {
                        // not MR resource, but a URL - open it
                        Launcher.launchInBrowser(url);
                        return;
                    }
                }
            }
        }
    }

}
