package Model;

public class MedewerkerModel {

    private String medewerkerNaam;

    public MedewerkerModel(String medewerkerNaam){
        this.medewerkerNaam = medewerkerNaam;
    }

    public String getMedewerkerNaam() {
        return medewerkerNaam;
    }

    public void setMedewerkerNaam(String medewerkerNaam) {
        this.medewerkerNaam = medewerkerNaam;
    }

    @Override
    public String toString() {
        return medewerkerNaam;
    }
}
