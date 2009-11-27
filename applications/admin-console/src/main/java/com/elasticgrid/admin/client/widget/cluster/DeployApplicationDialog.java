/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
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

import com.elasticgrid.admin.client.AppEvents;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;

public class DeployApplicationDialog extends Dialog {
    protected TextField<String> clusterName;
    protected FileUploadField oar;
    protected Button reset;
    protected Button deploy;
    protected Status status;

    public DeployApplicationDialog() {
        FormLayout layout = new FormLayout();
        layout.setLabelWidth(150);
        layout.setDefaultWidth(200);
        setLayout(layout);

        setButtonAlign(HorizontalAlignment.LEFT);
        setButtons("");
        setIcon(IconHelper.createStyle("application"));
        setHeading("Deploy application...");
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

        oar = new FileUploadField();
        oar.setFieldLabel("Application to deploy");
        oar.setAllowBlank(false);
        add(oar);

        setFocusWidget(clusterName);
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

        deploy = new Button("Deploy");
        deploy.disable();
        deploy.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                onSubmit();
            }
        });

        addButton(reset);
        addButton(deploy);
    }

    protected void onSubmit() {
        status.show();
        getButtonBar().disable();
        AppEvent ae = new AppEvent(AppEvents.DEPLOY_APPLICATION);
        ae.setData(oar.getFileInput().getSrc());      // TODO: give the filename?
        Dispatcher.forwardEvent(ae);
        hide();
    }

    protected boolean hasValue(TextField<String> field) {
        return field.getValue() != null && field.getValue().length() > 0;
    }

    protected void validate() {
        deploy.setEnabled(hasValue(clusterName));
    }

}