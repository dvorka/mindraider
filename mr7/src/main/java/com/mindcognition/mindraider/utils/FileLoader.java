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
package com.mindcognition.mindraider.utils;

import java.io.File;
import java.io.IOException;

import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Load file to text area.
 */
public class FileLoader extends Thread {
    private static final Logger logger = Logger.getLogger(FileLoader.class);

    JTextArea doc;
    File file;

    public FileLoader(File file, JTextArea doc) {
        this.file = file;
        this.doc = doc;

        setPriority(4);
        setDaemon(true);
    }

    public void run() {
        try {
            doc.setText(FileUtils.readFileToString(file, "UTF-8"));
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("run(): " + e.getMessage());
            }
        }
    }

    public static void loadFile(String url, JTextArea doc) {
        File f = new File(url);
        if (f.exists()) {
            Thread loader = new FileLoader(f, doc);
            loader.start();
        }
    }
}