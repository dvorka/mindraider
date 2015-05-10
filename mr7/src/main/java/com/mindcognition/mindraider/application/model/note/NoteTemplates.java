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
package com.mindcognition.mindraider.application.model.note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mindcognition.mindraider.utils.Utils;

public class NoteTemplates {
    
    public static final String TEMPLATE_NOTE_NONE="None";
    public static final String TEMPLATE_NOTE_MEETING="Meeting";
    public static final String TEMPLATE_NOTE_BRIEFING="Briefing";
    public static final String TEMPLATE_NOTE_REPORT="Progress Report";

    public static final String TEMPLATE_NOTE_COACH_ACHIEVEMENT="GROW Model Coaching: Achievement";
    public static final String TEMPLATE_NOTE_COACH_GOAL="GROW Model Coaching: Goal";
    public static final String TEMPLATE_NOTE_COACH_REALITY="GROW Model Coaching: Reality";
    public static final String TEMPLATE_NOTE_COACH_OPTIONS="GROW Model Coaching: Options";
    public static final String TEMPLATE_NOTE_COACH_WILL="GROW Model Coaching: Will";
    
    public NoteTemplates() {
    }

    public String getTitleForTemplateLabel(String template, String defaultTitle) {
        String resultTitle=defaultTitle;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MMM/dd ");
        
        if(TEMPLATE_NOTE_MEETING.equals(template) ||
                TEMPLATE_NOTE_REPORT.equals(template) ||
                    TEMPLATE_NOTE_BRIEFING.equals(template)) {
            resultTitle=simpleDateFormat.format(new Date())+defaultTitle;
        }
        
        return resultTitle;
    }
    
    public String getAnnotationForTemplateLabel(String template, String defaultAnnotation) {
        Map<String, String> templateToFile=new HashMap<String, String>();
        templateToFile.put(TEMPLATE_NOTE_BRIEFING, "template-briefing.txt");
        templateToFile.put(TEMPLATE_NOTE_MEETING, "template-meeting.txt");
        templateToFile.put(TEMPLATE_NOTE_REPORT, "template-report.txt");
        templateToFile.put(TEMPLATE_NOTE_COACH_ACHIEVEMENT, "template-coach-achievement.txt");
        templateToFile.put(TEMPLATE_NOTE_COACH_GOAL, "template-coach-goal.txt");
        templateToFile.put(TEMPLATE_NOTE_COACH_REALITY, "template-coach-reality.txt");
        templateToFile.put(TEMPLATE_NOTE_COACH_OPTIONS, "template-coach-options.txt");
        templateToFile.put(TEMPLATE_NOTE_COACH_WILL, "template-coach-will.txt");
        
        String file=templateToFile.get(template);
        if(file!=null) {
            return Utils.inputStreamToString(NoteTemplates.class.getResourceAsStream(file));
        }
        
        return defaultAnnotation;
    }    
}
