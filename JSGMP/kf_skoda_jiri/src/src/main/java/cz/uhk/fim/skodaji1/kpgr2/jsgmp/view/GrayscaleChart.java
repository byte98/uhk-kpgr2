/*
 * Copyright (C) 2023 Jiri Skoda <jiri.skoda@uhk.cz>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.view;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.Threadable;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.chart.PieChart;

/**
 * Class which handles displaying grayscale distribution in chart
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class GrayscaleChart implements Threadable
{
    /**
     * Sleep between checking if bitmap has changed (in miliseconds)
     */
    private static final int SLEEP = 500;
    
    /**
     * Counter of created grayscale charts
     */
    private static long counter;
    
    /**
     * Thread which serves checking and computing data for chart
     */
    private final Thread thread;
    
    /**
     * Chart in which data will be displayed
     */
    private PieChart output;
    
    /**
     * Pie chart value for gray pixels
     */
    private final PieChart.Data grayData;
    
    /**
     * Pie chart value for color pixels
     */
    private final PieChart.Data colorData;
    
    /**
     * Source of data
     */
    private final Bitmap source;
    
    /**
     * Flag, whether thread is running
     */
    private boolean running = false;
    
    /**
     * Flag, whether data should be refreshed
     */
    private boolean refresh = false;
    
    /**
     * Creates new handler for displaying gray pixels distribution
     * @param bitmap Source of data
     */
    public GrayscaleChart(Bitmap bitmap)
    {
        this.source = bitmap;
        this.source.addChangeActionListener(new Bitmap.BitmapChangedActionListener()
        {
            @Override
            public void onChange(Bitmap bitmap)
            {
                GrayscaleChart.this.refresh = true;
            }
        });
        this.grayData = new PieChart.Data("Šedé pixely", 50f);
        this.colorData = new PieChart.Data("Barevné pixely", 50f);
        this.thread = new Thread(this, String.format("JSGMP:GrayscaleChart-%d",GrayscaleChart.counter));
        GrayscaleChart.counter++;
    }
    
    /**
     * Sets output for displaying data
     * @param output Pie chart in which data will be displayed
     */
    public void setOutput(PieChart output)
    {
        this.output = output;
        this.output.getData().clear();
        this.output.getData().add(this.grayData);
        this.output.getData().add(this.colorData);
        this.refresh = true;
    }

    @Override
    public void start() {
        this.running = true;
        this.refresh = true;
        this.thread.start();
    }

    @Override
    public void stop() {
        this.running = false;        
        this.thread.stop();
    }

    @Override
    public void run() {
        while (this.running == true)
        {
            if (this.refresh == true)
            {
                this.refresh = false;
                    double pixelCounter = 0;
                    double grayCounter = 0;
                    for (Pixel px: this.source)
                    {
                        pixelCounter++;
                        if (px.getRed() == px.getGreen() && px.getGreen() == px.getBlue())
                        {
                            grayCounter++;
                        }
                    }
                    double grayPct = (grayCounter / pixelCounter) * 100f;
                    double colorPct = 100f - grayPct;
                    this.grayData.setPieValue(grayPct);
                    this.colorData.setPieValue(colorPct);
            }
            try
            {
                Thread.sleep(GrayscaleChart.SLEEP);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(GrayscaleChart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }    
}
