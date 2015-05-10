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
package com.emental.mindraider.gnowsis;

import com.emental.mindraider.test.MRBaseTest;
import com.mindcognition.mindraider.integration.uriqa.UriqaClient;


/**
 * UriqaConnection test class.
 */
public class UriqaConnectionTest extends MRBaseTest
{
    /**
     * The UriqaConnection object.
     */
    private UriqaClient uriqaConnection;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        uriqaConnection = UriqaClient.session;
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (uriqaConnection != null)
        {
            uriqaConnection = null;
        }
    }

    /**
     * The getResourceRepresentationUrl test case.
     */
    public void testGetResourceRepresentationUrl()
    {
    }

    /**
     * establishConnection test case.
     */
    public void testEstablishConnection()
    {

    }

    /**
     * The disconnect test case.
     *
     */
    public void testDisconnect()
    {
    }
}
