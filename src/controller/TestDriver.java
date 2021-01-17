package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Weather;

public class TestDriver extends Application {

	public static void main(String[] args) {
		launch(args);
				
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Group for the two rectangles
				Group group = new Group();
				
			
				double width = 430.0;
				double percentage = 0.25;
				String s = (percentage * 100) + "%";
				Label label = new Label(s);
				label.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
				label.setTextFill(Color.WHITE);

				// Your background image rectangle
				Rectangle background = new Rectangle(0, 0, width, 30);
				background.setFill(Color.BLUE);
				background.setStroke(Color.rgb(1,1,1,0.88));
				background.setStrokeWidth(0.88);

				// Second rectangle to cover parts of the background
				Rectangle rect = new Rectangle(percentage * width, 0, (1 - percentage) * width, 30);
				rect.setFill(Color.WHITE);
				rect.setStroke(Color.rgb(1,1,1,0.88));
				rect.setStrokeWidth(0.88);

				// Group the two rectangles together
				group.getChildren().add(background);
				group.getChildren().add(rect);
				group.getChildren().add(label);
				
				Scene scene = new Scene(group);
                stage.setScene(scene);
                stage.show();
		
	}
}
