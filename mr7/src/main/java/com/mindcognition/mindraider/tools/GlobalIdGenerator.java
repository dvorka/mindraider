/*
 * Created on Nov 18, 2011
 */
package com.mindcognition.mindraider.tools;

import java.util.UUID;

/** 
 * Global/Origin IDs are used by MR and MF to exchange Outlines and Notes. They enable to keep
 * their own ID scheme and identify the same entities. URNs are used for Global/Origin IDs,
 * they are user independent:
 * 
 *   MindRaider:
 *     urn:mindraider.sf.net:outline:[ts]:
 *     urn:mindraider.sf.net:outline:[ts]:note:[ts]
 * 
 *   MindForger
 *     urn:mindforger.com:outline:[GAE ID]:
 *     urn:mindforger.com:outline:[GAE ID]:note:[GAE ID]
 * 
 * Only Global/Origin IDs are stored to Atom and internal IDs are kept within applications. If the
 * ID on the internal entity doesn't exist, it is generated.
 */
public class GlobalIdGenerator {

    public static final String OUTLINE_GLOBAL_ID_PREFIX="urn:mindraider.sf.net:outline:";
    public static final String NOTE_GLOBAL_ID_SEGMENT=":note:";

    public static String generateOutlineUri() {
        return
                OUTLINE_GLOBAL_ID_PREFIX+
                UUID.randomUUID();                        
    }
    
    public static String generateNoteId(String globalOutlineId) {
        return
                globalOutlineId+
                NOTE_GLOBAL_ID_SEGMENT+
                UUID.randomUUID();                        
    }
    
}
