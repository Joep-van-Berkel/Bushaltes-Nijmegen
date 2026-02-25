// In MedewerkerGegevensModel.java
package Model;

public class MedewerkerGegevensModel {
    private String medewerkerNaam;
    private String email;
    private String medewerkerSoort;

    public MedewerkerGegevensModel(String medewerkerNaam, String email, String medewerkerSoort) {
        this.medewerkerNaam = medewerkerNaam;
        this.email = email;
        this.medewerkerSoort = medewerkerSoort;
    }

    public String getMedewerkerNaam() {
        return medewerkerNaam;
    }

    public void setMedewerkerNaam(String medewerkerNaam) {
        this.medewerkerNaam = medewerkerNaam;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMedewerkerSoort() {
        return medewerkerSoort;
    }

    public void setMedewerkerSoort(String medewerkerSoort) {
        this.medewerkerSoort = medewerkerSoort;
    }
}