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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mindcognition.mindraider.MindRaiderApplication;

/**
 * Class used to start MindRaider using <code>java -jar</code>, from JDistro, etc.
 * 
 * This class must be compiled with JDK 1.4!
 * 
 * @author Martin Dvorak
 */
public class MindRaiderRunner {

    /**
     * Run Mind Raider.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        // warn on different java version
        String javaVersion = System.getProperty("java.version");
        // TODO lazy to parse java version :-/
        if(!javaVersion.startsWith("1.5") && 
                !javaVersion.startsWith("1.6") &&
                    !javaVersion.startsWith("1.7") &&
                        !javaVersion.startsWith("1.8")) {
            JFrame.setDefaultLookAndFeelDecorated(true);

            JFrame frame = new JFrame("Detector");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.pack();
            //frame.setVisible(true);
            
            JOptionPane.showMessageDialog( 
                    frame, 
                    "MindRaider runs on JRE 1.5 or later only!  " +
                    "\n(your current version is "+javaVersion+")", 
                    "Warning: Java Version", 
                    JOptionPane.ERROR_MESSAGE
                );   
            
            frame.dispose();
        } else {
            // start Mind Raider
            MindRaiderApplication.main(args);
        }
    }
}
