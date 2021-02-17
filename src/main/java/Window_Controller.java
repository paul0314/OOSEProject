import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXButton;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Window_Controller implements Initializable {
    @FXML JFXButton game_setting_button;
    @FXML JFXButton setting_button;
    @FXML JFXButton save_setting_button;
    @FXML JFXButton load_setting_button;
    @FXML JFXButton close_button;
    @FXML Canvas canvas;
    @FXML BorderPane borderpane;
    @FXML VBox empty_vbox;

    /*Definition (und teilweise Initialisierung) von Klassenvariablen*/

    public Color[] color_theme;

    private VBox save_settings;
    private VBox load_settings;
    private VBox setting_settings;
    private VBox game_settings;

    private static final Gson gson = new Gson();

    public AnimationTimer animation;

    public Save_Controller save_controller;
    public Load_Controller load_controller;
    public Setting_Controller setting_controller;
    public Game_Setting_Controller game_setting_controller;

    public GraphicsContext graphics;

    public double sliderspeed;

    public int add_delete;

    /*Definition (und teilweise Initialisierung) von Klassenvariablen*/
    public double cellsize = 10;
    public int reihe = 50;
    public int zeile = 50;
    private final SimpleIntegerProperty generation = new SimpleIntegerProperty(0);
    //2x9 Feld
    public boolean[][] rules;
    private boolean[][] standard_rules;
    public int[][] grid = new int[reihe][zeile];
    public int[][] grid_lifetime = new int[reihe][zeile];
    private final Random random = new Random();
    public String save_name = null;
    public String load_name = null;
    public String load_rules_name = null;
    public String save_rules_name = null;
    public int top_left_x = 0;
    public int top_left_y = 0;
    public int zoom = 50;
    double mouse_pos_x = 0;
    double mouse_pos_y = 0;

    //Draw Methode zur Darstellung des Canvas
    private void draw(GraphicsContext graphics) {
        //"Hintergrund"
        graphics.setFill(Color.LAVENDER);
        graphics.fillRect(0, 0, 500, 500);

        //Iteriere über ganzes Feld und setze die Felder an die richtige Position mit der richtigen Farbe
        for (int i = top_left_x; i < (top_left_x + zoom); i++) {
            for (int j = top_left_y; j < (top_left_y + zoom); j++) {
                if (grid[i][j] == 1) {
                    //Zunächst wird ein größeres graues Quadrat erstellt, die eigentlich Farbe kommt "darein"
                    graphics.setFill(Color.gray(0.5, 0.5));
                    graphics.fillRect((i - top_left_x) * cellsize, (j - top_left_y) * cellsize, cellsize, cellsize);
                    //color_theme gibt das aktuellen Farbschema in Form eines Color-Arrays der Größe 10 an
                    //Hier wird der Inhalt der Zellen gefüllt
                    graphics.setFill(color_theme[9-grid_lifetime[i][j]]);
                    graphics.fillRect(((i - top_left_x) * cellsize) + 1, ((j - top_left_y) * cellsize) + 1, cellsize - 2, cellsize - 2);

                } else {
                    //Falls Zellen tot sind, bleiben sie weiß
                    graphics.setFill(Color.gray(0.5, 0.5));
                    graphics.fillRect((i - top_left_x) * cellsize, (j - top_left_y) * cellsize, cellsize, cellsize);
                    graphics.setFill(Color.WHITE);
                    graphics.fillRect(((i - top_left_x) * cellsize) + 1, ((j - top_left_y) * cellsize) + 1, cellsize - 2, cellsize - 2);

                }
            }
        }
    }

    /*Initialisierung (zufällig)*/
    public void init(GraphicsContext graphics) {
        for (int i = 0; i < reihe; i++) {
            for (int j = 0; j < zeile; j++) {
                grid[i][j] = random.nextInt(2);
                if(grid[i][j] == 1){
                    grid_lifetime[i][j] = 1;
                }
                else{
                    grid_lifetime[i][j] = 0;
                }
            }
        }
        generation.set(0);
        draw(graphics);
    }

    /*Zähle Nachbarn eines Feldes an der Position (i,j)*/
    private int countNeighbors(int i, int j) {
        int sum = 0;

        /*Probleme mit Randfällen ausschließen*/
        int iStart = i == 0 ? 0 : -1;
        int iEnd = i == grid.length - 1 ? 0 : 1;
        int jStart = j == 0 ? 0 : -1;
        int jEnd = j == grid[0].length - 1 ? 0 : 1;

        /*Alle acht Nachbarn durchgehen, solange kein Randfall vorliegt*/
        for (int k = iStart; k <= iEnd; k++) {
            for (int l = jStart; l <= jEnd; l++) {
                sum += grid[i + k][l + j];
            }
        }

        /*Das Feld selbst soll nicht zu den Nachbarn mitgezählt werden*/
        sum -= grid[i][j];

        return sum;
    }

    //Alle Zellen auf 0 setzen und Regeln zurücksetzen
    public void clear(GraphicsContext graphics) {
        for (int i = 0; i < reihe; i++) {
            for (int j = 0; j < zeile; j++) {
                grid[i][j] = 0;
                grid_lifetime[i][j] = 0;
            }
        }
        rules = standard_rules;
        generation.set(0);
        draw(graphics);
    }


    /*Nächste Generation generieren anhand der gegebenen Regeln*/
    public void tick(GraphicsContext graphics) {
        /*Array in dem die nächste Generation berechnet wird*/
        int[][] next = new int[reihe][zeile];
        /*Für jedes Feld bestimmen, ob es in der nächsten Generation lebt oder tot ist*/
        for (int i = 0; i < reihe; i++) {
            for (int j = 0; j < zeile; j++) {
                int nachbar = countNeighbors(i, j);
                /*Zustand in der nächsten Generation abhängig von dem aktuellen Zustand und der Anzahl an lebendigen Nachbarn*/
                if (rules[grid[i][j]][nachbar]) {
                    next[i][j] = 1;
                    grid_lifetime[i][j] = Math.min(9, grid_lifetime[i][j]+1);
                }
                else{
                    grid_lifetime[i][j] = 0;
                }
            }
        }
        generation.set(generation.getValue() + 1);
        /*Neuen Generation in dem Feld speichern und darstellen*/
        grid = next;
        draw(graphics);
    }

    /*Aktuellen Zustand abspeichern in Json-Datei im angegebenen Pfad*/
    public void save() throws IOException {
        /*Default-Path*/
        Path pfad = Paths.get("D:\\Uni_sec_sem\\OOSE\\OOSEProject\\conway_fixed_fxml\\Lifetime\\default.json");
        /*Speicherstruktur für das Feld*/
        JsonArray to_save = new JsonArray();
        /*Falls ein Dateiname angegeben wurde, wird dieser statt Default genutzt.*/
        if (save_name != null) {
            /*Zusammenbauen des Path-Strings*/
            String base_path = "D:\\Uni_sec_sem\\OOSE\\OOSEProject\\conway_fixed_fxml\\Lifetime\\";
            String path_string = base_path +
                    save_name +
                    ".json";
            pfad = Paths.get(path_string);
        }
        /*Falls noch keine Datei existiert unter dem angegebenen Pfad, wird eine erstellt. Sonst wird nichts getan.*/
        if (!Files.exists(pfad)) {
            Files.createFile(pfad);
        }
        else{
            return;
        }
        /*Abspeichern des Feldes im Json-Array*/
        for (int[] ints : grid_lifetime) {
            JsonArray helper = new JsonArray();
            for (int j = 0; j < grid_lifetime[0].length; j++) {
                helper.add(ints[j]);
            }
            to_save.add(helper);
        }
        /*Json-Datei mit Json-Array füllen*/
        Files.writeString(pfad, gson.toJson(to_save));
    }

    public void reverse_rules(){
        for(int i = 0; i < rules.length; i++){
            for(int j = 0; j < rules[0].length; j++){
                rules[i][j] = !rules[i][j];
            }
        }
        setting_controller.set_selected();
    }

    /*Aktuellen Zustand laden aus Json-Datei im angegebenen Pfad*/
    public void load(GraphicsContext graphics) throws IOException {
        /*Path wird später initialisiert*/
        Path pfad;
        /*Feld wird gleich in saved_grid geladen und danach in den eigentlichen grid übergeben*/
        int[][] saved_grid;
        /*Falls ein Dateiname angegeben wurde, wird dieser genutzt.*/
        if (load_name != null) {
            String base_path = "D:\\Uni_sec_sem\\OOSE\\OOSEProject\\conway_fixed_fxml\\Lifetime\\";
            String path_string = base_path +
                    load_name +
                    ".json";
            pfad = Paths.get(path_string);
        }
        /*Falls keiner angegeben wurde oder die Datei nicht existiert, ändert sich nichts.*/
        else {
            return;
        }
        if (!Files.exists(pfad)) {
            return;
        }
        /*Einlesen des Feldes, falls es existiert, übertragen in grid und darstellen*/
        else {
            String array_string = Files.readString(pfad);
            saved_grid = gson.fromJson(array_string, new TypeToken<int[][]>() {
            }.getType());
            if (saved_grid.length == 0) {
                return;
            }
        }
        //Laden des gespeicherten Gitters + Lifetimes
        for(int i = 0; i < saved_grid.length; i++){
            for(int j = 0; j < saved_grid[0].length; j++){
                grid[i][j] = Math.min(saved_grid[i][j], 1);
                grid_lifetime[i][j] = saved_grid[i][j];
            }
        }
        //Falls gefunden Lebenszeit auch laden
        generation.set(0);
        draw(graphics);
    }

    /*Aktuellen Zustand abspeichern in Json-Datei im angegebenen Pfad*/
    public void save_rules() throws IOException {
        /*Default-Path*/
        Path pfad = Paths.get("D:\\Uni_sec_sem\\OOSE\\OOSEProject\\conway_fixed_fxml\\Rules\\default.json");
        /*Speicherstruktur für das Feld*/
        JsonArray to_save = new JsonArray();
        /*Falls ein Dateiname angegeben wurde, wird dieser statt Default genutzt.*/
        if (save_rules_name != null) {
            /*Zusammenbauen des Path-Strings*/
            String base_path = "D:\\Uni_sec_sem\\OOSE\\OOSEProject\\conway_fixed_fxml\\Rules\\";
            String path_string = base_path +
                    save_rules_name +
                    ".json";
            pfad = Paths.get(path_string);
        }
        /*Falls noch keine Datei existiert unter dem angegebenen Pfad, wird eine erstellt*/
        if (!Files.exists(pfad)) {
            Files.createFile(pfad);
        }
        /*Abspeichern des Feldes im Json-Array*/
        for (boolean[] bools : rules) {
            JsonArray helper = new JsonArray();
            for (int j = 0; j < rules[0].length; j++) {
                helper.add(bools[j]);
            }
            to_save.add(helper);
        }
        /*Json-Datei mit Json-Array füllen*/
        Files.writeString(pfad, gson.toJson(to_save));
    }

    /*Aktuellen Zustand laden aus Json-Datei im angegebenen Pfad*/
    public void load_rules() throws IOException {
        /*Path wird später initialisiert*/
        Path pfad;
        /*Feld wird gleich in saved_rules geladen und danach in die eigentlichen Regeln übergeben*/
        boolean[][] saved_rules;
        /*Falls ein Dateiname angegeben wurde, wird dieser genutzt.*/
        if (load_rules_name != null) {
            String base_path = "D:\\Uni_sec_sem\\OOSE\\OOSEProject\\conway_fixed_fxml\\Rules\\";
            String path_string = base_path +
                    load_rules_name +
                    ".json";
            pfad = Paths.get(path_string);
        }
        /*Falls keiner angegeben wurde oder die Datei nicht existiert, ändert sich nichts.*/
        else {
            return;
        }
        if (!Files.exists(pfad)) {
            return;
        }
        /*Einlesen des Feldes, falls es existiert und übertragen in Rules*/
        else {
            String array_string = Files.readString(pfad);
            saved_rules = gson.fromJson(array_string, new TypeToken<boolean[][]>() {
            }.getType());
            if (saved_rules.length == 0) {
                return;
            }
        }
        rules = saved_rules;
        setting_controller.set_selected();
    }


    /*Invertieren der Zellenzustände*/
    public void reverse(GraphicsContext graphics) {
        /*Iteriere über alle Felder, invertiere*/
        for (int i = 0; i < reihe; i++) {
            for (int j = 0; j < zeile; j++) {
                //Alle lebenden Zellen sterben und alle toten leben danach -> Lebenszeit für alle 0
                grid[i][j] = 1 - grid[i][j];
                grid_lifetime[i][j] = 0;
            }
        }
        draw(graphics);
    }

    //UI auswählen und auf die rechte Seite des Borderpanes einfügen
    public void setting_ui() {
        borderpane.setRight(setting_settings);
    }

    public void save_ui() {
        borderpane.setRight(save_settings);
    }

    public void load_ui() {
        borderpane.setRight(load_settings);
    }

    public void game_ui() {
        borderpane.setRight(game_settings);
    }

    //Funktion, die der Close-Button zum Schließen nutzt
    public void close() {
        Stage stage = (Stage) close_button.getScene().getWindow();
        stage.close();
    }

    //Herauszoomen(50x50 Feld)
    public void zoom_out(){
        //maximal 50x50
        if(zoom>=50){return;}
        //Dargestellte Höhe und Breite um je 4 Zellen erhöhen
        zoom += 4;
        //Randfall 1 (top_left_y minus Zoom-Inkrement / 2)
        top_left_y = Math.max(top_left_y-2,0);
        //Randfall 2
        while(top_left_y + zoom > 50){
            top_left_y -= 1;
        }
        //Randfall 1 (normal top_left_x minus Zoom-Inkrement / 2)
        top_left_x = Math.max(top_left_x-2,0);
        //Randfall 2
        while(top_left_x + zoom > 50){
            top_left_x -= 1;
        }
        //Zellengröße dem geänderten Zoom anpassen
        cellsize = 500.0 / zoom;
        draw(graphics);
    }

    public void zoom_in(){
        //Kleinstmöglichstes Fenster 10x10
        if(zoom<=10){return;}
        //Bei jedem Zoom je 4 Zeilen / Spalten kleiner
        zoom -= 4;
        top_left_y += 2;
        top_left_x += 2;
        //Zellengröße anpassen
        cellsize = 500.0 / zoom;
        draw(graphics);
    }

    //Methode, um zu regeln, was passiert, wenn man auf dem dem Canvas eine Maustaste drückt
    public void canvas_mouse_pressed(MouseEvent event){
        try {
            //Klickpositions speichern und Verschiebung aufgrund des Zooms berücksichtigen
            int i = (int) (event.getX() / cellsize);
            int j = (int) (event.getY() / cellsize);
            i += top_left_x;
            j += top_left_y;

            //Nur fortfahren, wenn lediglich einer der beiden Maustasten gedrückt ist
            if (event.isPrimaryButtonDown() && event.isSecondaryButtonDown()) {
                return;
            } else if (!event.isSecondaryButtonDown() && !event.isPrimaryButtonDown()) {
                return;
            }
            //Zellen löschen / beleben mit linker Maustaste
            if (event.isPrimaryButtonDown()) {
                if(grid[i][j] != add_delete){
                    grid[i][j] = add_delete;
                    if(grid[i][j] == 1){
                        grid_lifetime[i][j] = 1;
                    }
                    else{
                        grid_lifetime[i][j] = 0;
                    }
                    draw(graphics);
                }
            }
            else {
                //Mausposition speichern
                mouse_pos_y = event.getY();
                mouse_pos_x = event.getX();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void canvas_mouse_dragged(MouseEvent event) {
        try {
            //Klickpositions speichern und Verschiebung aufgrund des Zooms berücksichtigen (bezogen auf das Feld)
            int i = (int) (event.getX() / cellsize);
            int j = (int) (event.getY() / cellsize);
            i += top_left_x;
            j += top_left_y;

            //Nur fortfahren, wenn lediglich einer der beiden Maustasten gedrückt ist
            if (event.isPrimaryButtonDown() && event.isSecondaryButtonDown()) {
                return;
            } else if (!event.isSecondaryButtonDown() && !event.isPrimaryButtonDown()) {
                return;
            }

            //Zellen beleben / töten mit linker Maustaste
            if(event.isPrimaryButtonDown()){
                if(grid[i][j] != add_delete){
                    grid[i][j] = add_delete;
                    if(grid[i][j] == 1){
                        grid_lifetime[i][j] = 1;
                    }
                    else{
                        grid_lifetime[i][j] = 0;
                    }
                    draw(graphics);
                }
            }
            //Sichtfeld bewegen mit rechter Maustaste
            else {
                //In die 4 verschiedenen Richtungen prüfen, ob das Fenster verschoben werden muss
                while(event.getY() - mouse_pos_y > cellsize){
                    if(top_left_y + zoom < 50){
                        top_left_y += 1;
                        mouse_pos_y += cellsize;
                    }
                    else{
                        break;
                    }
                }
                while(event.getY() - mouse_pos_y < (cellsize*(-1.0))){
                    if(top_left_y != 0){
                        top_left_y -= 1;
                        mouse_pos_y -= cellsize;
                    }
                    else{
                        break;
                    }
                }
                while(event.getX() - mouse_pos_x > cellsize){
                    if(top_left_x + zoom < 50){
                        top_left_x += 1;
                        mouse_pos_x += cellsize;
                    }
                    else{
                        break;
                    }
                }
                while(event.getX() - mouse_pos_x < (cellsize*(-1.0))){
                    if(top_left_x != 0){
                        top_left_x -= 1;
                        mouse_pos_x -= cellsize;
                    }
                    else{
                        break;
                    }
                }
                draw(graphics);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    //dunkel -> hell
    //Die vier verschiedenen Color-Themes zwischen denen zur Darstellung der Zellen gewählt werden kann
    public void get_color_theme(String color_string) {
        if(color_string.equals("Red")){
            color_theme = new Color[]{Color.web("03071e"), Color.web("370617"), Color.web("6a040f"), Color.web("9d0208"), Color.web("d00000"), Color.web("dc2f02"), Color.web("e85d04"), Color.web("f48c06"), Color.web("faa307"), Color.web("ffba08")};
        }
        if(color_string.equals("Blue")){
            color_theme = new Color[]{Color.web("03045e"), Color.web("03045e"), Color.web("023e8a"), Color.web("0077b6"), Color.web("0096c7"), Color.web("00b4d8"), Color.web("48cae4"), Color.web("90e0ef"), Color.web("ade8f4"), Color.web("caf0f8")};
        }
        if(color_string.equals("Green")){
            color_theme = new Color[]{Color.web("081c15"), Color.web("081c15"), Color.web("1b4332"), Color.web("2d6a4f"), Color.web("40916c"), Color.web("52b788"), Color.web("74c69d"), Color.web("95d5b2"), Color.web("b7e4c7"), Color.web("d8f3dc")};
        }
        if(color_string.equals("Purple")){
            color_theme = new Color[]{Color.web("10002b"), Color.web("10002b"), Color.web("240046"), Color.web("3c096c"), Color.web("5a189a"), Color.web("7b2cbf"), Color.web("7b2cbf"), Color.web("9d4edd"), Color.web("c77dff"), Color.web("e0aaff")};
        }
    }

    //Initialize-Methode
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Regeln setzen (normale Game-of-Life-Regeln)
        standard_rules = new boolean[][]{{false, false, false, true, false, false, false, false, false},
                {false, false, true, true, false, false, false, false, false}};
        rules = new boolean[][]{{false, false, false, true, false, false, false, false, false},
                {false, false, true, true, false, false, false, false, false}};

        //Color-Theme zunächst rot
        color_theme = new Color[]{Color.web("03071e"), Color.web("370617"), Color.web("6a040f"), Color.web("9d0208"), Color.web("d00000"), Color.web("dc2f02"), Color.web("e85d04"), Color.web("f48c06"), Color.web("faa307"), Color.web("ffba08")};

        //"Unter-Controller" setzen
        setting_controller = new Setting_Controller();
        load_controller = new Load_Controller();
        save_controller = new Save_Controller();
        game_setting_controller = new Game_Setting_Controller();

        //Auf Main-Controller verweisen (this)
        setting_controller.init(this);
        load_controller.init(this);
        save_controller.init(this);
        game_setting_controller.init(this);

        //verschiedene Loader für die "Unter-Controller"
        FXMLLoader loader = new FXMLLoader((getClass().getResource("game_setting_pane.fxml")));
        try {
            game_settings = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        game_setting_controller = loader.getController();
        game_setting_controller.init(this);

        FXMLLoader loader1 = new FXMLLoader((getClass().getResource("setting_pane.fxml")));
        try {
            setting_settings = loader1.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setting_controller = loader1.getController();
        setting_controller.init(this);

        FXMLLoader loader2 = new FXMLLoader((getClass().getResource("load_pane.fxml")));
        try {
            load_settings = loader2.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        load_controller = loader2.getController();
        load_controller.init(this);

        FXMLLoader loader3 = new FXMLLoader((getClass().getResource("save_pane.fxml")));
        try {
            save_settings = loader3.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        save_controller = loader3.getController();
        save_controller.init(this);

        //Ausgewählte Regeln setzen
        setting_controller.set_selected();

        //Anfangs sollen GameSettings angezeigt werden
        borderpane.setRight(game_settings);

        //GraphicsContext setzen (Canvas) und initialisieren
        graphics = canvas.getGraphicsContext2D();
        init(graphics);

        //Simulationsgeschwindigkeit setzen und Listener hinzufügen
        sliderspeed = 250.0;
        game_setting_controller.speedProperty().addListener(((observable, oldValue, newValue) -> sliderspeed = newValue.doubleValue()));

        //Add_delete gibt an, ob man mit linker Maustaste löscht oder belebt
        //Initialisierung und hinzufügen eines Listeners
        add_delete = 1;
        game_setting_controller.add_delete_Property().addListener(((observable, oldValue, newValue) -> add_delete = newValue.intValue()));

        //Pfad unter dem gespeichert wird, wird durch save_name definiert
        //Initialisierung und hinzufügen eines Listeners
        save_name = "default";
        save_controller.save_name_Property().addListener(((observable, oldValue, newValue) -> save_name = newValue));

        //Pfad unter dem gespeichert wird, wird durch save_rules_name definiert
        //Initialisierung und hinzufügen eines Listeners
        save_rules_name = "default";
        save_controller.save_rule_name_Property().addListener(((observable, oldValue, newValue) -> save_rules_name = newValue));

        //Pfad aus dem geladen wird, wird durch load_name definiert
        //Initialisierung und hinzufügen eines Listeners
        load_name = "default";
        load_controller.load_name_Property().addListener(((observable, oldValue, newValue) -> load_name = newValue));

        //Pfad aus dem geladen wird, wird durch load_rules_name definiert
        //Initialisierung und hinzufügen eines Listeners
        load_rules_name = "default";
        load_controller.load_rules_name_Property().addListener(((observable, oldValue, newValue) -> load_rules_name = newValue));

        //Ordner erstellen, falls nicht vorhanden
        new File("D:\\Uni_sec_sem\\OOSE\\OOSEProject\\conway_fixed_fxml\\Rules").mkdirs();
        new File("D:\\Uni_sec_sem\\OOSE\\OOSEProject\\conway_fixed_fxml\\Lifetime\\").mkdirs();

        //Mögliche Farboptionen der ComboBox hinzufügen und Listener hinzufügen
        setting_controller.color_combobox.getItems().clear();
        setting_controller.color_combobox.getItems().addAll("Red","Blue","Green","Purple");
        setting_controller.color_combobox.getSelectionModel().select("Red");
        setting_controller.color_combobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> get_color_theme(newValue));

        //Generationszähler
        generation.set(0);
        generation.addListener((observable, oldValue, newValue) -> game_setting_controller.generation_count.setText("Generation: " + generation.getValue()));

        //Animation definieren, entsprechend der durch den Slider definierten Geschwindigkeit wird aktualisiert
        animation = new AnimationTimer() {
            private long lastUpdate=0;
            @Override
            public void handle(long now) {
                if ((now - lastUpdate) >= TimeUnit.MILLISECONDS.toNanos((long) sliderspeed)) {
                    tick(graphics);
                    lastUpdate = now;
                }
            }
        };
    }
}
