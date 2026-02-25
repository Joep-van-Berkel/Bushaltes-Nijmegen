package View;

import Model.BushalteLocatieModel;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class MyScene extends Scene {

    private BorderPane root;
    private ArrayList<BushalteLocatieModel> bushaltes;
    private HostServices hostServices;

    private BushalteLocatieModel currentBushalte;

    public MyScene(ArrayList<BushalteLocatieModel> bushaltes, HostServices hostServices) {
        super(new Pane());

        this.bushaltes = bushaltes;
        this.hostServices = hostServices;

        root = new BorderPane();
        this.setRoot(root);

        this.goToHomeView();
    }

    public void goToHomeView(){
        root.setTop(new MenuBarView(this));
        root.setCenter(new HomeView());
    }

    public void goToLocatiesView(){
        root.setTop(new MenuBarView(this));
        root.setCenter(new LocatiesView(this, bushaltes, hostServices));
    }

    public void goToPlanningView(BushalteLocatieModel bushalte){
        root.setTop(new MenuBarView(this));
        root.setCenter(new PlanningView(bushalte));
    }

    public void goToMedewerkerView(){
        root.setTop(new MenuBarView(this));
        root.setCenter(new MedewerkerView());
    }

    public void goToAdminView(){
        root.setTop(new MenuBarView(this));
        root.setCenter(new AdminView());
    }
}