package view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Alerts {

	public static void JsonError(String message) {
		Alert alert = new Alert(AlertType.INFORMATION,
				"An error has occured when trying to read the JSON file for the requested location.\n"
						+ "The error message is as follows:\n" + message,
				ButtonType.OK);
		alert.setTitle("Error Reading JSON File");
		alert.setHeaderText("JSON Error");

		alert.show();
	}

	public static void readTimeOut(String message) {
		Alert alert = new Alert(AlertType.INFORMATION,
				"The Weather Application has failed to read from the required website\n" + "The message is: " + message,
				ButtonType.OK);
		alert.setTitle("Read Process has Timed Out");
		alert.setHeaderText("Timed Out");

		alert.show();
	}

	public static void credits() {
		Alert alert = new Alert(AlertType.INFORMATION,
				"Created by Brenton Haliw\nBrenton.Haliw@gmail.com\nhttps://www.github.com/bjhaliw", ButtonType.OK);
		alert.setTitle("Credits");
		alert.setHeaderText("Thank you for trying me!");

		alert.show();
	}
}
