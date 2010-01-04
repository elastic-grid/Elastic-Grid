/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.elasticgrid.admin.client.widget.storage;

import com.elasticgrid.admin.model.StorageEngine;
import com.elasticgrid.admin.model.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import java.util.List;

public class StorageEnginePanel extends ContentPanel {
    private ContainersPanel containersPanel;

    public StorageEnginePanel() {
        setLayout(new FillLayout());
        containersPanel = new ContainersPanel();
        containersPanel.setHeading("Containers");
        containersPanel.setBorders(false);
    }

    public void showStorageEngine(StorageEngine engine) {
        if (engine != null) {
            removeAll();

            List<Container> containers = engine.getContainers();
            if (containers != null)
                containersPanel.getStore().add(containers, true);
            add(containersPanel);

            layout();
        } else {
            removeAll();
            addText("<div style='padding: 12px;font-size: 12px'>Please select a storage engine from the left panel.</div>");
        }
    }
}