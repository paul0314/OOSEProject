import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class Load_Controller {
    //FXML-Datei Objekte
    @FXML TextField load_file_textfield;
    @FXML JFXButton load_file_button;
    @FXML JFXButton rule_load_button;
    @FXML TextField rule_load_textfield;

    //main-Controller wird über die init-Methode übergeben
    private Window_Controller main;

    //SimpleStringProperty damit später Listener definiert werden können
    private final SimpleStringProperty load_name = new SimpleStringProperty();
    private final SimpleStringProperty load_rules_name = new SimpleStringProperty();

    //SimpleStringProperty load_name ändern und load-Methode des MainControllers aufrufen
    public void load(MouseEvent mouseEvent) throws IOException {
        load_name.set(load_file_textfield.getText());
        main.load(main.graphics);
    }

    //Methoden auf denen Listener definiert werden im Window-Controller
    public SimpleStringProperty load_name_Property(){
        return load_name;
    }
    public SimpleStringProperty load_rules_name_Property(){
        return load_rules_name;
    }

    //Window_Controller wird definiert (für Interaktion zwischen den Controllern)
    public void init(Window_Controller mainController){
        main = mainController;
        load_name.set("default");
        load_rules_name.set("default");
    }

    //SimpleStringProperty load_rules_name ändern und load_rules-Methode des MainControllers aufrufen
    public void load_rules(MouseEvent mouseEvent) throws IOException {
        load_rules_name.set(rule_load_textfield.getText());
        main.load_rules();
    }
}
