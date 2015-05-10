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
package com.emental.mindraider.core.facet;

import com.emental.mindraider.test.MRBaseTest;


public class FacetCustodianTest extends MRBaseTest
{

    private FacetCustodian facetCustodian;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        facetCustodian = FacetCustodian.getInstance();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (facetCustodian != null)
        {
            facetCustodian = null;
        }
    }

    /**
     * The getFacetLabels test case.
     */
    public void testGetFacetLabels()
    {
        //facetCustodian.getFacetLabels()
    }

    /**
     * The getFacet test case.
     */
    public void testGetFacet()
    {
        //facetCustodian.getFacet(getName())
    }
}
