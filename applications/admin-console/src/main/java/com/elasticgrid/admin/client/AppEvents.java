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
package com.elasticgrid.admin.client;

import com.extjs.gxt.ui.client.event.EventType;

public class AppEvents {

    public static final EventType INIT = new EventType();

    public static final EventType LOGIN = new EventType();

    public static final EventType ERROR = new EventType();

    public static final EventType NAV_CLUSTERS = new EventType();
    public static final EventType NAV_STORAGE = new EventType();

    public static final EventType VIEW_CLUSTER = new EventType();
    public static final EventType VIEW_NODES = new EventType();
    public static final EventType VIEW_SERVICES = new EventType();
    public static final EventType VIEW_WATCH = new EventType();
    public static final EventType VIEW_STORAGE_ENGINE = new EventType();

    public static final EventType UPDATE_WATCH = new EventType();

    public static final EventType START_CLUSTER = new EventType();
    public static final EventType STOP_CLUSTER = new EventType();

    public static final EventType START_NODE = new EventType();
    public static final EventType STOP_NODE = new EventType();

    public static final EventType DEPLOY_APPLICATION = new EventType();

}
