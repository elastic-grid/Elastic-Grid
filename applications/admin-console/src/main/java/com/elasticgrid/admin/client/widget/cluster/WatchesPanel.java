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
package com.elasticgrid.admin.client.widget.cluster;

import com.allen_sauer.gwt.log.client.Log;
import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.model.Node;
import com.elasticgrid.admin.model.Service;
import com.elasticgrid.admin.model.Watch;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Container used in order to select the watch which should be displayed. Each selected watch may actually have more
 * than 1 graph panel used, depending on the number of nodes/services selected.
 *
 * @author Jerome Bernard
 */
public class WatchesPanel extends ContentPanel {
    private ComboBox<Watch> cbWatches;
    private ListStore<Watch> watchesStore;
    private SortedMap<Object, GraphPanel> graphPanels = new TreeMap<Object, GraphPanel>();

    public WatchesPanel() {
        setLayout(new FillLayout(Orientation.VERTICAL));
        setHeaderVisible(false);
        setBorders(false);

        watchesStore = new ListStore<Watch>();
        cbWatches = new ComboBox<Watch>();
        cbWatches.setEditable(false);
        cbWatches.setForceSelection(true);
        cbWatches.setWidth(100);
        cbWatches.setDisplayField("name");
        cbWatches.setStore(watchesStore);

        ToolBar toolBar = new ToolBar();
        toolBar.add(new LabelToolItem("Display metric: "));
        toolBar.add(cbWatches);
        toolBar.setAlignment(HorizontalAlignment.RIGHT);
        setTopComponent(toolBar);

        layout();
    }

    public void onWatchesChange(final Type type, List<Watch> watches) {
        if (watches == null || watches.size() == 0)
            return;
        populateWatchesList(watches);
        cbWatches.removeAllListeners();
        cbWatches.addSelectionChangedListener(new SelectionChangedListener<Watch>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<Watch> se) {
                Watch watch = se.getSelectedItem();
                String watchName = watch.getName();
                AppEvent ae = new AppEvent(AppEvents.VIEW_WATCH, watchName);
                Set keys = graphPanels.keySet();
                switch (type) {
                    case NODE:
                        ae.setData("nodes", keys);
                    case SERVICE:
                        ae.setData("services", keys);
                }
                Dispatcher.get().dispatch(ae);
            }
        });
        if (watches.size() > 0)
            cbWatches.setSelection(Arrays.asList(watches.get(0)));
    }

    public void onWatchesDataUpdate(Map<?, Watch> watches) {
        if (watches == null || watches.size() == 0) {
            Log.warn("Can't display null watch!");
            if (watches != null && watches.size() == 0)
                Log.warn("Got a non-null list but empty!");
            return;
        }
        for (Map.Entry<?, Watch> entry : watches.entrySet()) {
            Object key = entry.getKey();
            Watch watch = entry.getValue();
            GraphPanel panel = graphPanels.get(key);
            panel.updateChart(watch);
        }
    }

    private void populateWatchesList(List<Watch> watches) {
        watchesStore.removeAll();
        for (Watch w : watches)
            watchesStore.add(w);
    }

    public void addGraphPanelForNode(Node node) {
        GraphPanel graphPanel = new GraphPanel("Statistics for node " + node.getName());
        graphPanels.put(node, graphPanel);
        add(graphPanel);
    }

    public void addGraphPanelForService(Service service) {
        GraphPanel graphPanel = new GraphPanel("Statistics for service " + service.getName());
        graphPanels.put(service, graphPanel);
        add(graphPanel);
    }

    public void removeAllGraphPanels() {
        graphPanels.clear();
        removeAll();
    }

    public static enum Type {
        NODE, SERVICE
    }
}
