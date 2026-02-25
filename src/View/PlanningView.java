package View;

import DB.EmailDB;
import DB.MedewerkerBushaltePlanningDB;
import DB.MedewerkerGegevensDB;
import Model.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PlanningView extends BorderPane {

    private final BushalteLocatieModel bushalte;
    private final MedewerkerGegevensDB medewerkerGegevensDB;
    private final MedewerkerBushaltePlanningDB medewerkerBushaltePlanningDB;
    private final EmailDB emailDB;
    private final TableView<MedewerkerGegevensModel> medewerkerTableView;
    private final TableView<MedewerkerBushaltePlanningModel> planningTableView;
    private final ComboBox<String> emailComboBox;
    private final DatePicker datePicker;
    private final Spinner<Integer> hourSpinner;
    private final Spinner<Integer> minuteSpinner;

    public PlanningView(BushalteLocatieModel bushalte) {
        this.bushalte = bushalte;
        this.medewerkerGegevensDB = new MedewerkerGegevensDB();
        this.medewerkerBushaltePlanningDB = new MedewerkerBushaltePlanningDB();
        this.emailDB = new EmailDB();
        this.planningTableView = new TableView<>();
        this.emailComboBox = new ComboBox<>();
        this.medewerkerTableView = new TableView<>();
        this.datePicker = new DatePicker();
        this.hourSpinner = new Spinner<>(0, 23, 12);
        this.minuteSpinner = new Spinner<>(0, 59, 0);

        setUpLayout();
        setUpPlanningTableViewColumns(); // Set up columns once
        populateEmailComboBox();
        populateMedewerkerTableView();
        populatePlanningTableView();
        restrictDatePicker();
    }

    private void setUpLayout() {
        VBox firstContainer = createFirstContainer();
        VBox secondContainer = createSecondContainer();
        VBox thirdContainer = createThirdContainer();

        HBox mainContainer = new HBox(20, firstContainer, secondContainer, thirdContainer);
        mainContainer.setAlignment(Pos.CENTER);

        VBox topContainer = new VBox(mainContainer);
        topContainer.setPadding(new Insets(100, 0, 100, 0));
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        this.setCenter(topContainer);

        Platform.runLater(() -> mainContainer.requestFocus());
    }

    private VBox createFirstContainer() {
        VBox firstContainer = new VBox(10);
        firstContainer.setPadding(new Insets(10));

        Label header1 = new Label("Personeel inplannen");
        header1.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label emailLabel = new Label("Email");
        emailComboBox.setMaxWidth(Double.MAX_VALUE);
        emailComboBox.setPromptText("Maak een keuze..");

        Label dateLabel = new Label("Datum");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setEditable(false);

        Label timeLabel = new Label("Tijdstip");
        HBox timeBox = new HBox(10, hourSpinner, minuteSpinner);
        timeBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(hourSpinner, Priority.ALWAYS);
        HBox.setHgrow(minuteSpinner, Priority.ALWAYS);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox buttonBox1 = new VBox(10);
        Button createButton = new Button("Aanmaken");
        createButton.setMaxWidth(Double.MAX_VALUE);
        createButton.setOnAction(e -> handleAanmakenButton());
        Button deleteButton = new Button("Verwijderen");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setOnAction(e -> handleVerwijderenButton());
        buttonBox1.getChildren().addAll(createButton, deleteButton);

        firstContainer.getChildren().addAll(header1, emailLabel, emailComboBox, dateLabel, datePicker, timeLabel, timeBox, spacer, buttonBox1);
        return firstContainer;
    }

    private VBox createSecondContainer() {
        VBox secondContainer = new VBox(10);
        secondContainer.setPadding(new Insets(10));
        secondContainer.setPrefWidth(700);

        Label header2 = new Label("Planning bushalte: " + bushalte.getHalteNaam());
        header2.setFont(Font.font("System", FontWeight.BOLD, 22));

        VBox.setVgrow(planningTableView, Priority.ALWAYS);
        secondContainer.getChildren().addAll(header2, planningTableView);
        return secondContainer;
    }

    private VBox createThirdContainer() {
        VBox thirdContainer = new VBox(10);
        thirdContainer.setPadding(new Insets(10));
        thirdContainer.setPrefWidth(600);

        Label header3 = new Label("Medewerker gegevens");
        header3.setFont(Font.font("System", FontWeight.BOLD, 22));

        VBox.setVgrow(medewerkerTableView, Priority.ALWAYS);
        thirdContainer.getChildren().addAll(header3, medewerkerTableView);
        return thirdContainer;
    }

    private void setUpPlanningTableViewColumns() {
        // Create columns for the planningTableView
        TableColumn<MedewerkerBushaltePlanningModel, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<MedewerkerBushaltePlanningModel, String> bushalteNaamColumn = new TableColumn<>("Bushalte");
        bushalteNaamColumn.setCellValueFactory(new PropertyValueFactory<>("bushalteNaam"));

        TableColumn<MedewerkerBushaltePlanningModel, String> planDatumColumn = new TableColumn<>("Plan Datum");
        planDatumColumn.setCellValueFactory(cellData -> {
            String planDatum = cellData.getValue().getPlanDatum();
            LocalDate date = LocalDate.parse(planDatum, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        });

        TableColumn<MedewerkerBushaltePlanningModel, String> planTijdstipColumn = new TableColumn<>("Plan Tijdstip");
        planTijdstipColumn.setCellValueFactory(cellData -> {
            String planTijdstip = cellData.getValue().getPlanTijdstip();
            LocalTime time = LocalTime.parse(planTijdstip, DateTimeFormatter.ofPattern("HH:mm:ss"));
            return new SimpleStringProperty(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        });

        // Add the columns to the planningTableView
        planningTableView.getColumns().addAll(emailColumn, bushalteNaamColumn, planDatumColumn, planTijdstipColumn);

        // Adjust column widths when the table width changes
        planningTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            emailColumn.setPrefWidth(tableWidth * 0.3);
            bushalteNaamColumn.setPrefWidth(tableWidth * 0.3);
            planDatumColumn.setPrefWidth(tableWidth * 0.2);
            planTijdstipColumn.setPrefWidth(tableWidth * 0.2);
        });
    }

    private void populateEmailComboBox() {
        // Retrieve all email records from the medewerkergegevens table
        List<MedewerkerGegevensModel> medewerkerGegevens = medewerkerGegevensDB.getAllMedewerkers();

        // Extract email addresses from the medewerker gegevens models
        List<String> emailStrings = medewerkerGegevens.stream()
                .map(MedewerkerGegevensModel::getEmail)
                .collect(Collectors.toList());

        // Convert the list of email addresses to an observable list
        ObservableList<String> emailList = FXCollections.observableArrayList(emailStrings);

        // Set the items of the ComboBox to the observable list of email addresses
        emailComboBox.setItems(emailList);
    }

    private void populateMedewerkerTableView() {
        // Create columns for the medewerkerTableView
        TableColumn<MedewerkerGegevensModel, String> nameColumn = new TableColumn<>("Naam");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("medewerkerNaam"));

        TableColumn<MedewerkerGegevensModel, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<MedewerkerGegevensModel, String> soortColumn = new TableColumn<>("Soort");
        soortColumn.setCellValueFactory(new PropertyValueFactory<>("medewerkerSoort"));

        // Add the columns to the medewerkerTableView
        medewerkerTableView.getColumns().addAll(nameColumn, emailColumn, soortColumn);

        // Adjust column widths when the table width changes
        medewerkerTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            nameColumn.setPrefWidth(newWidth.doubleValue() * 0.4);
            emailColumn.setPrefWidth(newWidth.doubleValue() * 0.4);
            soortColumn.setPrefWidth(newWidth.doubleValue() * 0.2);
        });

        // Retrieve all medewerkers from the database
        List<MedewerkerGegevensModel> medewerkers = medewerkerGegevensDB.getAllMedewerkers();

        // Convert the list of medewerkers to an observable list
        ObservableList<MedewerkerGegevensModel> data = FXCollections.observableArrayList(medewerkers);

        // Set the items of the medewerkerTableView to the observable list of medewerkers
        medewerkerTableView.setItems(data);
    }

    private void populatePlanningTableView() {
        // Retrieve all plannings for the current bushalte from the database
        List<MedewerkerBushaltePlanningModel> plannings = medewerkerBushaltePlanningDB.getPlanningsByBushalteNaam(bushalte.getHalteNaam());

        // Convert the list of plannings to an observable list
        ObservableList<MedewerkerBushaltePlanningModel> data = FXCollections.observableArrayList(plannings);

        // Set the items of the planningTableView to the observable list of plannings
        planningTableView.setItems(data);
    }

    private void handleAanmakenButton() {
        String email = emailComboBox.getValue();
        LocalDate date = datePicker.getValue();
        Integer hour = hourSpinner.getValue();
        Integer minute = minuteSpinner.getValue();

        if (email != null && date != null && hour != null && minute != null) {
            String planDatum = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String planTijdstip = LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            MedewerkerBushaltePlanningModel planning = new MedewerkerBushaltePlanningModel(email, bushalte.getHalteNaam(), planDatum, planTijdstip);

            if (medewerkerBushaltePlanningDB.checkIfPlanningExists(planning)) {
                showAlert(Alert.AlertType.WARNING, "Waarschuwing", "Planning bestaat al.");
            } else {
                if (medewerkerBushaltePlanningDB.insertPlanning(planning)) {
                    populatePlanningTableView();
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Planning succesvol aangemaakt.");

                    // Send email notification based on medewerkerSoort
                    MedewerkerGegevensModel medewerker = medewerkerGegevensDB.getMedewerkerByEmail(email);
                    if (medewerker != null) {
                        if (medewerker instanceof FreelancerModel) {
                            ((FreelancerModel) medewerker).sendEmailForFreelancer(planning, medewerker);
                        } else if (medewerker instanceof InLoondienstModel) {
                            ((InLoondienstModel) medewerker).sendEmailForInLoondienst(planning, medewerker);
                        } else {
                            System.out.println("Unknown Medewerker type.");
                        }
                    } else {
                        System.out.println("Medewerker not found for email: " + email);
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Fout bij het aanmaken van de planning.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Waarschuwing", "Vul alle velden in.");
        }
    }

    private void handleVerwijderenButton() {
        MedewerkerBushaltePlanningModel selectedPlanning = planningTableView.getSelectionModel().getSelectedItem();
        if (selectedPlanning != null) {
            if (medewerkerBushaltePlanningDB.deletePlanning(selectedPlanning)) {
                populatePlanningTableView();
            }
        }
    }

    private void restrictDatePicker() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}