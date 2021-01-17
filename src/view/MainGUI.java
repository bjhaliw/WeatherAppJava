package view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Weather;

import java.io.File;

import java.time.LocalTime;
import java.util.Random;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import javafx.scene.paint.Color;

public class MainGUI extends Application {

	Weather weather;
	Text humidity, barometer, dewpoint, windSpeed;
	Text highAndLowTemp;
	BorderPane bpane;

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane pane = new StackPane();

		ImageView view = new ImageView("resources/sun/sunflowers-3640935_1920.jpg");
		VBox topBox = new VBox(10);
		topBox.setAlignment(Pos.TOP_CENTER);
		TextField zipfield = new TextField();
		zipfield.setOpacity(.3);
		zipfield.setPromptText("Enter Zipcode");
		Button goButton = new Button("Go");
		goButton.setOpacity(.5);

		Text time = setText("", 36, .8);
		Text location = setText("Current Location", 24, .8);
		Text conText = setText("Current Conditions", 24, .8);
		Text tempText = setText("Current Temperature", 36, .8);
		Text lastUpdate = setText("Current as of: ", 30, .5);
		this.highAndLowTemp = setText("High and Low Temp", 24, .8);
		this.barometer = setText("Barometer", 24, .8);
		this.windSpeed = setText("Wind Speed", 24, .8);
		this.dewpoint = setText("Dew Point", 24, .8);

		HBox weatherInfo = new HBox(30);
		weatherInfo.setAlignment(Pos.CENTER);

		VBox tempAndCondition = new VBox(10);
		tempAndCondition.setAlignment(Pos.CENTER);
		tempAndCondition.getChildren().addAll(tempText, conText);
		
		VBox timeAndLocation = new VBox(10);
		timeAndLocation.setAlignment(Pos.CENTER);
		timeAndLocation.getChildren().addAll(time, location, lastUpdate);
		
		VBox otherDetails = new VBox(10);
		otherDetails.setAlignment(Pos.CENTER);
		otherDetails.getChildren().addAll(this.highAndLowTemp, windSpeed);
		
		Separator sep1 = new Separator();
		sep1.setOrientation(Orientation.VERTICAL);
		Separator sep2 = new Separator();
		sep2.setOrientation(Orientation.VERTICAL);
		
		weatherInfo.getChildren().addAll(tempAndCondition, sep1, timeAndLocation, sep2, otherDetails);
		
		// System clock
		showClock(time);

