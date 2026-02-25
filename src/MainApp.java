import DB.DBConn;
import DB.ReadJSON;
import Model.BushalteLocatieModel;
import View.MyScene;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MainApp extends Application {

    private static DBConn conn;

    public static void main(String[] args) {

        new DBConn();
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        // initialize BushalteLocatieModels in advance.
        ReadJSON readJSON = new ReadJSON();
        ArrayList<BushalteLocatieModel> bushaltes = readJSON.getBushalteLocatieData();
        HostServices hostServices = getHostServices();

        stage.setTitle("Bushaltes Gemeente Nijmegen");
        stage.setScene(new MyScene(bushaltes, hostServices));
        stage.setMaximized(true);
        stage.show();

        DBConn.instance.makeConnection();
    }

    @Override
    public void stop() throws Exception {

        DBConn.instance.closeConnection();
    }
}