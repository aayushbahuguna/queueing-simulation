package queuingSimulation;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineChart extends ApplicationFrame {
    public LineChart(double[] X, double[] Y, String title, String xAxis, String yAxis) {
        super(title);
        final XYDataset dataset = createDataset(X, Y, yAxis);
        final JFreeChart chart = createChart(dataset, title, xAxis, yAxis);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }
    
    private XYDataset createDataset(double[] X, double[] Y, String yAxis) {    
    	XYSeries series = new XYSeries(yAxis);
        for(int i = 0;i < X.length; i++){
        	series.add(X[i], Y[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
    
    private JFreeChart createChart(XYDataset dataset, String title, String xAxis, String yAxis) {
        final JFreeChart chart = ChartFactory.createXYLineChart(
            title,      // chart title
            xAxis,                      // x axis label
            yAxis,                      // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

    
        chart.setBackgroundPaint(Color.white);
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                
        return chart;
    }
    
   /* public static void main(final String[] args) {
    	double[] X = new double[10];
    	double[] Y = new double[10];
    	for(int i = 0; i < 10; i++){
    		X[i] = i;
    		Y[i] = i;
    	}
        final LineChart demo = new LineChart(X, Y, "Queue Length", "Time", "Queue Length");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}