		zipfield.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				if (zipfield.getText() != null && !zipfield.getText().equals("")) {
					updateWeatherValues(view, zipfield, conText, tempText, lastUpdate, location);
				}
			}
		});

		goButton.setOnAction(e -> {
			if (zipfield.getText() != null && !zipfield.getText().equals("")) {
				updateWeatherValues(view, zipfield, conText, tempText, lastUpdate, location);
			}
		});
		
		HBox zipBox = new HBox(10);
		zipBox.setAlignment(Pos.CENTER_LEFT);
		zipBox.getChildren().addAll(zipfield, goButton);
		topBox.getChildren().addAll(zipBox, weatherInfo);
		
		this.bpane = new BorderPane();
		this.bpane.setBottom(lastUpdate);
		this.bpane.setTop(topBox);
		BorderPane.setAlignment(lastUpdate, Pos.CENTER);
		BorderPane.setAlignment(topBox, Pos.CENTER);
		pane.getChildren().addAll(view, this.bpane);

		Scene scene = new Scene(pane, 1920, 1000);
		// scene.getStylesheets().add("Main.css");
		primaryStage.setScene(scene);
		primaryStage.setTitle("Weather App");
		primaryStage.show();
	}

	/**
	 * Updates the weather details box with current information
	 */
	private void getWeatherDetails() {
		if (this.weather != null && this.weather.getCurrentWeatherDetail() != null) {
			this.barometer
					.setText("Barometric Pressure: " + this.weather.getCurrentWeatherDetail().get("Barometer"));
			this.windSpeed.setText("Wind Speed: " + this.weather.getCurrentWeatherDetail().get("Wind Speed"));
			this.dewpoint.setText("Dew Point: " + this.weather.getCurrentWeatherDetail().get("Dewpoint"));
		}
	}

	/**
	 * Creates a BarChart displaying the humidity and precipitation chance
	 */
	private void createBarChart() {
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
			String rain = this.weather.getDetailedForecast().get(0);

			if (rain.indexOf("Chance of precipitation is ") != -1) {
				rain = rain.substring(rain.indexOf("Chance of precipitation is "));
				rain = rain.substring("Chance of precipitation is ".length());
				rain = rain.replace("%", "");
				rain = rain.replace(".", "");
				rain = rain.replaceAll("\\D+","");
				Number precip = Double.parseDouble(rain);
				series2.getData().add(new XYChart.Data("Preceipitation", precip));
			} else {
				series2.getData().add(new XYChart.Data("Preceipitation", 0));
			}

			System.out.println(rain);

			bc.getData().addAll(series1, series2);
			bc.lookup(".default-color0.chart-bar").setStyle("-fx-bar-fill: green;");
			bc.lookup(".default-color1.chart-bar").setStyle("-fx-bar-fill: blue;");

			this.bpane.setLeft(bc);

		}

	}

	/**
	 * Handler for the Go button and zipfield textfield. Updates the values with
	 * current information
	 * 
	 * @param view       - ImageView displaying the background image
	 * @param zipfield   - TextField containing the zipcode
	 * @param conText    - Text containing the current condition
	 * @param tempText   - Text containing the current temperature
	 * @param lastUpdate - Text containing the date of the information
	 */
	private void updateWeatherValues(ImageView view, TextField zipfield, Text conText, Text tempText, Text lastUpdate,
			Text location) {
		if (zipfield.getText() != null && !zipfield.getText().equals("")) {
			System.out.println(zipfield.getText());
			this.weather = new Weather(zipfield.getText());
			conText.setText(weather.getCurrentCondition());
			tempText.setText(weather.getCurrentTemp());
			location.setText(weather.getCurrentLocation());
			getWeatherDetails();
			if (this.weather.getCurrentCondition().contains("Cloud")
					|| this.weather.getCurrentCondition().contains("Overcast")) {
				view.setImage(getRandomImage("src/resources/cloud"));
			} else if (this.weather.getCurrentCondition().contains("Sun")
					|| (this.weather.getCurrentCondition().contains("Fair"))) {
				view.setImage(getRandomImage("src/resources/sun"));
			} else if (this.weather.getCurrentCondition().contains("Rain")) {
				view.setImage(getRandomImage("src/resources/rain"));
			} else if (this.weather.getCurrentCondition().contains("Clear")) {
				view.setImage(getRandomImage("src/resources/clear"));
			} else if (this.weather.getCurrentCondition().contains("Wind")) {
				view.setImage(getRandomImage("src/resources/wind"));
			} else if (this.weather.getCurrentCondition().contains("Mist")
					|| this.weather.getCurrentCondition().contains("Fog")) {
				view.setImage(getRandomImage("src/resources/fog"));
			} else if (this.weather.getCurrentCondition().contains("Snow")) {
				view.setImage(getRandomImage("src/resources/snow"));
			}

			String high = "";
			String low = "";
			int counter = 0;
			for (String string : this.weather.getSimpleForecast()) {
				if (counter == 2) {
					break;
				} else if (string.contains("High:")) {
					high = string.substring(string.lastIndexOf("High:"));
					counter++;
				} else if (string.contains("Low:")) {
					low = string.substring(string.lastIndexOf("Low:"));
					counter++;
				}
			}

			this.highAndLowTemp.setText(high + ", " + low);

			createBarChart();
			lastUpdate.setText("Current as of: " + this.weather.getCurrentWeatherDetail().get("Last update"));
		}
	}

	private Text setText(String string, int fontSize, double strokeWidth) {
		Text text = new Text(string);
		text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, fontSize));
		text.setStrokeWidth(strokeWidth);
		text.setStroke(Color.BLACK);
		text.setFill(Color.WHITE);

		return text;
	}

	/**
	 * Pulls a random image from one of the image directories depending on what the
	 * condition is (rainy, sunny, etc.)
	 * 
	 * @param directory - Current condition directory containing images
	 * @return - Image object with the selected condition
	 */
	private Image getRandomImage(String directory) {

		File cloudDir = new File(directory);
		File[] pictures = cloudDir.listFiles();

		for (File file : pictures) {
			System.out.println(file.getAbsolutePath());
		}

		Random random = new Random();
		File selectedPic = pictures[random.nextInt(pictures.length - 1)];
		String s = "file:///" + selectedPic.getAbsolutePath();
		Image image = new Image(s);

		return image;

	}

	/**
	 * Creates and shows the current time for the user based on their system.
	 * 
	 * @param time - Text object to be manipulated to show the time
	 */
	private void showClock(Text time) {
		Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
			LocalTime currentTime = LocalTime.now();
			String min = "" + currentTime.getMinute();
			String sec = "" + currentTime.getSecond();

			if (currentTime.getMinute() < 10) {
				min = "0" + currentTime.getMinute();
			}

			if (currentTime.getSecond() < 10) {
				sec = "0" + currentTime.getSecond();
			}

			time.setText(currentTime.getHour() + ":" + min + ":" + sec);
		}), new KeyFrame(Duration.seconds(1)));
		clock.setCycleCount(Animation.INDEFINITE);
		clock.play();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
