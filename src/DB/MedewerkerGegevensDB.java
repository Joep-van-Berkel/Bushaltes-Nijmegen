package DB;

import Model.FreelancerModel;
import Model.InLoondienstModel;
import Model.MedewerkerGegevensModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedewerkerGegevensDB {

    private Connection conn;

    public MedewerkerGegevensDB() {
        this.conn = DBConn.instance.conn;
    }

    public boolean insertMedewerker(MedewerkerGegevensModel medewerker) {
        String sql = "INSERT INTO medewerkergegevens (medewerkerNaam, email, medewerkerSoort) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medewerker.getMedewerkerNaam());
            pstmt.setString(2, medewerker.getEmail());
            pstmt.setString(3, medewerker.getMedewerkerSoort());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error code for MySQL
                return false;
            } else {
                e.printStackTrace();
                return false;
            }
        }
    }

    public List<MedewerkerGegevensModel> getAllMedewerkers() {
        List<MedewerkerGegevensModel> medewerkerList = new ArrayList<>();

        String sql = "SELECT * FROM medewerkergegevens ORDER BY medewerkerNaam asc";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String medewerkerNaam = rs.getString("medewerkerNaam");
                String email = rs.getString("email");
                String medewerkerSoort = rs.getString("medewerkerSoort");
                MedewerkerGegevensModel medewerker = new MedewerkerGegevensModel(medewerkerNaam, email, medewerkerSoort);
                medewerkerList.add(medewerker);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medewerkerList;
    }

    public boolean deleteMedewerker(MedewerkerGegevensModel medewerker) {
        String sql = "DELETE FROM medewerkergegevens WHERE medewerkerNaam = ? AND email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medewerker.getMedewerkerNaam());
            pstmt.setString(2, medewerker.getEmail());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MedewerkerGegevensModel getMedewerkerByEmail(String email) {
        String sql = "SELECT * FROM medewerkergegevens WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String medewerkerNaam = rs.getString("medewerkerNaam");
                    String medewerkerSoort = rs.getString("medewerkerSoort").trim();
                    if (medewerkerSoort.equals("Freelancer")) {
                        return new FreelancerModel(medewerkerNaam, email, medewerkerSoort);
                    } else if (medewerkerSoort.equals("In loondienst")) {
                        return new InLoondienstModel(medewerkerNaam, email, medewerkerSoort);
                    } else {
                        System.out.println("Unknown medewerkerSoort: " + medewerkerSoort);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}