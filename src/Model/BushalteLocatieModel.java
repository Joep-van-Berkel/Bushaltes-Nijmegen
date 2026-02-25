package Model;

public class BushalteLocatieModel {
    private String halteNaam;
    private boolean rolstoelToegankelijk;
    private double[] coordinaten;

    public BushalteLocatieModel(String halteNaam, boolean rolstoelToegankelijk, double[] coordinaten) {
        this.halteNaam = halteNaam;
        this.rolstoelToegankelijk = rolstoelToegankelijk;
        this.coordinaten = coordinaten;

        System.out.println(halteNaam + " at " + coordinaten[0] + " " + coordinaten[1] + " is rolstoeltoegankelijk: " + rolstoelToegankelijk);
    }

    public String getHalteNaam() {
        return halteNaam;
    }

    public void setHalteNaam(String halteNaam) {
        this.halteNaam = halteNaam;
    }

    public boolean getRolstoelToegankelijk() {
        return rolstoelToegankelijk;
    }

    public void setRolstoelToegankelijk(boolean rolstoelToegankelijk) {
        this.rolstoelToegankelijk = rolstoelToegankelijk;
    }

    public double[] getCoordinaten() {
        return coordinaten;
    }

    public void setCoordinaten(double[] coordinaten) {
        this.coordinaten = coordinaten;
    }
}