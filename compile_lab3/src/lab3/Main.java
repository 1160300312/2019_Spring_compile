package lab3;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	public static void main(String args[]){
		launch(args);
	}

	@Override
	public void start(Stage s) throws Exception {
		try {
			Parent root = FXMLLoader.load(getClass()
			        .getResource("/lab3/GUI.fxml"));
			s.setTitle("compile");
			s.setScene(new Scene(root));
			s.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
