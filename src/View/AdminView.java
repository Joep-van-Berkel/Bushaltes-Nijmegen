package View;

import DB.EmailDB;
import DB.MedewerkerDB;
import Model.EmailModel;
import Model.MedewerkerModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminView extends BorderPane {

    private static final double LISTVIEW_WIDTH = 350;
    private static final double BUTTON_WIDTH = 150;
    private static final double TEXTFIELD_WIDTH = BUTTON_WIDTH * 3 + 20;
    private static final Font LABEL_FONT = Font.font("System", FontWeight.BOLD, 22);

    private EmailDB emailDB;
    private MedewerkerDB medewerkerDB;
    private ObservableList<EmailModel> emailItems;
    private ObservableList<MedewerkerModel> medewerkerItems;

    public AdminView() {
        emailDB = new EmailDB();
        medewerkerDB = new MedewerkerDB();
        emailItems = FXCollections.observableArrayList();
        medewerkerItems = FXCollections.observableArrayList();

        ListView<EmailModel> emailList = new ListView<>(emailItems);
        emailList.setPrefWidth(LISTVIEW_WIDTH);
        ListView<MedewerkerModel> naamList = new ListView<>(medewerkerItems);
        naamList.setPrefWidth(LISTVIEW_WIDTH);

        loadEmails();
        loadMedewerkers();

        TextField emailField = createTextField("Vul hier een nieuwe email in...");
        TextField naamField = createTextField("Vul hier een nieuwe naam in...");

        Button emailAanmakenButton = createButton("Aanmaken");
        Button emailVerwijderenButton = createButton("Verwijderen");
        Button emailBewerkenButton = createButton("Bewerken");

        Button naamAanmakenButton = createButton("Aanmaken");
        Button naamVerwijderenButton = createButton("Verwijderen");
        Button naamBewerkenButton = createButton("Bewerken");

        emailAanmakenButton.setOnAction(e -> handleEmailAanmaken(emailField));
        emailVerwijderenButton.setOnAction(e -> handleEmailVerwijderen(emailList));
        emailBewerkenButton.setOnAction(e -> handleEmailBewerken(emailList, emailField));

        naamAanmakenButton.setOnAction(e -> handleNaamAanmaken(naamField));
        naamVerwijderenButton.setOnAction(e -> handleNaamVerwijderen(naamList));
        naamBewerkenButton.setOnAction(e -> handleNaamBewerken(naamList, naamField));

        HBox emailButtons = new HBox(10, emailAanmakenButton, emailVerwijderenButton, emailBewerkenButton);
        emailField.setPrefWidth(TEXTFIELD_WIDTH);

        HBox naamButtons = new HBox(10, naamAanmakenButton, naamVerwijderenButton, naamBewerkenButton);
        naamField.setPrefWidth(TEXTFIELD_WIDTH);

        VBox emailBox = createVBox(createLabel("Email identificatie"), emailField, emailButtons);
        VBox naamBox = createVBox(createLabel("Naam identificatie"), naamField, naamButtons);

        HBox emailSection = new HBox(10, emailBox, emailList);
        emailSection.setAlignment(Pos.TOP_LEFT);

        HBox naamSection = new HBox(10, naamBox, naamList);
        naamSection.setAlignment(Pos.TOP_LEFT);

        Separator separator = new Separator();
        separator.setPadding(new Insets(20, 0, 20, 0));

        VBox listViewBox = new VBox(20, emailSection, separator, naamSection);
        listViewBox.setPadding(new Insets(20));

        HBox mainBox = new HBox(20, listViewBox);
        mainBox.setPadding(new Insets(20));
        mainBox.setAlignment(Pos.CENTER);

        this.setCenter(mainBox);

        Platform.runLater(() -> mainBox.requestFocus());
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(BUTTON_WIDTH);
        return button;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    private VBox createVBox(Label label, TextField textField, HBox buttons) {
        VBox vBox = new VBox(10, label, textField, buttons);
        vBox.setPadding(new Insets(20));
        return vBox;
    }

    private void handleEmailAanmaken(TextField emailField) {
        String email = emailField.getText();
        if (!email.isEmpty()) {
            EmailModel emailModel = new EmailModel(email);
            if (emailDB.insertRecord(emailModel)) {
                emailItems.add(emailModel);
                emailField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Email succesvol aangemaakt.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Email bestaat al!");
            }
        }
    }

    private void handleEmailVerwijderen(ListView<EmailModel> emailList) {
        EmailModel selectedEmail = emailList.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            if (emailDB.deleteRecord(selectedEmail)) {
                emailItems.remove(selectedEmail);
            }
        }
    }

    private void handleEmailBewerken(ListView<EmailModel> emailList, TextField emailField) {
        EmailModel selectedEmail = emailList.getSelectionModel().getSelectedItem();
        String newEmail = emailField.getText();
        if (selectedEmail != null && !newEmail.isEmpty()) {
            if (emailDB.checkIfRecordExists(newEmail)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Email bestaat al!");
            } else {
                EmailModel newEmailModel = new EmailModel(newEmail);
                if (emailDB.updateRecord(selectedEmail, newEmailModel)) {
                    emailItems.set(emailItems.indexOf(selectedEmail), newEmailModel);
                    emailField.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Email succesvol bijgewerkt.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Bijwerken email mislukt.");
                }
            }
        }
    }

    private void handleNaamAanmaken(TextField naamField) {
        String naam = naamField.getText();
        if (!naam.isEmpty()) {
            MedewerkerModel medewerkerModel = new MedewerkerModel(naam);
            if (medewerkerDB.insertRecord(medewerkerModel)) {
                medewerkerItems.add(medewerkerModel);
                naamField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Medewerker succesvol aangemaakt.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Medewerker bestaat al!");
            }
        }
    }

    private void handleNaamVerwijderen(ListView<MedewerkerModel> naamList) {
        MedewerkerModel selectedNaam = naamList.getSelectionModel().getSelectedItem();
        if (selectedNaam != null) {
            if (medewerkerDB.deleteRecord(selectedNaam)) {
                medewerkerItems.remove(selectedNaam);
            }
        }
    }

    private void handleNaamBewerken(ListView<MedewerkerModel> naamList, TextField naamField) {
        MedewerkerModel selectedNaam = naamList.getSelectionModel().getSelectedItem();
        String newNaam = naamField.getText();
        if (selectedNaam != null && !newNaam.isEmpty()) {
            if (medewerkerDB.checkIfRecordExists(newNaam)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Medewerker bestaat al!");
            } else {
                MedewerkerModel newMedewerkerModel = new MedewerkerModel(newNaam);
                if (medewerkerDB.updateRecord(selectedNaam, newMedewerkerModel)) {
                    medewerkerItems.set(medewerkerItems.indexOf(selectedNaam), newMedewerkerModel);
                    naamField.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Medewerker succesvol bijgewerkt.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Bijwerken medewerker mislukt.");
                }
            }
        }
    }

    private void loadEmails() {
        emailItems.clear();
        emailItems.addAll(emailDB.selectAllRecords());
    }

    private void loadMedewerkers() {
        medewerkerItems.clear();
        medewerkerItems.addAll(medewerkerDB.selectAllRecords());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}