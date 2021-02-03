import com.jfoenix.controls.JFXButton;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class Game_Setting_Controller {
    @FXML JFXButton reverse_rules_button;
    //Objekte der FXML-Datei
    @FXML VBox vbox_game_setting_pane;
    @FXML JFXButton clear_button;
    @FXML JFXButton reset_button;
    @FXML JFXButton step_button;
    @FXML JFXButton run_button;
    @FXML JFXButton stop_button;
    @FXML JFXButton reverse_button;
    @FXML JFXButton add_delete_button;
    @FXML JFXButton zoom_in_button;
    @FXML JFXButton zoom_out_button;
    @FXML Slider slider_speed;
    @FXML Label infoLabel_speed;
    @FXML Label generation_count;

    //SimpleStringProperty damit später Listener definiert werden können
    private final SimpleDoubleProperty speed_value = new SimpleDoubleProperty();
    private final SimpleIntegerProperty add_delete = new SimpleIntegerProperty();

    //main-Controller wird über die init-Methode übergeben
    private Window_Controller main;

    //Methoden auf denen Listener definiert werden im Window-Controller
    public SimpleDoubleProperty speedProperty(){
        return speed_value;
    }
    public SimpleIntegerProperty add_delete_Property(){
        return add_delete;
    }

    //Wenn der Slider bewegt wird den Wert aktualisieren und den Text
    public void slide(MouseEvent mouseEvent){
        speed_value.set(slider_speed.getValue());
        infoLabel_speed.setText("Step alle " + ((long) slider_speed.getValue()) + "ms");
    }

    //Zoom siehe Window_Controller
    public void zoom_out(MouseEvent mouseEvent) {
        main.zoom_out();
    }

    public void zoom_in(MouseEvent mouseEvent) {
        main.zoom_in();
    }

    //Add-Delete Button wechselt bei jedem Klick die Aufgabe
    public void add_delete(MouseEvent mouseEvent) {
        if(add_delete_button.getText().equals("Add")){
            add_delete_button.setText("Delete");
            add_delete.set(0);
        }
        else{
            add_delete_button.setText("Add");
            add_delete.set(1);
        }
    }

    //Im Folgenden lediglich Aufrufe aus dem Window_Controller
    public void reverse(MouseEvent mouseEvent) {
        main.reverse(main.graphics);
    }

    public void stop(MouseEvent mouseEvent) {
        main.animation.stop();
    }

    public void run(MouseEvent mouseEvent) {
        main.animation.start();
    }

    public void step(MouseEvent mouseEvent) {
        main.tick(main.graphics);
    }

    public void reset(MouseEvent mouseEvent) {
        main.init(main.graphics);
    }

    public void clear(MouseEvent mouseEvent) {
        main.clear(main.graphics);
    }

    public void reverse_rules(MouseEvent mouseEvent) {
        main.reverse_rules();
    }

    //Window_Controller wird definiert (für Interaktion zwischen den Controllern)
    //Default-Werte setzen
    public void init(Window_Controller mainController){
        add_delete.set(1);
        main = mainController;
        speed_value.set(250.0);
    }
}
