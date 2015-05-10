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
package com.emental.mindraider.test;

import junit.framework.TestCase;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The MindRaider base test case.
 */
public class MRBaseTest extends TestCase
{

    protected Log log = LogFactory.getLog(getClass());

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        log.debug("setUp()");
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        log.debug(getName() + " tearDown()");
    }

    /**
     * It returns the full test name.
     * @see junit.framework.TestCase#getName()
     */
    public String getName()
    {
        return ClassUtils.getShortClassName(this.getClass()) + "::" + super.getName();
    }
}
