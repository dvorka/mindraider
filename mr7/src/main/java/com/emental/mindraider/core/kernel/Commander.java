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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.mindcognition.mindraider.l10n.Messages;

/**
 * Class that is able to send commands to the Remote command daemon.
 */
public class Commander {

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(Commander.class);

    /**
     * Constructor.
     *
     * @param command
     *            the command String
     */
    public Commander(String command) {
        logger.debug(Messages.getString("Commander.goingToInvokeRemoteCommand", command));

        Socket socket = null;

        try {
            socket = new Socket("localhost", RemoteCommandDaemon.DEFAULT_PORT);

            /**
             * @todo remove deprecated BufferedReader replacing code of the form
             *       by: BufferedReader d
             *       =&nbsp;new&nbsp;BufferedReader(new&nbsp;InputStreamReader(in));
             */
            // create streams for reading and writing from the socket
            BufferedReader dataInputStream = new BufferedReader(
                    new InputStreamReader(new DataInputStream(socket
                            .getInputStream())));
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            logger.debug(Messages.getString("Commander.connectedToLocalhost", RemoteCommandDaemon.DEFAULT_PORT));
            logger.debug(Messages.getString("Commander.sendingCommand", command));
            printStream.println(command);
            logger.debug(Messages.getString("Commander.receivingStatus"));
            String result = dataInputStream.readLine();
            logger.debug(result);

        } catch (Exception e) {
            logger.debug(Messages.getString("Commander.mrNotRunning"), e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                logger.error(Messages.getString("Commander.unableToCloseSocket"), e);
            }

            logger.debug(Messages.getString("Commander.done"));
        }
    }
}