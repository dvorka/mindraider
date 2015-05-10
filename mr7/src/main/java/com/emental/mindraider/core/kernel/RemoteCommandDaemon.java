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

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.mindcognition.mindraider.l10n.Messages;

/**
 * Server accepting remote commands for MindRaider.
 */
public class RemoteCommandDaemon extends Thread {
    private static final Logger logger = Logger.getLogger(RemoteCommandDaemon.class);

    /**
     * The default port constant.
     */
    public final static int DEFAULT_PORT = 8315;

    /**
     * The port.
     */
    protected int port;

    /**
     * The listen server socket.
     */
    protected ServerSocket listenSocket;

    /**
     * Constructor that creates server socket and starts to listen in extra thread.
     * @param port
     */
    public RemoteCommandDaemon() {
        this.port = DEFAULT_PORT;
        this.setDaemon(true);

        try {
            listenSocket = new ServerSocket(port);
        }
        catch (Throwable e) {
            logger.error(Messages.getString("RemoteCommandDaemon.unableToCreateRemoteCommandPort"), e);
            return;
        }

        logger.debug(Messages.getString("RemoteCommandDaemon.commandDaemonListening", port));
        this.start();
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            while (true) {
                Socket clientSocket = listenSocket.accept();
                new CommandConnection(clientSocket);
            }
        }
        catch (Exception e) {
            logger.error(Messages.getString("RemoteCommandDaemon.errorListeningToCommands"), e);
        }
    }
}
