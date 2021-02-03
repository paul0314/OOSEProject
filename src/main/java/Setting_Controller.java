import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class Setting_Controller {
    //Farbauswahl
    @FXML ComboBox<String> color_combobox;
    //Ausgewählte Regeln bestätigen
    @FXML JFXButton confirm_rules_button;

    //b steht für Survives
    //s steht für Born
    @FXML JFXRadioButton s0;
    @FXML JFXRadioButton s1;
    @FXML JFXRadioButton s2;
    @FXML JFXRadioButton s3;
    @FXML JFXRadioButton s4;
    @FXML JFXRadioButton s5;
    @FXML JFXRadioButton s6;
    @FXML JFXRadioButton s7;
    @FXML JFXRadioButton s8;

    @FXML JFXRadioButton b0;
    @FXML JFXRadioButton b1;
    @FXML JFXRadioButton b2;
    @FXML JFXRadioButton b3;
    @FXML JFXRadioButton b4;
    @FXML JFXRadioButton b5;
    @FXML JFXRadioButton b6;
    @FXML JFXRadioButton b7;
    @FXML JFXRadioButton b8;

    //main-Controller wird über die init-Methode übergeben
    private Window_Controller main;

    //Regeln bestätigen durch Drücken des Knopfes
    //Übertragen in die Regeln des Main-Controllers
    public void confirm_rules(){
        main.rules[0][0] = s0.isSelected();
        main.rules[0][1] = s1.isSelected();
        main.rules[0][2] = s2.isSelected();
        main.rules[0][3] = s3.isSelected();
        main.rules[0][4] = s4.isSelected();
        main.rules[0][5] = s5.isSelected();
        main.rules[0][6] = s6.isSelected();
        main.rules[0][7] = s7.isSelected();
        main.rules[0][8] = s8.isSelected();

        main.rules[1][0] = b0.isSelected();
        main.rules[1][1] = b1.isSelected();
        main.rules[1][2] = b2.isSelected();
        main.rules[1][3] = b3.isSelected();
        main.rules[1][4] = b4.isSelected();
        main.rules[1][5] = b5.isSelected();
        main.rules[1][6] = b6.isSelected();
        main.rules[1][7] = b7.isSelected();
        main.rules[1][8] = b8.isSelected();
    }

    //Window_Controller wird definiert (für Interaktion zwischen den Controllern)
    public void init(Window_Controller mainController){
        main = mainController;
    }

    //Entsprechend der gesetzten Regeln die Buttons setzen
    public void set_selected(){
        s0.setSelected(main.rules[0][0]);
        s1.setSelected(main.rules[0][1]);
        s2.setSelected(main.rules[0][2]);
        s3.setSelected(main.rules[0][3]);
        s4.setSelected(main.rules[0][4]);
        s5.setSelected(main.rules[0][5]);
        s6.setSelected(main.rules[0][6]);
        s7.setSelected(main.rules[0][7]);
        s8.setSelected(main.rules[0][8]);

        b0.setSelected(main.rules[1][0]);
        b1.setSelected(main.rules[1][1]);
        b2.setSelected(main.rules[1][2]);
        b3.setSelected(main.rules[1][3]);
        b4.setSelected(main.rules[1][4]);
        b5.setSelected(main.rules[1][5]);
        b6.setSelected(main.rules[1][6]);
        b7.setSelected(main.rules[1][7]);
        b8.setSelected(main.rules[1][8]);
    }
}
