package View;

import DB.DBConn;
import Model.BushalteLocatieModel;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class LocatiesView extends BorderPane {

    private ArrayList<BushalteLocatieModel> bushaltes;
    private HostServices hostServices;
    private DBConn conn;

    public LocatiesView(MyScene scene, ArrayList<BushalteLocatieModel> bushaltes, HostServices hostServices) {
        this.bushaltes = bushaltes;
        this.hostServices = hostServices;

        // Set padding for the main BorderPane
        this.setPadding(new Insets(10));

        // Create a header label
        Label headerLabel = new Label("Bushalte locaties");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        // Arrange the header in a VBox to add some padding
        VBox headerContainer = new VBox(headerLabel);
        headerContainer.setPadding(new Insets(10));

        // Set the header at the top of the BorderPane
        this.setTop(headerContainer);

        // Create a ListView to display the list of bushaltes
        ListView<String> listView = new ListView<>();
        listView.setPrefWidth(250);

        // Convert bushaltes to an ObservableList of names
        ObservableList<String> bushalteNames = FXCollections.observableArrayList(
                bushaltes.stream().map(BushalteLocatieModel::getHalteNaam).collect(Collectors.toList())
        );
        listView.setItems(bushalteNames);

        // Create a search bar and button
        HBox searchContainer = new HBox(10);
        TextField searchBar = new TextField();
        searchBar.setPromptText("Typ om te zoeken..");
        Button deselecteerButton = new Button("Deselecteer");
        searchContainer.getChildren().addAll(searchBar, deselecteerButton);

        // Create a WebView to display the map
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/resources/map.html").toExternalForm());
        webView.getEngine().setJavaScriptEnabled(true);

        // Add the pins to the map
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                for (BushalteLocatieModel bushalte : bushaltes) {
                    double[] coords = bushalte.getCoordinaten();
                    String script = String.format(Locale.US, "addPin(%f, %f, '%s')", coords[1], coords[0], bushalte.getHalteNaam());
                    webEngine.executeScript(script);
                }
            }
        });

        // Create a HashMap to store bushaltes by their names
        HashMap<String, BushalteLocatieModel> bushalteMap = new HashMap<>();
        for (BushalteLocatieModel bushalte : bushaltes) {
            bushalteMap.put(bushalte.getHalteNaam(), bushalte);
        }

        // Create labels to display the selected bushalte name and its accessibility
        Label selectedBushalteLabel = new Label("Bushalte: ");
        Label rolstoeltoegankelijkheidLabel = new Label("Rolstoeltoegankelijk: ");

        // Add a listener to the ListView to zoom in onto the map
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldvalue, newValue) -> {
            if (newValue != null) {
                BushalteLocatieModel selectedBushalte = bushalteMap.get(newValue);
                if (selectedBushalte != null) {
                    double[] coords = selectedBushalte.getCoordinaten();

                    String setIndivMarkerScript = String.format(Locale.US, "addPin(%f, %f, '%s')", coords[1], coords[0], selectedBushalte.getHalteNaam());
                    String zoomScript = String.format(Locale.US, "zoomAtPin(%f, %f)", coords[1], coords[0]);

                    webEngine.executeScript("removePins()");
                    webEngine.executeScript(setIndivMarkerScript);
                    webEngine.executeScript(zoomScript);
                }
            }
        });

        Hyperlink googleMapsLink = new Hyperlink("Bekijk route op Google Maps");

        // Add a listener to the ListView to update the labels and the hyperlink
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                BushalteLocatieModel selectedBushalte = bushalteMap.get(newValue);
                if (selectedBushalte != null) {
                    selectedBushalteLabel.setText("Bushalte: " + selectedBushalte.getHalteNaam());
                    rolstoeltoegankelijkheidLabel.setText("Rolstoeltoegankelijk: " + (selectedBushalte.getRolstoelToegankelijk() ? "Ja" : "Nee"));

                    // Update the hyperlink with the coordinates
                    double[] coords = selectedBushalte.getCoordinaten();
                    String googleMapsUrl = String.format(Locale.US, "https://www.google.com/maps/dir/?api=1&origin=Current+Location&destination=%f,%f", coords[0], coords[1]);
                    googleMapsLink.setText("Bekijk route op Google Maps");
                    googleMapsLink.setOnAction(e -> hostServices.showDocument(googleMapsUrl));
                }
            }
        });

        // Add a listener to the searchBar to filter the ListView
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            listView.setItems(bushalteNames.filtered(name -> name.toLowerCase().contains(newValue.toLowerCase())));
        });

        deselecteerButton.setOnAction(event -> {
            // Clear the selection in the ListView
            listView.getSelectionModel().clearSelection();

            // Clear all markers
            webEngine.executeScript("removePins()");

            // Add all the markers back to the map
            for (BushalteLocatieModel bushalte : bushaltes) {
                double[] coords = bushalte.getCoordinaten();
                String script = String.format(Locale.US, "addPin(%f, %f, '%s')", coords[1], coords[0], bushalte.getHalteNaam());
                webEngine.executeScript(script);
            }

            // Zoom out to the initial view
            webEngine.executeScript("resetZoom()");

            // Clear the labels
            selectedBushalteLabel.setText(" Bushalte: ");
            rolstoeltoegankelijkheidLabel.setText(" Rolstoeltoegankelijk: ");
        });

        // Create a button to perform an action with the selected bushalte info
        Button planningButton = new Button("Planning");
        planningButton.setPrefWidth(200);
        planningButton.setOnAction(event -> {
            String selectedBushalte = listView.getSelectionModel().getSelectedItem();
            if (selectedBushalte != null) {
                BushalteLocatieModel bushalte = bushalteMap.get(selectedBushalte);
                if (bushalte != null) {
                    scene.goToPlanningView(bushalte);
                }
            }
        });

        // Arrange the components in a VBox layout
        VBox centerPane = new VBox(10);
        centerPane.setPadding(new Insets(10));
        centerPane.getChildren().addAll(webView, selectedBushalteLabel, rolstoeltoegankelijkheidLabel, googleMapsLink, planningButton);
        VBox.setVgrow(webView, Priority.ALWAYS);

        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.getChildren().addAll(listView, searchContainer);
        VBox.setVgrow(listView, Priority.ALWAYS);

        this.setCenter(centerPane);
        this.setRight(rightPane);

        Platform.runLater(() -> this.requestFocus());
    }
}