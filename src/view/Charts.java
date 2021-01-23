package view;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.WeatherInformation;
import model.WeatherValues;

public class Charts {
	WeatherInformation weather;

	public Charts(WeatherInformation weather) {
		this.weather = weather;
	}

	/**
	 * Creates a BarChart displaying the humidity and precipitation chance
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected BarChart<String, Number> createHumidityPrecipitationBarChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis(0, 100, 10);
		yAxis.setTickLabelFill(Color.WHITE);
		xAxis.setTickLabelFill(Color.WHITE);
		yAxis.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		xAxis.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
		final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
		bc.setPrefWidth(100);
		bc.setTitle("Humidity/Precipitation");
		bc.lookup(".chart-title").setStyle(
				"-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);");
		bc.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
		bc.setLegendVisible(false);

		if (this.weather != null && this.weather.getCurrentWeatherDetail() != null) {
			String hum = this.weather.getCurrentWeatherDetail().get("Humidity").replace("%", "");
			Number num = Integer.parseInt(hum);
			XYChart.Series series1 = new XYChart.Series();
			series1.getData().add(new XYChart.Data("Humidity", num));

			XYChart.Series series2 = new XYChart.Series();
			String rain = this.weather.getDetailedPeriods().get(0).getDetailedForecast();

			if (rain.indexOf("Chance of precipitation is ") != -1) {
				rain = rain.substring(rain.indexOf("Chance of precipitation is "));
				rain = rain.substring("Chance of precipitation is ".length());
				rain = rain.replace("%", "");
				rain = rain.replace(".", "");
				rain = rain.replaceAll("\\D+", "");
				Number precip = Double.parseDouble(rain);
				series2.getData().add(new XYChart.Data("Preceipitation", precip));
			} else {
				series2.getData().add(new XYChart.Data("Preceipitation", 0));
			}

			// System.out.println(rain);

			bc.getData().addAll(series1, series2);
			bc.lookup(".default-color0.chart-bar").setStyle("-fx-bar-fill: green;");
			bc.lookup(".default-color1.chart-bar").setStyle("-fx-bar-fill: blue;");
			bc.setVerticalGridLinesVisible(false);
			bc.setHorizontalGridLinesVisible(false);

			return bc;

		}

		return null;
	}

	protected AreaChart<String, Number> createAreaChart(ArrayList<WeatherValues> list, int timeLength) {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);

		XYChart.Series<String, Number> series = new XYChart.Series();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;
		int counter = 0;
		boolean breakOut = false;
		for (int i = 0; i < list.size(); i++) {
			if (breakOut) {
				break;
			}

			WeatherValues currWV = list.get(i);
			System.out.println(currWV.getValue());
			// Getting the duration that the forecast value is good for
			String durationString = currWV.getValidTime().substring(currWV.getValidTime().indexOf("/") + 1);
			Duration dur = Duration.parse(durationString);
			int hours = (int) dur.toHours(); // How many hours the forecast is good for

			// Getting the current value time
			String time = currWV.getValidTime().substring(0, currWV.getValidTime().indexOf("+"));
			time = time.substring(0, time.lastIndexOf(":"));
			time = time.replace("T", " ");
			LocalDateTime date = LocalDateTime.from(df.parse(time));


			// While the forecasted values are good
			while (hours > 0) {
				series.getData().add(new XYChart.Data(date.toString(), currWV.getValue()));
				date = date.plusHours(1);
				hours--;
				counter++;
				if (counter > timeLength) {
					breakOut = true;
					break;
				}
			}
			

			double val = currWV.getValue();
			if (val > maxValue) {
				maxValue = val;
			} 
			
			if (val < minValue) {
				minValue = val;
			}
			
			System.out.println("Max Value: " + maxValue + ", Min Value: " + minValue);
		}
		
		for (Data<String, Number> entry : series.getData()) {
			entry.nodeProperty().addListener(e -> {
				displayLabelForData(entry);
			});
		}


		System.out.println("Final Max Value: " + maxValue + ", Min Value: " + minValue);
		if (maxValue != Double.MIN_VALUE && minValue != Double.MAX_VALUE) {
			yAxis.setAutoRanging(false);
			yAxis.setUpperBound(maxValue + 2);
			yAxis.setLowerBound(minValue - 2);
			yAxis.setTickUnit(1);
		}

		chart.getData().addAll(series);

		chart.setLegendVisible(false);
		chart.getStylesheets().add("AreaChart.css");

		return chart;
	}

	private void displayLabelForData(XYChart.Data<String, Number> data) {
		final Node node = data.getNode();
		final Text dataText = new Text(data.getYValue() + "");
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
