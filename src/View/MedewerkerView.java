package View;

import DB.EmailDB;
import DB.MedewerkerDB;
import DB.MedewerkerGegevensDB;
import Model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class MedewerkerView extends BorderPane {
    private static final double COMPONENT_WIDTH = 300; // Constant for the width of ComboBox and Button
    private static final double LEFT_CONTAINER_WIDTH = 300; // Constant for the width of the left container
    private static final double TABLE_VIEW_WIDTH = 600; // Constant for the width of the TableView
    private static final double TABLE_COLUMN_WIDTH = TABLE_VIEW_WIDTH / 3; // Constant for the width of each TableColumn

    private ComboBox<String> medewerkerComboBox;
    private ComboBox<String> emailComboBox;
    private ComboBox<String> soortMedewerkerComboBox;
    private TableView<MedewerkerGegevensModel> tableView;
    private MedewerkerGegevensDB medewerkerGegevensDB;
    private MedewerkerDB medewerkerDB;
    private EmailDB emailDB;

    public MedewerkerView() {
        medewerkerDB = new MedewerkerDB();
        emailDB = new EmailDB();
        medewerkerGegevensDB = new MedewerkerGegevensDB();

        tableView = new TableView<>();

        // Left container
        VBox leftContainer = new VBox(10);
        leftContainer.setPadding(new Insets(20));
        leftContainer.setAlignment(Pos.TOP_LEFT);
        leftContainer.setPrefWidth(LEFT_CONTAINER_WIDTH);

        Label titleLabel = new Label("Medewerker aanmaken");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));

        VBox medewerkerBox = createLabeledComboBox("Medewerker naam");
        VBox emailBox = createLabeledComboBox("Medewerker email");
        VBox soortMedewerkerBox = createLabeledComboBox("Soort medewerker");

        medewerkerComboBox = (ComboBox<String>) medewerkerBox.getChildren().get(1);
        emailComboBox = (ComboBox<String>) emailBox.getChildren().get(1);
        soortMedewerkerComboBox = (ComboBox<String>) soortMedewerkerBox.getChildren().get(1);
        soortMedewerkerComboBox.getItems().addAll("In loondienst", "Freelancer");

        medewerkerComboBox.setPrefWidth(COMPONENT_WIDTH);
        emailComboBox.setPrefWidth(COMPONENT_WIDTH);
        soortMedewerkerComboBox.setPrefWidth(COMPONENT_WIDTH);

        // Populate comboboxes with data from the databases
        populateComboBox(medewerkerComboBox, getMedewerkerNames());
        populateComboBox(emailComboBox, getEmailAddresses());

        Button aanmakenButton = new Button("Aanmaken");
        aanmakenButton.setPrefWidth(COMPONENT_WIDTH);

        Button verwijderButton = new Button("Verwijderen");
        verwijderButton.setPrefWidth(COMPONENT_WIDTH);

        Region leftSpacer = new Region();
        VBox.setVgrow(leftSpacer, Priority.ALWAYS);

        leftContainer.getChildren().addAll(titleLabel, medewerkerBox, emailBox, soortMedewerkerBox, leftSpacer, aanmakenButton, verwijderButton);

        // Right container
        VBox rightContainer = new VBox(10);
        rightContainer.setPadding(new Insets(20));
        rightContainer.setAlignment(Pos.TOP_LEFT);

        Label tableTitleLabel = new Label("Medewerkers lijst");
        tableTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        tableTitleLabel.setPadding(new Insets(0, 0, 10, 0));
        tableView.setPrefWidth(TABLE_VIEW_WIDTH);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        TableColumn<MedewerkerGegevensModel, String> nameColumn = new TableColumn<>("Naam");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("medewerkerNaam"));
        nameColumn.setPrefWidth(TABLE_COLUMN_WIDTH);

        TableColumn<MedewerkerGegevensModel, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(TABLE_COLUMN_WIDTH);

        TableColumn<MedewerkerGegevensModel, String> soortColumn = new TableColumn<>("Soort");
        soortColumn.setCellValueFactory(new PropertyValueFactory<>("medewerkerSoort"));
        soortColumn.setPrefWidth(TABLE_COLUMN_WIDTH);

        tableView.getColumns().addAll(nameColumn, emailColumn, soortColumn);

        rightContainer.getChildren().addAll(tableTitleLabel, tableView);

        // Center the layout
        HBox centerContainer = new HBox(20);
        centerContainer.setPadding(new Insets(20));
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.getChildren().addAll(leftContainer, rightContainer);

        // Set containers to the BorderPane
        this.setCenter(centerContainer);

        Platform.runLater(() -> centerContainer.requestFocus());

        aanmakenButton.setOnAction(event -> createMedewerker());
        verwijderButton.setOnAction(event -> deleteMedewerker());

        populateTableView();
    }

    private VBox createLabeledComboBox(String labelText) {
        Label label = new Label(labelText);
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText("Maak een keuze..");
        VBox vBox = new VBox(5, label, comboBox);
        vBox.setAlignment(Pos.TOP_LEFT);
        return vBox;
    }

    private void populateComboBox(ComboBox<String> comboBox, ObservableList<String> items) {
        comboBox.setItems(items);
    }

    private ObservableList<String> getMedewerkerNames() {
        ObservableList<String> names = FXCollections.observableArrayList();
        for (MedewerkerModel medewerker : medewerkerDB.selectAllRecords()) {
            names.add(medewerker.getMedewerkerNaam());
        }
        return names;
    }

    private ObservableList<String> getEmailAddresses() {
        ObservableList<String> emails = FXCollections.observableArrayList();
        for (EmailModel email : emailDB.selectAllRecords()) {
            emails.add(email.getEmail());
        }
        return emails;
    }

    private void createMedewerker() {
        String medewerkerNaam = medewerkerComboBox.getValue();
        String email = emailComboBox.getValue();
        String soortMedewerker = soortMedewerkerComboBox.getValue();

        if (medewerkerNaam == null || email == null || soortMedewerker == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "Niet alle velden zijn ingevuld.");
            return;
        }

        // Check if the record already exists
        if (medewerkerGegevensDB.getMedewerkerByEmail(email) != null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Email wordt al gebruikt door een medewerker.");
            return;
        }

        MedewerkerGegevensModel medewerker = new MedewerkerGegevensModel(medewerkerNaam, email, soortMedewerker);

        boolean success = medewerkerGegevensDB.insertMedewerker(medewerker);
        if (success) {
            populateTableView();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Medewerker succesvol aangemaakt.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Er is een fout opgetreden bij het aanmaken van de medewerker.");
        }
    }

    private void deleteMedewerker() {
        MedewerkerGegevensModel selectedMedewerker = tableView.getSelectionModel().getSelectedItem();
        if (selectedMedewerker != null) {
            boolean success = medewerkerGegevensDB.deleteMedewerker(selectedMedewerker);
            if (success) {
                populateTableView();
            }
        }
    }

    private void populateTableView() {
        List<MedewerkerGegevensModel> medewerkers = medewerkerGegevensDB.getAllMedewerkers();
        ObservableList<MedewerkerGegevensModel> data = FXCollections.observableArrayList(medewerkers);
        tableView.setItems(data);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}