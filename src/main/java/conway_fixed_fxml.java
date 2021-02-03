import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//Main-Klasse
public class conway_fixed_fxml extends Application {
    //launch-Methode
    public static void main(String[] args){
        launch();
    }

    //Start-Methode
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Laden des Main-Controllers + FXML-Datei
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("window.fxml"));
        //Laden in das zugrundeliegende Objekt (BorderPane)
        BorderPane load = fxmlloader.load();
        //Scene laden, Stage setzen und zeigen
        Scene scene = new Scene(load, 795, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
