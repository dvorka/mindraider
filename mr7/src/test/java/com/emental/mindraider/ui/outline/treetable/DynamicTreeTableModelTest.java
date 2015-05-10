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
package com.emental.mindraider.ui.outline.treetable;


import java.util.Date;

import javax.swing.tree.TreeNode;
import javax.swing.treetable.DynamicTreeTableModel;
import javax.swing.treetable.TreeTableModel;

import com.emental.mindraider.test.MRBaseTest;



public class DynamicTreeTableModelTest extends MRBaseTest
{

    private static final TreeNode root = null;

    private static final String[] columnNames = {"Name", "Location", "Last Visited", "Created"};

    private static final String[] getterMethodNames = {"getName", "getLocation", "getLastVisited", "getCreated"};

    private static final String[] setterMethodNames = {"setName", "setLocation", "setLastVisited", "setCreated"};

    private static final Class[] classes = {TreeTableModel.class, String.class, Date.class, Date.class};

    DynamicTreeTableModel dynamicTreeTableNode;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        dynamicTreeTableNode = new DynamicTreeTableModel(
            root,
            columnNames,
            getterMethodNames,
            setterMethodNames,
            classes);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (dynamicTreeTableNode != null)
        {
            dynamicTreeTableNode = null;
        }
    }

    /**
     * The getColumnClass test.
     *
     */
    public void testGetColumnClass()
    {
        int columnCount = dynamicTreeTableNode.getColumnCount();
        for(int i = 0; i< columnCount; i++)
        {
            assertEquals("column class is not as expected", classes[i], dynamicTreeTableNode.getColumnClass(i));
        }
    }

    /**
     * The getColumnCount test case.
     */
    public void testGetColumnCount()
    {
        assertEquals("column count is not as expected", columnNames.length, dynamicTreeTableNode.getColumnCount());
    }


    /**
     * The getColumnName test for each column.
     */
    public void testGetColumnName()
    {

        int columnCount = dynamicTreeTableNode.getColumnCount();
        for (int i = 0; i < columnCount; i++)
        {
            assertEquals("column name is not as expected", columnNames[i], dynamicTreeTableNode.getColumnName(i));
        }
    }

    /**
     * The getRoot test case.
     */
    public void testGetRoot()
    {
        assertEquals("root node not as expected", root, dynamicTreeTableNode.getRoot());
    }
}
