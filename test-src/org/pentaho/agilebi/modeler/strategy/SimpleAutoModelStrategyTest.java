/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2011 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.agilebi.modeler.strategy;

import org.junit.Test;
import org.pentaho.agilebi.modeler.AbstractModelerTest;
import org.pentaho.agilebi.modeler.nodes.*;
import org.pentaho.metadata.model.concept.types.DataType;

import java.util.HashMap;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created: 4/19/11
 *
 * @author rfellows
 */
public class SimpleAutoModelStrategyTest extends AbstractModelerTest {

  private static final String LOCALE = "en-US";

  @Override
  public void setUp() throws Exception {
    super.setUp();
    super.generateTestDomain();
  }

  @Test
  public void testAutoModelOlap() throws Exception {
    SimpleAutoModelStrategy strategy = new SimpleAutoModelStrategy(LOCALE);

    strategy.autoModelOlap(workspace, new MainModelNode());

    int columnCount = 0;
    HashSet<String> columns = new HashSet<String>();
    HashSet<String> numericColumns = new HashSet<String>();
    for(AvailableTable table : workspace.getAvailableTables().getAsAvailableTablesList()) {
      columnCount += table.size();
      for (AvailableField field : table.getAvailableFields()) {
        if (!columns.contains(field.getName())) {
          columns.add(field.getName());
        }
        if (field.getPhysicalColumn().getDataType() == DataType.NUMERIC && !numericColumns.contains(field.getName())) {
          numericColumns.add(field.getName());
        }
      }
    }

    assertEquals(columns.size(), workspace.getModel().getDimensions().size());
    assertEquals(numericColumns.size(), workspace.getModel().getMeasures().size());
    
  }

  @Test
  public void testAutoModelRelational() throws Exception {
    SimpleAutoModelStrategy strategy = new SimpleAutoModelStrategy(LOCALE);

    strategy.autoModelRelational(workspace, new RelationalModelNode());

    // one category per table
    // one field per table.column

    HashMap<String, Integer> tables = new HashMap<String, Integer>();
    for(AvailableTable table : workspace.getAvailableTables().getAsAvailableTablesList()) {
      tables.put(table.getName(), table.getAvailableFields().size());
    }

    assertEquals(tables.size(), workspace.getRelationalModel().getCategories().size());
    for(CategoryMetaData cat : workspace.getRelationalModel().getCategories()) {
      assertTrue(tables.containsKey(cat.getName()));
      assertEquals(tables.get(cat.getName()).intValue(), cat.size());
    }

  }


}
