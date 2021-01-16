package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class MainGUI extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane pane = new StackPane();
		
		ImageView view = new ImageView("resources/sunflowers-3640935_1920.jpg");
		HBox box = new HBox(10);
		box.setAlignment(Pos.TOP_CENTER);
		box.getChildren().addAll(new Label("Hello, World!"), new Label("How are you?"));
		pane.getChildren().addAll(view, box);

		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
