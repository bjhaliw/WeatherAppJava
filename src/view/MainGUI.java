package view;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.WeatherGridInformation;
import model.WeatherInformation;
import model.WeatherPeriod;
import model.WeatherValues;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import errors.WeatherJsonError;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class MainGUI extends Application {

	// Holds all of the weather information
	private WeatherInformation weather;

	// The text displayed on screen for the GUI
	private Label precipitation, humidity, windSpeed, highAndLowTemp;
	private Label currentTemp, clock, shortSummary, currLocation;

	// Progress Bar for the user to see the status of info collection
	private ProgressBar progressBar;

	// Shows the user the weather status
	private Image weatherIcon;
	private ImageView weatherIconView;

	// The main GUI's box
	private VBox root;

	// TabPane displaying the different charts
	private TabPane chartPane;

	// HBox displaying the forecasts
	private HBox sevenDayBox;

	// Thread for the Clock animation
	Timeline clockThread;
	LocalTime currentTime;

	// Text size for most of the labels present in the GUI
	private final static int TEXT_SIZE = 14;

	public MainGUI() {
		this.precipitation = setText("Precipitation: ###%", TEXT_SIZE);
		this.humidity = setText("Humidity: ###%", TEXT_SIZE);
		this.windSpeed = setText("Wind Info: N/E/W/S ### mph", TEXT_SIZE);
		this.highAndLowTemp = setText("High: ##F, Low: ##F", TEXT_SIZE);
		this.shortSummary = setText("Short Summary of Weather", TEXT_SIZE);
		this.clock = setText("Clock", TEXT_SIZE + 5);
		this.currentTemp = setText("##F", TEXT_SIZE + 10);
		this.currLocation = setText("Current Location", TEXT_SIZE + 10);
		this.progressBar = new ProgressBar(0);
		this.weatherIcon = new Image("resources/test_gui/sun.png", 100, 100, false, false);
		this.weatherIconView = new ImageView(this.weatherIcon);
		this.root = new VBox(10);
		this.chartPane = new TabPane();
		this.sevenDayBox = new HBox(20);
	}

	@Override
	public void start(Stage primaryStage) {

		this.root.setAlignment(Pos.TOP_CENTER);

		currLocation.setUnderline(true);
		Label hrsLabel = setText("Next 24 Hours", TEXT_SIZE + 5);
		hrsLabel.setUnderline(true);
		Label daysLabel = setText("Extended Forecast", TEXT_SIZE + 5);
		daysLabel.setUnderline(true);

		Label credits = setText("Brenton Haliw  |  github.com/bjhaliw  |  Powered by the NWS and MapQuest", 10);

		this.root.getChildren().addAll(createLocationBox(), new Separator(), currLocation,
				createCurrentWeatherDetails(), new Separator());
		this.root.getChildren().addAll(hrsLabel, createTabPane());
		this.root.getChildren().addAll(daysLabel, createForecastBox(), credits);

		Scene scene = new Scene(root, 500, 990);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Weather App");
		primaryStage.getScene().getRoot().setStyle("-fx-base:black");
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> System.exit(0));
	}

	/**
	 * Creates the top box of the GUI. Contains the TextField for the user to input
	 * the desired location, a Button to start the weather collection process, and a
	 * ProgressBar to let the user know that the process is in action.
	 * 
	 * @return - HBox containing the top part of the GUI
	 */
	private HBox createLocationBox() {
		// Contains location field, go button, progress bar
		HBox locationBox = new HBox(10);
		locationBox.setAlignment(Pos.CENTER);
		locationBox.setPadding(new Insets(10, 0, 0, 0));

		TextField locationField = new TextField();
		locationField.setPromptText("Enter Location");
		Button goButton = new Button("Go!");

		locationField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				String location = locationField.getText();
				if (location != null && !location.equals("")) {
					startThread(locationField);
				}
			}
		});

		goButton.setOnAction(e -> {
			String location = locationField.getText();
			if (location != null && !location.equals("")) {

				startThread(locationField);
			}
		});

		locationBox.getChildren().addAll(locationField, goButton, this.progressBar);

		return locationBox;
	}

	/**
	 * Creates the portion of the GUI containing the current weather details. The
	 * box contains information such as the time, current temperature, the high/low
	 * temperature, wind speed/direction, and other useful information.
	 * 
	 * @return - HBox containing the current weather details
	 */
	private HBox createCurrentWeatherDetails() {
		// Main box for current weather details
		HBox weatherInformationBox = new HBox(40);
		weatherInformationBox.setAlignment(Pos.CENTER);

		// Time, icon and short description
		VBox weatherSummary = new VBox(15);
		weatherSummary.setAlignment(Pos.CENTER);
		weatherSummary.getChildren().addAll(this.clock, this.weatherIconView, this.shortSummary);
		showClock(this.clock, "America/New_York");
		// High/Low temp, Precip chance, humidity, wind speed
		VBox currentWeatherDetails = new VBox(20);
		currentWeatherDetails.setAlignment(Pos.CENTER);
		currentWeatherDetails.getChildren().addAll(this.currentTemp, this.highAndLowTemp, this.precipitation,
				this.humidity, this.windSpeed);

		weatherInformationBox.getChildren().addAll(weatherSummary, currentWeatherDetails);
		return weatherInformationBox;
	}

	/**
	 * Creates a TabPane to display the charts
	 * 
	 * @return
	 */
	private TabPane createTabPane() {
		Tab tempTab = new Tab("Temperature");
		Tab precipTab = new Tab("Precipitation");
		Tab humidityTab = new Tab("Humidity");
		Tab windTab = new Tab("Wind");

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);
		chart.getStylesheets().add("Chart.css");

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		Random rand = new Random();
		for (int i = 0; i < 9; i++) {
			int value = rand.nextInt(5);
			series.getData().add(new XYChart.Data<>("HH:MM" + i, value));
		}

		for (Data<String, Number> entry : series.getData()) {
			entry.nodeProperty().addListener(e -> {
				displayLabelForData(entry);
			});
		}

		yAxis.setVisible(false);
		yAxis.setTickLabelsVisible(false);

		chart.getData().add(series);
		chart.setLegendVisible(false);
		chart.setPrefHeight(400);
		tempTab.setContent(chart);

		this.chartPane.getTabs().addAll(tempTab, precipTab, humidityTab, windTab);
		this.chartPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		this.chartPane.tabMinWidthProperty()
				.bind(this.root.widthProperty().divide(this.chartPane.getTabs().size()).subtract(16));
		this.chartPane.getStyleClass().add("floating");
		return this.chartPane;
	}

	/**
	 * Helper method for the createTabPane() method. Allows for the creation of text
	 * to be displayed above each data point for easy reading
	 * 
	 * @param data - The data point
	 */
	private void displayLabelForData(XYChart.Data<String, Number> data) {
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

	/**
	 * Creates the ScrollPane to show the seven day forecast
	 * 
	 * @return
	 */
	private ScrollPane createForecastBox() {
		ScrollPane pane = new ScrollPane();
		sevenDayBox.setPadding(new Insets(0, 20, 0, 20));
		sevenDayBox.setAlignment(Pos.CENTER);

		for (int i = 0; i < 7; i++) {
			VBox box = new VBox();
			Image icon = new Image("resources/test_gui/sun.png", 50, 50, true, true);
			Label summary = new Label("Clear");
			Label temp = new Label("High: 45F, Low: 22F");
			Label rain = new Label("Precipitation: 45%");
			Label date = new Label("Monday - January 25");
			box.getChildren().addAll(date, new ImageView(icon), summary, temp, rain);
			box.setAlignment(Pos.CENTER);
			box.setSpacing(10);

			if (i != 6) {
				Separator sep = new Separator();
				sep.setOrientation(Orientation.VERTICAL);
				this.sevenDayBox.getChildren().addAll(box, sep);
			} else {
				this.sevenDayBox.getChildren().addAll(box);
			}

		}

		pane.setPannable(true);
		pane.setPrefHeight(200);
		pane.setContent(sevenDayBox);
		pane.setFitToHeight(true);
		pane.setStyle("-fx-background-color: -fx-outer-border, -fx-inner-border, -fx-body-color;"
				+ "-fx-background-insets: 0, 1, 2;" + "-fx-background-radius: 5, 4, 3;");
		return pane;
	}

	/**
	 * Loads the bottom portion of the GUI with forecast boxes. These boxes contain
	 * information for the periods such as the day, icon, temperature, and
	 * precipitation chance.
	 */
	private void loadForecastBox() {
		ArrayList<WeatherPeriod> periods = this.weather.getDetailedPeriods();

		this.sevenDayBox.getChildren().clear();

		for (int i = 0; i < periods.size(); i++) {
			WeatherPeriod currPeriod = periods.get(i);

			VBox box = new VBox();
			Image icon = new Image(currPeriod.getIcon(), 50, 50, true, true);
			Label summary = new Label(currPeriod.getShortForecast());
			Label temp = new Label("Temp: " + currPeriod.getTemperature() + "°F");
			Label rain = new Label("Precipitation : " + getHighestPrecipValue(currPeriod.getStartTime()) + "%");
			Label date = new Label(currPeriod.getName());
			box.getChildren().addAll(date, new ImageView(icon), summary, temp, rain);
			box.setAlignment(Pos.CENTER);
			box.setSpacing(10);
			Tooltip.install(box, new Tooltip(currPeriod.getDetailedForecast()));

			if (i != periods.size() - 1) {
				Separator sep = new Separator();
				sep.setOrientation(Orientation.VERTICAL);
				this.sevenDayBox.getChildren().addAll(box, sep);
			} else {
				this.sevenDayBox.getChildren().addAll(box);
			}
		}
	}

	/**
	 * Helper method for the loadForecastBox() method. Gets the highest
	 * precipitation chance value for the day by looping through each hourly period.
	 * 
	 * @param date - String value representing the date to find the highest
	 *             precipitation chance
	 * @return - String value representing the highest probability of precipitation
	 *         for given date
	 */
	private String getHighestPrecipValue(String date) {
		double curr, max = Double.MIN_VALUE;

		ArrayList<WeatherValues> values = this.weather.getValuesMap().get("probabilityOfPrecipitation").getValues();
		for (int i = 0; i < values.size(); i++) {
			String validTime = values.get(i).getValidTime();
			if (validTime.substring(0, validTime.indexOf("T")).equals(date.substring(0, date.indexOf("T")))) {
				curr = values.get(i).getValue();
				if (curr > max) {
					max = curr;
				}
			}
		}

		if (max == Double.MIN_VALUE) {
			max = 0;
		}

		return (int) max + "";
	}

	/**
	 * Starts the main weather information collection phase. Creates a Task for the
	 * GUI's ProgressBar to keep track of letting the user know the status of the
	 * collection. Terminates the resulting thread at the end of the weather
	 * collection phase.
	 * 
	 * @param locationField
	 * @param conText
	 * @param tempText
	 * @param lastUpdate
	 * @param location
	 */
	public void startThread(TextField locationField) {

		Task<Parent> updateWeather = new Task<Parent>() {
			@Override
			public Parent call() {

				updateWeatherValues(locationField);

				// method to set progress
				updateProgress(0, 100);

				// method to set labeltext
				updateMessage("All done!");

				return locationField;
			}
		};

		this.progressBar.progressProperty().bind(updateWeather.progressProperty());

		Thread loadingThread = new Thread(updateWeather);
		loadingThread.start();

		updateWeather.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				loadingThread.interrupt();
				System.out.println("Update Weather Finished");

			}
		});

	}

	/**
	 * Handler for the Go button and zipfield textfield. Updates the values with
	 * current information
	 * 
	 * @param view          - ImageView displaying the background image
	 * @param locationField - TextField containing the zipcode
	 * @param conText       - Text containing the current condition
	 * @param tempText      - Text containing the current temperature
	 * @param lastUpdate    - Text containing the date of the information
	 */
	private void updateWeatherValues(TextField locationField) {
		if (locationField.getText() != null && !locationField.getText().equals("")) {
			try {
				this.clockThread.stop();
				this.weather = new WeatherInformation(locationField.getText());

				Platform.runLater(() -> {
					System.out.println(this.weather.getTimeZone());
					showClock(this.clock, this.weather.getTimeZone());

					WeatherInformation.removeOldValues(this.weather.getHourlyPeriods(), currentTime);

					// Updating the top portion of the GUI with current weather information
					this.currLocation.setText(this.weather.getCurrentLocation());
					this.shortSummary.setText(this.weather.getHourlyPeriods().get(0).getShortForecast());
					this.currentTemp.setText(this.weather.getHourlyPeriods().get(0).getTemperature() + "°F");
					this.currLocation.setText(weather.getCurrentLocation());
					this.windSpeed.setText("Wind: " + this.weather.getHourlyPeriods().get(0).getWindDirection() + " "
							+ this.weather.getHourlyPeriods().get(0).getWindSpeed());
					this.weatherIcon = new Image(this.weather.getHourlyPeriods().get(0).getIcon(), 100, 100, true,
							true);
					this.weatherIconView.setImage(weatherIcon);

					ArrayList<WeatherValues> currHumidity = WeatherGridInformation.get24HrValues(
							this.weather.getValuesMap().get("relativeHumidity").getValues(), this.currentTime);
					this.humidity.setText("Humidity: " + (int) currHumidity.get(0).getValue() + "%");

					ArrayList<WeatherValues> precipValues = WeatherGridInformation.get24HrValues(
							this.weather.getValuesMap().get("probabilityOfPrecipitation").getValues(),
							this.currentTime);
					this.precipitation.setText("Precipitation: " + (int) precipValues.get(0).getValue() + "%");

					// Update the high and low temperature for the day
					String high = String.valueOf(
							(int) this.weather.getValuesMap().get("maxTemperature").getValues().get(0).getValue());
					String low = String.valueOf(
							(int) this.weather.getValuesMap().get("minTemperature").getValues().get(0).getValue());

					this.highAndLowTemp.setText("High: " + high + "°F, Low: " + low + "°F");

					// Update the middle area of the GUI with forecast charts
					ArrayList<AreaChart<String, Number>> chartList = Charts
							.createPeriodAreaCharts(this.weather.getHourlyPeriods());

					this.chartPane.getTabs().get(0).setContent(chartList.get(0));
					this.chartPane.getTabs().get(1).setContent(Charts.createAreaChart(precipValues, this.currentTime));
					this.chartPane.getTabs().get(2).setContent(Charts.createAreaChart(currHumidity, this.currentTime));
					this.chartPane.getTabs().get(3).setContent(chartList.get(1));

					// Update the bottom portion of the GUI with the forecast boxes
					loadForecastBox();
				});

			} catch (WeatherJsonError e) {

				Platform.runLater(() -> {
					Alerts.JsonError(e.getMessage());
				});

				e.printStackTrace();
			}

		}
	}

	/**
	 * Creates a new JavaFX Text object to be used in the GUI
	 * 
	 * @param string      - What the Text should say
	 * @param fontSize    - How big the font should be
	 * @param strokeWidth - How wide the text stroke should be
	 * @return - JavaFX Text Object
	 */
	private Label setText(String string, int fontSize) {
		Label text = new Label(string);
		text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, fontSize));
		return text;
	}

	/**
	 * Creates and shows the current time for the user based on their system.
	 * 
	 * @param time - Text object to be manipulated to show the time
	 */
	private void showClock(Label time, String timeZone) {
		currentTime = LocalTime.now(ZoneId.of(timeZone));
		this.clockThread = new Timeline(new KeyFrame(Duration.ZERO, e -> {

			String hr = currentTime.getHour() + "";
			String min = currentTime.getMinute() + "";
			String sec = currentTime.getSecond() + "";
			String am_pm = "";

			if (currentTime.getHour() == 12) {
				am_pm = "PM";
			} else if (currentTime.getHour() == 0) {
				hr = "12";
				am_pm = "AM";
			} else if (currentTime.getHour() > 12) {
				int temp = currentTime.getHour() - 12;
				hr = temp + "";
				am_pm = "PM";
			} else {
				am_pm = "AM";
			}

			if (currentTime.getMinute() < 10) {
				min = "0" + currentTime.getMinute();
			}

			if (currentTime.getSecond() < 10) {
				sec = "0" + currentTime.getSecond();
			}
			time.setText(hr + ":" + min + ":" + sec + " " + am_pm);
		}), new KeyFrame(Duration.seconds(1)));
		this.clockThread.setCycleCount(Animation.INDEFINITE);
		this.clockThread.play();

	}

	/**
	 * Launches the MainGUI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
