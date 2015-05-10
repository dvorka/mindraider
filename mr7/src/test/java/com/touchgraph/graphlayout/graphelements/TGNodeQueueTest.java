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
package com.touchgraph.graphlayout.graphelements;

import com.emental.mindraider.test.MRBaseTest;
import com.touchgraph.graphlayout.Node;

/**
 * TGNodeQueue test case.
 */
public class TGNodeQueueTest extends MRBaseTest
{
    TGNodeQueue queue;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        queue = new TGNodeQueue();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (queue != null)
        {
            queue = null;
        }
    }

    /**
     * contains test case.
     *
     */
    public void testContains()
    {
        Node node = new Node("test");
        queue.push(node);
        assertTrue("queue doesn't contain node", queue.contains(node));
    }

    /**
     * isEmpty test case.
     *
     */
    public void testIsEmpty()
    {
        assertEquals("queue is not empty", true, queue.isEmpty());
    }

    /**
     * push test case.
     *
     */
    public void testPush()
    {
        Node node = new Node("test");
        queue.push(node);
        assertFalse("queue is empty", queue.isEmpty());
    }

    /**
     * pop test case.
     *
     */
    public void testPop()
    {
        Node node = new Node("test");
        queue.push(node);
        node = queue.pop();
        assertNotNull("node is null", node);
        assertEquals("given node is not the expected one", "test", node.getID());
    }


}

