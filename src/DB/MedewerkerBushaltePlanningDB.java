package DB;

import Model.MedewerkerBushaltePlanningModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedewerkerBushaltePlanningDB {

    private Connection conn;

    public MedewerkerBushaltePlanningDB() {
        this.conn = DBConn.instance.conn;
    }

    // Method to add a new MedewerkerBushaltePlanning
    public boolean insertPlanning(MedewerkerBushaltePlanningModel planning) {
        String sql = "INSERT INTO MedewerkerBushaltePlanning (email, bushalteNaam, planDatum, planTijdstip) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, planning.getEmail());
            pstmt.setString(2, planning.getBushalteNaam());
            pstmt.setString(3, planning.getPlanDatum());
            pstmt.setString(4, planning.getPlanTijdstip());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // retrieve planning for specific bushalte
    public List<MedewerkerBushaltePlanningModel> getPlanningsByBushalteNaam(String bushalteNaam) {
        List<MedewerkerBushaltePlanningModel> planningList = new ArrayList<>();
        String sql = "SELECT * FROM MedewerkerBushaltePlanning WHERE bushalteNaam = ? ORDER BY planDatum, planTijdstip ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bushalteNaam);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("email");
                    String planDatum = rs.getString("planDatum");
                    String planTijdstip = rs.getString("planTijdstip");
                    MedewerkerBushaltePlanningModel planning = new MedewerkerBushaltePlanningModel(email, bushalteNaam, planDatum, planTijdstip);
                    planningList.add(planning);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planningList;
    }

    // Method to delete a MedewerkerBushaltePlanning by email, bushalteNaam, planDatum, and planTijdstip
    public boolean deletePlanning(MedewerkerBushaltePlanningModel planning) {
        String sql = "DELETE FROM MedewerkerBushaltePlanning WHERE email = ? AND bushalteNaam = ? AND planDatum = ? AND planTijdstip = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, planning.getEmail());
            pstmt.setString(2, planning.getBushalteNaam());
            pstmt.setString(3, planning.getPlanDatum());
            pstmt.setString(4, planning.getPlanTijdstip());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkIfPlanningExists(MedewerkerBushaltePlanningModel planning) {
        String sql = "SELECT COUNT(*) FROM MedewerkerBushaltePlanning WHERE email = ? AND bushalteNaam = ? AND planDatum = ? AND planTijdstip = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, planning.getEmail());
            pstmt.setString(2, planning.getBushalteNaam());
            pstmt.setString(3, planning.getPlanDatum());
            pstmt.setString(4, planning.getPlanTijdstip());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}