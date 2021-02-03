import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class Save_Controller {
    //FXML-Datei Objekte
    @FXML TextField save_file_textfield;
    @FXML JFXButton save_file_button;
    @FXML TextField rule_save_textfield;
    @FXML JFXButton rule_save_button;

    //main-Controller wird über die init-Methode übergeben
    private Window_Controller main;

    //SimpleStringProperty damit später Listener definiert werden können
    private final SimpleStringProperty save_name = new SimpleStringProperty();
    private final SimpleStringProperty save_rule_name = new SimpleStringProperty();

    //Methoden auf denen Listener definiert werden im Window-Controller
    public SimpleStringProperty save_name_Property(){
        return save_name;
    }
    public SimpleStringProperty save_rule_name_Property(){
        return save_rule_name;
    }

    //SimpleStringProperty save_name ändern und save-Methode des MainControllers aufrufen
    public void save(MouseEvent mouseEvent) throws IOException {
        save_name.set(save_file_textfield.getText());
        main.save();
    }

    //Window_Controller wird definiert (für Interaktion zwischen den Controllern)
    public void init(Window_Controller window_controller) {
        main = window_controller;
        save_name.set("default");
    }

    //SimpleStringProperty save_rule_name ändern und save_rules-Methode des MainControllers aufrufen
    public void save_rules(MouseEvent mouseEvent) throws IOException {
        save_rule_name.set(rule_save_textfield.getText());
        main.save_rules();
    }
}
