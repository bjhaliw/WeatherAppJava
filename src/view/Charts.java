package view;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.WeatherGridInformation;
import model.WeatherInformation;
import model.WeatherPeriod;
import model.WeatherValues;

public class Charts {
	WeatherInformation weather;

	public Charts(WeatherInformation weather) {
		this.weather = weather;
	}

	/**
	 * Creates a chart associated with the WeatherPeriod object
	 * 
	 * @param list - A list of WeatherPeriods
	 * @return - ArrayList containing AreaCharts
	 */
	protected static ArrayList<AreaChart<String, Number>> createPeriodAreaCharts(ArrayList<WeatherPeriod> list) {
		ArrayList<AreaChart<String, Number>> chartList = new ArrayList<>();

		for (int j = 0; j < 2; j++) {
			final CategoryAxis xAxis = new CategoryAxis();
			final NumberAxis yAxis = new NumberAxis();
			AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);
			yAxis.setVisible(false);
			yAxis.setTickLabelsVisible(false);
			chart.getStylesheets().add("Chart.css");
			XYChart.Series<String, Number> series = new XYChart.Series<>();

			double maxValue = Double.MIN_VALUE;
			double minValue = Double.MAX_VALUE;
			DateTimeFormatter df = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
			String am_pm = "";
			for (int i = 0; i < list.size() && i < 24; i += 3) {
				WeatherPeriod currWP = list.get(i);

				String time = currWP.getStartTime();
				System.out.println(time);

				LocalDateTime date = LocalDateTime.from(df.parse(time));
				
				int hours = date.getHour();
				if (hours > 12) {
					hours -= 12;
					am_pm = "pm";
				} else if (hours == 12) {
					am_pm = "pm";
				} else if (hours == 0) {
					hours = 12;
					am_pm = "am";
				} else {
					am_pm = "am";
				}

				int val;
				if (j == 0) {
					val = Integer.parseInt(currWP.getTemperature());
				} else {
					String wind = currWP.getWindSpeed();
					wind = wind.substring(0, wind.indexOf(" "));
					val = Integer.parseInt(wind);
				}
				series.getData().add(new XYChart.Data<>(hours + " " + am_pm, val));

				if (val > maxValue) {
					maxValue = val;
				}

				if (val < minValue) {
					minValue = val;
				}

				//System.out.println("Max Value: " + maxValue + ", Min Value: " + minValue);
			}

			for (Data<String, Number> entry : series.getData()) {
				entry.nodeProperty().addListener(e -> {
					displayLabelForData(entry);
				});
			}

			//System.out.println("Final Max Value: " + maxValue + ", Min Value: " + minValue);
			if (maxValue != Double.MIN_VALUE && minValue != Double.MAX_VALUE) {
				yAxis.setAutoRanging(false);
				yAxis.setUpperBound(maxValue + 2);
				yAxis.setLowerBound(minValue - 2);
				yAxis.setTickUnit(1);
			}

			chart.getData().addAll(series);
			chart.setPrefHeight(245);
			chart.setLegendVisible(false);

			chartList.add(chart);
		}

		return chartList;
	}

	protected static AreaChart<String, Number> createAreaChart(ArrayList<WeatherValues> list, LocalTime currentTime) {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);

		// Clear the Y axis
		yAxis.setVisible(false);
		yAxis.setTickLabelsVisible(false);
		chart.getStylesheets().add("Chart.css");
		chart.setLegendVisible(false);

		XYChart.Series<String, Number> series = new XYChart.Series<>();

		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;
		String am_pm = "";
		boolean breakOut = false;
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		for (int i = 0; i < list.size() && i < 24; i += 3) {
			if (breakOut) {
				break;
			}

			WeatherValues currWV = list.get(i);

			// Getting the current value time
			String time = currWV.getValidTime();
			time = time.replace("T", " ");
			LocalDateTime date = LocalDateTime.from(df.parse(time));
			int hours = date.getHour();

			if (hours > 12) {
				hours -= 12;
				am_pm = "pm";
			} else if (hours == 12) {
				am_pm = "pm";
			} else if (hours == 0) {
				hours = 12;
				am_pm = "am";
			} else {
				am_pm = "am";
			}

			series.getData().add(new XYChart.Data<>(hours + " " + am_pm, (int) currWV.getValue()));
			double val = currWV.getValue();
			if (val > maxValue) {
				maxValue = val;
			}

			if (val < minValue) {
				minValue = val;
			}
		}

		for (Data<String, Number> entry : series.getData()) {
			entry.nodeProperty().addListener(e -> {
				displayLabelForData(entry);
			});
		}

		if (maxValue != Double.MIN_VALUE && minValue != Double.MAX_VALUE) {
			yAxis.setAutoRanging(false);
			yAxis.setUpperBound(maxValue + 5);
			yAxis.setLowerBound(minValue - 2);
			yAxis.setTickUnit(1);
		}

		chart.getData().addAll(series);

		return chart;
	}

	/**
	 * Creates text to be displayed above each point to easily show the value
	 * 
	 * @param data - The current point on the chart
	 */
	private static void displayLabelForData(XYChart.Data<String, Number> data) {
		final Node node = data.getNode();
		final Text dataText = new Text(data.getYValue() + "");
		dataText.setFill(Color.WHITE);
		node.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
				Group parentGroup = (Group) parent;
				parentGroup.getChildren().add(dataText);
			}
		});

		node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
				dataText.setLayoutX(Math.round(bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2));
				dataText.setLayoutY(Math.round(bounds.getMinY() - dataText.prefHeight(-1) * 0.5));
			}
		});
	}

}
