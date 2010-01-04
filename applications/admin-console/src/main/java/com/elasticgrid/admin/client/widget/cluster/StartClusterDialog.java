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
import com.elasticgrid.admin.model.NodeProfileInfo;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import java.util.Arrays;

public class StartClusterDialog extends Dialog {
    protected TextField<String> clusterName;
    protected FieldSet monitors;
    protected FieldSet agents;
    protected FieldSet monitorsAndAgents;
    protected Button reset;
    protected Button start;
    protected Status status;

    public StartClusterDialog() {
        FormLayout layout = new FormLayout();
        layout.setLabelWidth(150);
        layout.setDefaultWidth(200);
        setLayout(layout);

        setButtonAlign(HorizontalAlignment.LEFT);
        setButtons("");
        setIcon(IconHelper.createStyle("cluster"));
        setHeading("Start a new cluster...");
        setModal(true);
        setBodyBorder(true);
        setBodyStyle("padding: 8px;");
        setWidth(400);
        setResizable(false);

        KeyListener keyListener = new KeyListener() {
            @Override
            public void componentKeyUp(ComponentEvent event) {
                validate();
            }
        };

        clusterName = new TextField<String>();
        clusterName.setFieldLabel("Cluster Name");
        clusterName.addKeyListener(keyListener);
        add(clusterName);

        monitors = buildNodeProfileInfoFieldSet("Monitors", "monitors");
        agents = buildNodeProfileInfoFieldSet("Agents", "agents");
        monitorsAndAgents = buildNodeProfileInfoFieldSet("Monitors and Agents", "monitors-and-agents");

        add(monitors);
        add(agents);
        add(monitorsAndAgents);

        setFocusWidget(clusterName);
    }

    private FieldSet buildNodeProfileInfoFieldSet(String heading, String componentsIdPrefix) {
        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeading(heading);
        FormLayout layout = new FormLayout();
        layout.setLabelWidth(150);
        layout.setDefaultWidth(190);
        fieldSet.setLayout(layout);
        fieldSet.setCollapsible(true);

        SimpleComboBox<String> nodeType = new SimpleComboBox<String>();
        nodeType.setId(componentsIdPrefix + "-node-type");
        nodeType.setFieldLabel("Kind of server to start");
        nodeType.add("Small");
        nodeType.add("Large");
        nodeType.add("Extra Large");
        nodeType.add("Medium High CPU");
        nodeType.add("Extra Large High CPU");
        nodeType.setSimpleValue("Small");
        fieldSet.add(nodeType);

        Slider slider = new Slider();
        slider.setIncrement(1);
        slider.setMinValue(0);
        slider.setMaxValue(20);
        SliderField numberOfNodes = new SliderField(slider);
        numberOfNodes.setId(componentsIdPrefix + "-number-of-nodes");
        numberOfNodes.setFieldLabel("Number of nodes to start");
        fieldSet.add(numberOfNodes);

        CheckBox hasOverride = new CheckBox();
        hasOverride.setId(componentsIdPrefix + "-override");
        hasOverride.setFieldLabel("Has overrides?");
        hasOverride.setToolTip("Configuration overrides are used for highly customized Elastic Grid deployments. " +
                "If you don't know what configuration overrides are, chances are you don't need them!");
        fieldSet.add(hasOverride);

        return fieldSet;
    }

    @Override
    protected void createButtons() {
        super.createButtons();
        status = new Status();
        status.setBusy("please wait...");
        status.hide();
        status.setAutoWidth(true);
        getButtonBar().add(status);

        getButtonBar().add(new FillToolItem());

        reset = new Button("Reset");
        reset.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                clusterName.reset();
                validate();
                clusterName.focus();
            }

        });

        start = new Button("Start");
        start.disable();
        start.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                onSubmit();
            }
        });

        addButton(reset);
        addButton(start);
    }

    protected void onSubmit() {
        status.show();
        getButtonBar().disable();
        Log.info("trace 1: " + ((SimpleComboBox<String>) monitors.getItemByItemId("monitors-node-type")).getValue().getValue());
        Log.info("trace 2: " + ((SliderField) monitors.getItemByItemId("monitors-number-of-nodes")).getValue());
        Log.info("trace 3: " + monitors.getItemByItemId("monitors-override"));
        Log.info("trace 3: " + ((CheckBox) monitors.getItemByItemId("monitors-override")).getValue());
        NodeProfileInfo monitorsNPI = new NodeProfileInfo(
                "Monitor",
                ((SimpleComboBox<String>) monitors.getItemByItemId("monitors-node-type"))
                        .getValue().getValue(),
                ((SliderField) monitors.getItemByItemId("monitors-number-of-nodes"))
                        .getValue(),
                ((CheckBox) monitors.getItemByItemId("monitors-override"))
                        .getValue());
        Log.info("trace 4");
        NodeProfileInfo agentsNPI = new NodeProfileInfo(
                "Agent",
                ((SimpleComboBox<String>) agents.getItemByItemId("agents-node-type"))
                        .getValue().getValue(),
                ((SliderField) agents.getItemByItemId("agents-number-of-nodes"))
                        .getValue(),
                ((CheckBox) agents.getItemByItemId("agents-override"))
                        .getValue());
        Log.info("trace 5");
        NodeProfileInfo monitorsAndAgentsNPI = new NodeProfileInfo(
                "Monitor and Agent",
                ((SimpleComboBox<String>) monitorsAndAgents.getItemByItemId("monitors-and-agents-node-type"))
                        .getValue().getValue(),
                ((SliderField) monitorsAndAgents.getItemByItemId("monitors-and-agents-number-of-nodes"))
                        .getValue(),
                ((CheckBox) monitorsAndAgents.getItemByItemId("monitors-and-agents-override"))
                        .getValue());
        Log.info("trace 6");
        AppEvent ae = new AppEvent(AppEvents.START_CLUSTER);
        Log.info("trace 7");
        ae.setData("clusterName", clusterName.getValue());
        ae.setData("node_profile_infos", Arrays.asList(monitorsNPI, agentsNPI, monitorsAndAgentsNPI));
        Log.info("Found node profiles " + Arrays.asList(monitorsNPI, agentsNPI, monitorsAndAgentsNPI));
        Log.info("trace 8");
        Dispatcher.forwardEvent(ae);
        hide();
    }

    protected boolean hasValue(TextField<String> field) {
        return field.getValue() != null && field.getValue().length() > 0;
    }

    protected void validate() {
        start.setEnabled(hasValue(clusterName));
    }

}