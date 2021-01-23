package view;

import java.io.File;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.util.Random;

import errors.WeatherJsonError;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.WeatherInformation;

public class MainGUITest extends Application {

	WeatherInformation weather;
	GridPane gpane;
	Text humidity, barometer, dewpoint, windSpeed;
	Text highAndLowTemp, windChill, heatIndex, visibility;
	BorderPane bpane;
	final ProgressBar progressBar = new ProgressBar(0);

	@Override
	public void start(Stage primaryStage) throws Exception {

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
		this.humidity = setText("Humidity", 24, .8);
		this.barometer = setText("Barometer", 24, .8);
		this.windSpeed = setText("Wind Speed", 24, .8);
		this.dewpoint = setText("Dew Point", 24, .8);
		this.heatIndex = setText("Heat Index", 24, .8);
		this.windChill = setText("Wind Chill", 24, .8);
		this.visibility = setText("Visibility", 24, .8);

		HBox weatherInfo = new HBox(30);
		weatherInfo.setAlignment(Pos.CENTER);

		VBox tempAndCondition = new VBox(10);
		tempAndCondition.setAlignment(Pos.CENTER);
		tempAndCondition.getChildren().addAll(tempText, conText);

		VBox timeAndLocation = new VBox(10);
		timeAndLocation.setAlignment(Pos.CENTER);
		timeAndLocation.getChildren().addAll(time, location, lastUpdate);

		VBox otherDetails = new VBox(10);
		otherDetails.setAlignment(Pos.CENTER_LEFT);
		otherDetails.getChildren().addAll(this.highAndLowTemp, windSpeed);

		Separator sep1 = new Separator();
		sep1.setOrientation(Orientation.VERTICAL);
		Separator sep2 = new Separator();
		sep2.setOrientation(Orientation.VERTICAL);

		weatherInfo.getChildren().addAll(tempAndCondition, sep1, timeAndLocation, sep2, otherDetails);

		// System clock
		showClock(time);

		this.progressBar.setOpacity(0.5);

		zipfield.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				if (zipfield.getText() != null && !zipfield.getText().equals("")) {
					startThread(zipfield, conText, tempText, lastUpdate, location);
				}
			}
		});

		goButton.setOnAction(e -> {
			if (zipfield.getText() != null && !zipfield.getText().equals("")) {
				startThread(zipfield, conText, tempText, lastUpdate, location);
			}
		});

		HBox zipBox = new HBox(10);
		zipBox.setAlignment(Pos.CENTER_LEFT);
		zipBox.getChildren().addAll(zipfield, goButton, this.progressBar);
		topBox.getChildren().addAll(createMenuBar(), zipBox, weatherInfo);

		this.gpane = new GridPane();
		gpane.setAlignment(Pos.CENTER_RIGHT);
		gpane.setVgap(40);
		gpane.add(this.humidity, 0, 0);
		gpane.add(this.heatIndex, 0, 1);
		gpane.add(this.windChill, 0, 2);
		gpane.add(this.dewpoint, 0, 3);
		gpane.add(this.barometer, 0, 4);
		gpane.add(this.visibility, 0, 5);

		GridPane.setHalignment(this.humidity, HPos.RIGHT);
		GridPane.setHalignment(this.heatIndex, HPos.RIGHT);
		GridPane.setHalignment(this.windChill, HPos.RIGHT);
		GridPane.setHalignment(this.dewpoint, HPos.RIGHT);
		GridPane.setHalignment(this.barometer, HPos.RIGHT);
		GridPane.setHalignment(this.visibility, HPos.RIGHT);

		this.bpane = new BorderPane();
		this.bpane.setBottom(lastUpdate);
		this.bpane.setTop(topBox);
		this.bpane.setRight(gpane);

		BorderPane.setAlignment(lastUpdate, Pos.CENTER);
		BorderPane.setAlignment(topBox, Pos.CENTER);
		BorderPane.setAlignment(gpane, Pos.CENTER_RIGHT);

		Scene scene = new Scene(bpane, 1920, 1000);
		scene.getStylesheets().add("Main.css");
		primaryStage.setScene(scene);
		primaryStage.setTitle("Weather App");
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> System.exit(0));
	}

	/**
	 * Creates the menu bar for the Stage
	 * 
	 * @return a MenuBar object
	 */
	public MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		menuBar.getStylesheets().add("MenuBar.css");

		// Create Menu for File
		Menu file = new Menu("File");
		MenuItem save = new MenuItem("Save");

		MenuItem exit = new MenuItem("Exit");

		// Create Menu for Help
		Menu help = new Menu("Help");
		MenuItem learnMore = new MenuItem("How to use");
		MenuItem about = new MenuItem("About");

		// Load MenuBar with menus
		menuBar.getMenus().addAll(file, help);
		file.getItems().addAll(save, exit);
		help.getItems().addAll(learnMore, about);

		about.setOnAction(e -> {
			credits();
		});

		learnMore.setOnAction(e -> {

		});

		save.setOnAction(e -> {
			System.out.println("Save Button pressed");
		});

		exit.setOnAction(e -> {
			System.exit(0);
		});

		return menuBar;
	}

	/**
	 * Updates the weather details box with current information
	 */
	private void getCurrentWeatherDetails() {
		if (this.weather != null && this.weather.getCurrentWeatherDetail() != null) {
			this.humidity.setText("Humidity: " + this.weather.getCurrentWeatherDetail().get("Humidity"));
			this.barometer.setText("Barometric Pressure: \n" + this.weather.getCurrentWeatherDetail().get("Barometer"));
			this.windSpeed.setText("Wind Speed: " + this.weather.getCurrentWeatherDetail().get("Wind Speed"));
			this.dewpoint.setText("Dew Point: \n" + this.weather.getCurrentWeatherDetail().get("Dewpoint"));
			this.windChill.setText("Wind Chill: \n" + this.weather.getCurrentWeatherDetail().get("Wind Chill"));
			this.heatIndex.setText("Heat Index: \n" + this.weather.getCurrentWeatherDetail().get("Heat Index"));
			this.visibility.setText("Visibility: \n" + this.weather.getCurrentWeatherDetail().get("Visibility"));

			if (this.weather.getCurrentWeatherDetail().get("Wind Chill") == null) {
				windChill.setVisible(false);
			}

			if (this.weather.getCurrentWeatherDetail().get("Heat Index") == null) {
				heatIndex.setVisible(false);
			}
		}
	}

	public void startThread(TextField zipfield, Text conText, Text tempText, Text lastUpdate, Text location) {

		Task<Parent> updateWeather = new Task<Parent>() {
			@Override
			public Parent call() {

				updateWeatherValues(zipfield, conText, tempText, lastUpdate, location);

				// method to set progress
				updateProgress(0, 100);

				// method to set labeltext
				updateMessage("All done!");

				return zipfield;
			}
		};

		this.progressBar.progressProperty().bind(updateWeather.progressProperty());

		Thread loadingThread = new Thread(updateWeather);
		loadingThread.start();

		updateWeather.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				loadingThread.interrupt();
				System.out.println("Finish");

			}
		});

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
	private void updateWeatherValues(TextField zipfield, Text conText, Text tempText, Text lastUpdate, Text location) {
		if (zipfield.getText() != null && !zipfield.getText().equals("")) {
			try {

				this.weather = new WeatherInformation(zipfield.getText());

				this.weather.updateWeather();

				conText.setText(weather.getCurrentCondition());
				tempText.setText(weather.getCurrentTemp());
				location.setText(weather.getCurrentLocation());
				getCurrentWeatherDetails();

				// Needs to be on its own thread or else it won't run
				Platform.runLater(() -> {
					Charts charts = new Charts(this.weather);
					this.bpane.setLeft(charts.createHumidityPrecipitationBarChart());

					TabPane pane = new TabPane();
					Tab hide = new Tab("Hide Content");
					Tab temperature = new Tab("Forecasted Temperatures");
					Tab precip = new Tab("Forecasted Precipitation");
					Tab humidity = new Tab("Forecasted Humidity");

					temperature.setContent(
							charts.createAreaChart(this.weather.getValuesMap().get("temperature").getValues()));
					precip.setContent(charts.createAreaChart(
							this.weather.getValuesMap().get("probabilityOfPrecipitation").getValues()));
					humidity.setContent(
							charts.createAreaChart(this.weather.getValuesMap().get("relativeHumidity").getValues()));
					pane.getTabs().addAll(hide, temperature, precip, humidity);
					pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
					pane.getStylesheets().add("TabPane.css");
					this.bpane.setCenter(pane);
				});

				String high = String
						.valueOf((int) this.weather.getValuesMap().get("maxTemperature").getValues().get(0).getValue());
				String low = String
						.valueOf((int) this.weather.getValuesMap().get("minTemperature").getValues().get(0).getValue());

				this.highAndLowTemp.setText("High: " + high + "°F, Low: " + low + "°F");

				lastUpdate.setText("Current as of: " + this.weather.getCurrentWeatherDetail().get("Last update"));

			} catch (WeatherJsonError e) {

				Platform.runLater(() -> {
					JsonError(e.getMessage());
				});

				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				Platform.runLater(() -> {
					readTimeOut(e.getMessage());
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
	private Text setText(String string, int fontSize, double strokeWidth) {
		Text text = new Text(string);
		text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, fontSize));
		text.setStrokeWidth(strokeWidth);
		text.setStroke(Color.BLACK);
		text.setFill(Color.WHITE);

		return text;
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

	private void JsonError(String message) {
		Alert alert = new Alert(AlertType.INFORMATION,
				"An error has occured when trying to read the JSON file for the requested location.\n"
						+ "The error message is as follows:\n" + message,
				ButtonType.OK);
		alert.setTitle("Error Reading JSON File");
		alert.setHeaderText("JSON Error");

		alert.show();
	}

	private void readTimeOut(String message) {
		Alert alert = new Alert(AlertType.INFORMATION,
				"The Weather Application has failed to read from the required website\n" + "The message is: " + message,
				ButtonType.OK);
		alert.setTitle("Read Process has Timed Out");
		alert.setHeaderText("Timed Out");

		alert.show();
	}

	private void credits() {
		Alert alert = new Alert(AlertType.INFORMATION,
				"Created by Brenton Haliw\nBrenton.Haliw@gmail.com\nhttps://www.github.com/bjhaliw", ButtonType.OK);
		alert.setTitle("Credits");
		alert.setHeaderText("Thank you for trying me!");

		alert.show();
	}
}
