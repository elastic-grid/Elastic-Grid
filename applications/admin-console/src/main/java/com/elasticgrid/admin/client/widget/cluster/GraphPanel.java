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
import com.elasticgrid.admin.model.Calculable;
import com.elasticgrid.admin.model.Thresholds;
import com.elasticgrid.admin.model.Watch;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.AreaChart;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.charts.client.model.charts.LineChart.LineStyle;
import com.extjs.gxt.charts.client.model.charts.dots.BaseDot;
import com.extjs.gxt.charts.client.model.charts.dots.Dot;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import java.util.List;

public class GraphPanel extends ContentPanel {
    private Chart chart;
    private AreaChart valueChart;
    private LineChart lowThresholdChart, highThresholdChart;
    private LineChart medianChart, stdDevChart;
    private static final int NUMBER_OF_X_STEPS = 10;
    private static final int NUMBER_OF_Y_STEPS = 10;
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yy hh:mm:ss aa");

    public GraphPanel(String title) {
        setTitle(title);
        setLayout(new FitLayout());
        setHeaderVisible(false);
        setBorders(false);
        chart = new Chart("../gxt/chart/open-flash-chart.swf");
        add(chart);
        layout();
    }

    public void updateChart(Watch watch) {
        ChartModel cm = new ChartModel();
        cm.setBackgroundColour("#FCFCFC");

        Legend lgd = new Legend(Position.RIGHT, true);
        lgd.setMargin(10);
        lgd.setPadding(10);
        cm.setLegend(lgd);

        valueChart = new AreaChart();
        lowThresholdChart = new LineChart();
        highThresholdChart = new LineChart();
        lowThresholdChart.setColour("#ff0000");
        highThresholdChart.setColour("#ff0000");
        LineStyle dashedStyle = new LineStyle(3, 3);
        medianChart = new LineChart();
        medianChart.setColour("#A9A9A9");
        medianChart.setLineStyle(dashedStyle);
        medianChart.setTooltip("Median is #val#");
        stdDevChart = new LineChart();
        stdDevChart.setColour("#A9A9A9");
        stdDevChart.setLineStyle(dashedStyle);
        stdDevChart.setTooltip("Standard deviation is #val#");

        XAxis xaxis = new XAxis();
        double steps = Math.round((double) watch.getCalculables().size() / (double) NUMBER_OF_X_STEPS);
        xaxis.setRange(0, watch.getCalculables().size(), steps);
        cm.setXAxis(xaxis);

        // compute the max for the calculables
        double min = 0;
        double max = 0;
        for (Calculable c : watch.getCalculables())
            if (c.getValue() > max)
                max = c.getValue();
        steps = (max - min) / NUMBER_OF_Y_STEPS;

        YAxis yaxis = new YAxis();
        yaxis.setRange(min, max, steps);
        cm.setYAxis(yaxis);

        addCalculables(watch, watch.getCalculables());

        cm.addChartConfig(medianChart);
        cm.addChartConfig(stdDevChart);
        cm.addChartConfig(valueChart);
        cm.addChartConfig(lowThresholdChart);
        cm.addChartConfig(highThresholdChart);

        chart.setChartModel(cm);
    }

    private void addCalculables(Watch watch, List<Calculable> calculables) {
        if (watch == null) {
            Log.warn("Watch is null. Skipping display.");
            return;
        }
        for (Calculable calculable : calculables) {
            BaseDot dot = new Dot(calculable.getValue());
            dot.setTooltip("At " + DATE_FORMAT.format(calculable.getWhen()) + ", " + calculable.getId()
                    + " was at " + Math.floor(calculable.getValue() * 100 * 100) / 100 + "%");
            valueChart.addDots(dot);
            Thresholds thresholds = watch.getThresholds();
            if (thresholds != null) {
                Double lowThreshold = thresholds.getLow();
                Double highThreshold = thresholds.getHigh();
                Dot lowThresholdDot = new Dot(lowThreshold);
                Dot highThresholdDot = new Dot(highThreshold);
                lowThresholdDot.setTooltip("Low threshold set to " + lowThreshold);
                highThresholdDot.setTooltip("High threshold set to " + highThreshold);
                lowThresholdChart.addDots(lowThresholdDot);
                highThresholdChart.addDots(highThresholdDot);
            }
            Dot medianDot = new Dot(watch.getMedian());
            Dot stdDevDot = new Dot(watch.getStandardDeviation());
            medianDot.setTooltip("Median is at "
                    + Math.floor(medianDot.getValue().doubleValue() * 100) / 100);
            stdDevDot.setTooltip("Standard deviation is at "
                    + Math.floor(stdDevDot.getValue().doubleValue() * 100) / 100);
            medianChart.addDots(medianDot);
            stdDevChart.addDots(stdDevDot);
        }
    }

}