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
package com.emental.mindraider.core.kernel;

import java.io.DataInputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.dialogs.ProgressDialogJFrame;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.MindRaiderApplication;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.l10n.Messages;

/**
 * Class that handles all the commands.
 */
public class CommandConnection extends Thread {
    private static final Logger logger = Logger.getLogger(CommandConnection.class);

    /**
     * The command accepted String constant.
     */
    public static final String COMMAND_ACCEPTED = "Command Accepted";

    /**
     * The client Socket.
     */
    protected Socket client;

    /**
     * The DataInputString.
     */
    protected DataInputStream in;

    /**
     * The out PrintStream.
     */
    protected PrintStream out;

    /**
     * Constructor.
     * @param clientSocket the client Socket
     */
    public CommandConnection(Socket clientSocket) {
        setDaemon(true);
        this.client = clientSocket;

        try {
            in = new DataInputStream(client.getInputStream());
            out = new PrintStream(client.getOutputStream());
        }
        catch (Exception e) {
            logger.error(Messages.getString("CommandConnection.unableToHandleCommand"), e);
            try {
                client.close();
            }
            catch (Exception e2) {

                logger.error(Messages.getString("CommandConnection.unableToCloseCommandConnection"), e2);
            }
            return;
        }
        this.start();
    }

    /**
     * Handle itself - reads command, and sends it back as an confirmation.
     */
    public void run() {
        String line;
        String result;

        try {
            while (true) {
                // read line
                line = in.readLine();
                if (line == null) {
                    break;
                }

                result = COMMAND_ACCEPTED;
                out.println(result);

                // execute the command
                if (line != null) {
                    if (line.startsWith(MindRaiderApplication.COMMAND_TWIKI_IMPORT)) {
                        String twikiFile = line.substring(MindRaiderApplication.COMMAND_TWIKI_IMPORT.length());

                        logger.debug(Messages.getString("CommandConnection.commandGoingToImport", twikiFile));
                        File file = new File(twikiFile);
                        logger.debug(Messages
                            .getString("CommandConnection.importingTWikiTopic", file.getAbsolutePath()));

                        OutlineJPanel.getInstance().clear();
                        MindRaider.profile.setActiveOutlineUri(null);
                        MindRaider.profile.deleteActiveModel();

                        ProgressDialogJFrame progressDialogJFrame = new ProgressDialogJFrame(

                        Messages.getString("CommandConnection.twikiImport"), "<html>&nbsp;&nbsp;<b>"
                            + Messages.getString("CommandConnection.processingTopic")
                            + "</b>&nbsp;&nbsp;</html>");
                        try {
                            MindRaider.outlineCustodian.importNotebook(OutlineCustodian.FORMAT_TWIKI, (file != null
                                ? file.getAbsolutePath()
                                : null), progressDialogJFrame);
                        }
                        finally {
                            if (progressDialogJFrame != null) {
                                progressDialogJFrame.dispose();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {

            logger.error(Messages.getString("CommandConnection.unableToDispatchCommand"), e);
        }
        finally {
            try {
                client.close();
            }
            catch (Exception e) {
                logger.error(Messages.getString("CommandConnection.unableToCloseCommandOutput"), e);
            }
        }
    }
}
