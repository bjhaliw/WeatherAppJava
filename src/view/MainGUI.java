package view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Weather;

import java.io.File;

import java.time.LocalTime;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class MainGUI extends Application {

	Weather weather;

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane pane = new StackPane();

		ImageView view = new ImageView("resources/sun/sunflowers-3640935_1920.jpg");
		// HBox box = new HBox(10);
		VBox otherBox = new VBox(10);
		otherBox.setAlignment(Pos.TOP_CENTER);
		// box.setAlignment(Pos.TOP_CENTER);
		TextField zipfield = new TextField();
		zipfield.setOpacity(.3);
		zipfield.setPromptText("Enter Zipcode");
		Button goButton = new Button("Go");
		goButton.setOpacity(.5);
		Text conText = new Text("Current Conditions");
		Text tempText = new Text("Current Temperature");
		tempText.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 30));
		conText.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 30));
		tempText.setStrokeWidth(1);
		conText.setStrokeWidth(1);
		tempText.setFill(Color.WHITE);
		conText.setFill(Color.WHITE);
		tempText.setStroke(Color.BLACK);
		conText.setStroke(Color.BLACK);

		goButton.setOnAction(e -> {
			if (zipfield.getText() != null && !zipfield.getText().equals("")) {
				System.out.println(zipfield.getText());
				weather = new Weather(zipfield.getText());
				conText.setText(weather.getCurrentCondition());
				tempText.setText(weather.getCurrentTemp());

				if (this.weather.getCurrentCondition().contains("Cloud")) {
					view.setImage(getRandomImage("src/resources/cloud"));
				} else if (this.weather.getCurrentCondition().contains("Sun")) {
					view.setImage(getRandomImage("src/resources/sun"));
				} else if (this.weather.getCurrentCondition().contains("Rain")) {
					view.setImage(getRandomImage("src/resources/rain"));
				}

			}
		});

		Text time = new Text();
		time.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 30));
		time.setStrokeWidth(1);
		time.setStroke(Color.BLACK);
		time.setFill(Color.WHITE);
		showClock(time);

		// box.getChildren().addAll(zipfield, goButton, conText, tempText, time);
		// pane.getChildren().addAll(view, box);
		HBox zipBox = new HBox(10);
		zipBox.setAlignment(Pos.CENTER_LEFT);
		zipBox.getChildren().addAll(zipfield, goButton);
		otherBox.getChildren().addAll(zipBox, time, conText, tempText);
		pane.getChildren().addAll(view, otherBox);

		Scene scene = new Scene(pane, 1920, 1000);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Weather App");
		primaryStage.show();
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
