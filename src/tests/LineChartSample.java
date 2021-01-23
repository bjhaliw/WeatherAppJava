package tests;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;


public class LineChartSample extends Application {

    @SuppressWarnings("unchecked")
	@Override public void start(Stage stage) {
    	 stage.setTitle("Line Chart Sample");
         //defining the axes
         final NumberAxis xAxis = new NumberAxis();
         final NumberAxis yAxis = new NumberAxis();
         xAxis.setLabel("Number of Month");
         //creating the chart
         final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

         lineChart.setTitle("Stock Monitoring, 2010");
         //defining a series
         XYChart.Series<Number, Number> series = new XYChart.Series();
         series.setName("My portfolio");
         //populating the series with data
         Random rand = new Random();

         TreeMap<Integer, Integer> data = new TreeMap();
         //Create Chart data
         for (int i = 0; i < 3; i++) {
             data.put(rand.nextInt(51), rand.nextInt(51));
         }
         Set set = data.entrySet();

         Iterator i = set.iterator();
         while (i.hasNext()) {
             Map.Entry me = (Map.Entry) i.next();
             System.out.println(me.getKey() + " - " + me.getValue());
             series.getData().add(new XYChart.Data(me.getKey(), me.getValue()));//Add data to series
         }

         lineChart.getData().add(series);
         lineChart.getStylesheets().add("chart.css");

         //loop through data and add tooltip
         //THIS MUST BE DONE AFTER ADDING THE DATA TO THE CHART!
         for (Data<Number, Number> entry : series.getData()) {                
             System.out.println("Entered!");
             Tooltip t = new Tooltip(entry.getYValue().toString());
             Tooltip.install(entry.getNode(), t);
         }

         Scene scene = new Scene(lineChart, 1200, 1000);

         stage.setScene(scene);
         stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}