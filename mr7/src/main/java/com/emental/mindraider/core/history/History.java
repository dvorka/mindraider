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
package com.emental.mindraider.core.history;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodianListener;

/**
 * Handle notebooks history.
 */
public class History implements OutlineCustodianListener {

    /**
     * Logger for this class
     */
    private static final Logger cat = Logger.getLogger(History.class);

    /**
     * Notebooks history.
     */
    private ArrayList<String> notebooksHistory;

    /**
     * Where currently in the history list (reset on add and not on the tail).
     */
    private int location;

    /**
     * Constructor.
     */
    public History() {
        notebooksHistory = new ArrayList<String>();
        try {
            notebooksHistory
                    .add(MindRaider.outlineCustodian
                            .getOutlineUriByLocalName(OutlineCustodian.MR_DOC_NOTEBOOK_DOCUMENTATION_LOCAL_NAME));
        } catch (Exception e) {
            cat.debug("Error: ", e);
        }
        MindRaider.outlineCustodian.subscribe(this);
        location = 0;
    }

    /**
     * Add a notebook uri.
     * 
     * @param notebookUri
     *            the notebook uri.
     */
    public void add(String notebookUri) {
        if (notebookUri == null) {
            return;
        }
        // if not on the tail, remove the tail
        // if(notebooksHistory.size()>0 &&
        // location!=(notebooksHistory.size()-1)) {
        // // clear it behind location
        // for (int i = location+1; i < notebooksHistory.size(); i++) {
        // notebooksHistory.remove(i);
        // }
        // location=notebooksHistory.size()-1;
        // }
        if (notebooksHistory.size() > 0
                && notebookUri.equals(notebooksHistory.get(location))) {
            return;
        }
        notebooksHistory.add(notebookUri);
        location = notebooksHistory.size() - 1;
        cat.debug(" Added " + notebookUri);
        // cat.debug(toString());

        MindRaider.masterToolBar.refreshHistory();
    }

    /**
     * Check if can move back.
     * 
     * @return Returns <code>true</code> if it's possibile, otherwise
     *         <code>false</code>.
     */
    public boolean canMoveBack() {
        return !(location == 0);
    }

    /**
     * Returns the back notebook.
     * 
     * @return the notebok name
     */
    public String back() {
        // cat.debug(toString());
        if (canMoveBack()) {
            location--;
            MindRaider.masterToolBar.refreshHistory();
            return notebooksHistory.get(location);
        }
        return null;
    }

    /**
     * Check if notebook can be moved forward.
     * 
     * @return Returns <code>true</code> if it's possibile, otherwise
     *         <code>false</code>.
     */
    public boolean canMoveForward() {
        return location != (notebooksHistory.size() - 1);
    }

    /**
     * Move forward the notebook and return its name.
     * 
     * @return Returns the name of notebook.
     */
    public String forward() {
        // cat.debug(toString());
        if (canMoveForward()) {
            location++;
            MindRaider.masterToolBar.refreshHistory();
            return notebooksHistory.get(location);
        }
        return null;
    }

    /**
     * @see com.mindcognition.mindraider.application.model.outline.OutlineCustodianListener.NotebookCustodianListener#outlineCreated(com.emental.mindraider.OutlineResource.NotebookResource)
     */
    public void outlineCreated(OutlineResource newNotebook) {
        if (newNotebook != null) {
            add(newNotebook.resource.getMetadata().getUri().toString());
        }
    }

    /**
     * Remove the given uri.
     * 
     * @param uri
     *            the uri to remove.
     */
    public void remove(String uri) {
        if (uri != null) {
            for (int i = 0; i < notebooksHistory.size(); i++) {
                if (uri.equals(notebooksHistory.get(i))) {
                    if (i <= location) {
                        location--;
                    }
                    notebooksHistory.remove(i);
                    if (location < 0) {
                        location = 0;
                    }
                }
            }
            MindRaider.masterToolBar.refreshHistory();
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("\n");
        for (int i = 0; i < notebooksHistory.size(); i++) {
            stringBuffer.append(notebooksHistory.get(i));
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    /**
     * Getter for <code>location</code>.
     * 
     * @return Returns the location.
     */
    public int getLocation() {
        return this.location;
    }

    /**
     * Setter for <code>location</code>.
     * 
     * @param location
     *            The location to set.
     */
    public void setLocation(int location) {
        this.location = location;
    }

    /**
     * Getter for <code>notebooksHistory</code>.
     * 
     * @return Returns the notebooksHistory.
     */
    public ArrayList<String> getNotebooksHistory() {
        return this.notebooksHistory;
    }

    /**
     * Setter for <code>notebooksHistory</code>.
     * 
     * @param notebooksHistory
     *            The notebooksHistory to set.
     */
    public void setNotebooksHistory(ArrayList<String> notebooksHistory) {
        this.notebooksHistory = notebooksHistory;
    }
}
