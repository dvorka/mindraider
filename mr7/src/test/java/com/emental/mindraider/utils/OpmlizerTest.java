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
package com.emental.mindraider.utils;

import com.emental.mindraider.test.MRBaseTest;
import com.mindcognition.mindraider.export.Opmlizer;



public class OpmlizerTest extends MRBaseTest
{
    Opmlizer opmlizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        opmlizer = Opmlizer.getInstance();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (opmlizer != null)
        {
            opmlizer = null;
        }
    }

    /**
     * transform test case for null annotation.
     */
    public void testTransform1()
    {
        String annotation = null;
        assertEquals("annotation is not null", annotation, opmlizer.transform(annotation));
    }

    /**
     * transform test case for not null annotation.
     */
    public void testTransform2()
    {
        String src = "this is a test";
        String transform = opmlizer.transform(src);
        assertNotNull("transform is null", transform);
        assertEquals("transform is not as expected", (src + "<br/>\n") , transform);
    }

    /**
     * transform test case for not null annotation with blank spaces.
     *
     */
    public void testTransform3()
    {
        String src = "  this is a test";
        String transform = opmlizer.transform(src);
        assertNotNull(transform);

        String expected = "&nbsp;&nbsp;this is a test<br/>\n";
        assertEquals("transform is not as expected", expected, transform);
    }
}
