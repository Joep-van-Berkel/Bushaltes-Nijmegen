package Model;

import DB.MedewerkerBushaltePlanningDB;

public class MedewerkerBushaltePlanningModel {

    private String email;
    private String bushalteNaam;
    private String planDatum;
    private String planTijdstip;

    public MedewerkerBushaltePlanningModel(String email, String bushalteNaam, String planDatum, String planTijdstip){
        this.email = email;
        this.bushalteNaam = bushalteNaam;
        this.planDatum = planDatum;
        this.planTijdstip = planTijdstip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBushalteNaam() {
        return bushalteNaam;
    }

    public void setBushalteNaam(String bushalteNaam) {
        this.bushalteNaam = bushalteNaam;
    }

    public String getPlanDatum() {
        return planDatum;
    }

    public void setPlanDatum(String planDatum) {
        this.planDatum = planDatum;
    }

    public String getPlanTijdstip() {
        return planTijdstip;
    }

    public void setPlanTijdstip(String planTijdstip) {
        this.planTijdstip = planTijdstip;
    }
}
