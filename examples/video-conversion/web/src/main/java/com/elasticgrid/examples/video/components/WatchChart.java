/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.examples.video.components;

import com.elasticgrid.examples.video.util.ServiceLocator;
import com.sun.jini.config.ConfigUtil;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.Response;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.rioproject.watch.Calculable;
import org.rioproject.watch.WatchDataSource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.List;

public class WatchChart {

    /**
     * list(array) of paired values(label & value): [String,Number,String,Number,...]
     */
    @Parameter(required = true)
    private List<Object> _context;

    @Parameter(required = true)
    private int _width;

    @Parameter(required = true)
    private int _height;

    /**
     * width and height of the popup chart, if omitted, javascript for popup chart is omitted from output
     */
    @Parameter
    private int[] _popup;

    @Inject
    private ComponentResources _resources;

    @Inject
    private TypeCoercer _coercer;

    @SuppressWarnings("unchecked")
    void beginRender(MarkupWriter writer) {
        //add width and height to begining of parameters
        Object[] contextArray = _context == null ? new Object[0] : _context.toArray();
        Object[] params = new Object[contextArray.length + 2];
        System.arraycopy(contextArray, 0, params, 2, contextArray.length);
        params[0] = _width;
        params[1] = _height;

        // generate action link
        Link link = _resources.createActionLink("chart", false, params);
        Element img = writer.element("img", "src", link);

        // add javascript for popup
        if (_popup != null && _popup.length > 1) {
            params[0] = _popup[0];
            params[1] = _popup[1];
            link = _resources.createActionLink("chart", false, params);
            img.attribute("onclick", "window.open('" + link + "','_blank','width=" + (_popup[0] + 24) + ", height=" + (_popup[1] + 24) + "')");
            img.attribute("style", "cursor:pointer");
        }

        _resources.renderInformalParameters(writer);
    }

    void afterRender(MarkupWriter writer) {
        writer.end();
    }

    public StreamResponse onChart(final int width, final int height, Object... rest) throws RemoteException, InterruptedException {
        String serviceID = (String) rest[2];
        String watchID = (String) rest[3];
        System.out.println("Service ID is: " + serviceID + ". Watch ID is: " + watchID);

        List<WatchDataSource> watches = ServiceLocator.getWatchDataSourcesByServiceID(ConfigUtil.createServiceID(serviceID));
        WatchDataSource watch = null;
        for (WatchDataSource w : watches) {
            System.out.println("Testing with " + w.getID());
            if (w.getID().equals(watchID))
                watch = w;
        }
        if (watch == null)
            return null;

        TimeSeries s1 = new TimeSeries(watch.getID(), FixedMillisecond.class);
        for (Calculable calculable : watch.getCalculable())
            s1.add(new FixedMillisecond(calculable.getWhen()), calculable.getValue());

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);

        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                watch.getID() + " Watch", // title
                "Date", // x-axis label
                "Value", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );

        return new StreamResponse() {
            public String getContentType() {
                return "image/png";
            }

            public InputStream getStream() throws IOException {
                BufferedImage image = chart.createBufferedImage(width, height);
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                ChartUtilities.writeBufferedImageAsPNG(byteArray, image);
                return new ByteArrayInputStream(byteArray.toByteArray());
            }

            public void prepareResponse(Response response) {
            }
        };
    }
}