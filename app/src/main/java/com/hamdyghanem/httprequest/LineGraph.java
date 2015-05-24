package com.hamdyghanem.httprequest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by ICHIMARU on 07.05.2015.
 */
public class LineGraph {

    public Intent getIntent(Context context, int[] heat, int[] ph) {


        XYSeries seriesHeat = new XYSeries("Heat");
        for (int i=0; i < heat.length; i++) {
            seriesHeat.add(i+1, heat[i]);
        }
        XYSeries seriesPh = new XYSeries("Ph");
        for (int i=0; i < ph.length; i++) {
            seriesPh.add(i+1, ph[i]);
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(seriesHeat);
        dataset.addSeries(seriesPh);

        XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.setXLabels(0);
        String[] x_axis = new String[]{"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

        for (int i = 0; i < 7; i++) {
            mrenderer.addXTextLabel(i + 1, x_axis[i]);
        }
        //Design
        mrenderer.setShowGrid(true);
        mrenderer.setShowAxes(true);
        mrenderer.setShowGridX(true);
        mrenderer.setShowGridY(true);
        mrenderer.setShowLabels(true);
        mrenderer.setPanEnabled(true, true);
        mrenderer.setFitLegend(true);


        // mrenderer.setYAxisMax(125,5);
        mrenderer.setAxisTitleTextSize(15);
        mrenderer.setZoomButtonsVisible(true);
        mrenderer.setZoomEnabled(true);
        mrenderer.setZoomRate(2);
        mrenderer.setYAxisMax(120);

        mrenderer.setYTitle("Values");
        mrenderer.setXTitle("Date");
        mrenderer.setChartTitle("Graph");
        mrenderer.setXAxisMax(7);




        //Heat
        XYSeriesRenderer rendererHeat = new XYSeriesRenderer();
        rendererHeat.setColor(Color.RED);
        rendererHeat.setPointStyle(PointStyle.SQUARE);
        rendererHeat.setFillPoints(true);
        rendererHeat.setPointStrokeWidth(25);
        rendererHeat.setLineWidth(3);
        //Ph
        XYSeriesRenderer rendererPh = new XYSeriesRenderer();
        rendererPh.setColor(Color.WHITE);
        rendererPh.setPointStyle(PointStyle.CIRCLE);
        rendererPh.setPointStrokeWidth(25);
        rendererPh.setLineWidth(3);
        rendererPh.setFillPoints(true);

        mrenderer.addSeriesRenderer(rendererHeat);
        mrenderer.addSeriesRenderer(rendererPh);

        mrenderer.setChartTitle("Weekly Avarage Heat And Ph ");

        Intent intent = ChartFactory.getLineChartIntent(context,dataset,mrenderer, "Duzce University");

    return intent;
    }



}
