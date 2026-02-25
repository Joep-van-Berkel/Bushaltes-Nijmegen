// InLoondienstModel.java
package Model;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class InLoondienstModel extends MedewerkerGegevensModel {

    public InLoondienstModel(String medewerkerNaam, String email, String medewerkerSoort) {
        super(medewerkerNaam, email, medewerkerSoort);
    }

    public void sendEmailForInLoondienst(MedewerkerBushaltePlanningModel planning, MedewerkerGegevensModel medewerker) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate planDatum = LocalDate.parse(planning.getPlanDatum(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime planTijdstip = LocalTime.parse(planning.getPlanTijdstip(), DateTimeFormatter.ofPattern("HH:mm:ss"));

        String subject = "Planning " + planDatum.format(dateFormatter);
        String body = "Beste " + medewerker.getMedewerkerNaam() + ",\n\n" +
                "Bij deze de planning voor " + planDatum.format(dateFormatter) + ":\n" +
                "U wordt verwacht om " + planTijdstip.format(timeFormatter) + " op de halte: " + planning.getBushalteNaam() + ".\n\n" +
                "Met vriendelijke groet,\n" +
                "Joep van Berkel\n\n" +
                "Personeelplanner bij Breng";
        sendEmail(medewerker.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        String mailto = "mailto:" + to + "?subject=" + encodeURIComponent(subject) + "&body=" + encodeURIComponent(body);
        try {
            Desktop.getDesktop().mail(new URI(mailto));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (Exception e) {
            return value;
        }
    }
}