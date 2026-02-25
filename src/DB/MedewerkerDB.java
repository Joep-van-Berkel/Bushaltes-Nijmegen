package DB;

import Model.MedewerkerModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedewerkerDB {

    private Connection conn;

    public MedewerkerDB() {
        this.conn = DBConn.instance.conn;
    }

    public boolean insertRecord(MedewerkerModel medewerker) {
        if (checkIfRecordExists(medewerker.getMedewerkerNaam())) {
            return false;
        }
        String query = "INSERT INTO medewerkers (medewerkerNaam) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, medewerker.getMedewerkerNaam());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MedewerkerModel> selectAllRecords() {
        List<MedewerkerModel> medewerkers = new ArrayList<>();
        String query = "SELECT * FROM medewerkers";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                medewerkers.add(new MedewerkerModel(rs.getString("medewerkerNaam")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medewerkers;
    }

    public boolean updateRecord(MedewerkerModel oldMedewerker, MedewerkerModel newMedewerker) {
        if (!checkIfRecordExists(oldMedewerker.getMedewerkerNaam())) {
            System.out.println("Old medewerker does not exist in medewerkers table.");
            return false;
        }
        try {
            // Disable foreign key checks
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
                stmt.executeUpdate();
            }

            // Update dependent records in medewerkergegevens table
            String updateMedewerkergegevensQuery = "UPDATE medewerkergegevens SET medewerkerNaam = ? WHERE medewerkerNaam = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateMedewerkergegevensQuery)) {
                pstmt.setString(1, newMedewerker.getMedewerkerNaam());
                pstmt.setString(2, oldMedewerker.getMedewerkerNaam());
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Updated medewerkergegevens rows: " + rowsAffected);
            }

            // Update the record in medewerkers table
            String query = "UPDATE medewerkers SET medewerkerNaam = ? WHERE medewerkerNaam = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newMedewerker.getMedewerkerNaam());
                stmt.setString(2, oldMedewerker.getMedewerkerNaam());
                stmt.executeUpdate();
            }

            // Re-enable foreign key checks
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
                stmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRecord(MedewerkerModel medewerker) {
        try {
            // Disable foreign key checks
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
                stmt.executeUpdate();
            }

            // Delete dependent records in medewerkergegevens table
            String deleteMedewerkergegevensQuery = "DELETE FROM medewerkergegevens WHERE medewerkerNaam = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteMedewerkergegevensQuery)) {
                pstmt.setString(1, medewerker.getMedewerkerNaam());
                pstmt.executeUpdate();
            }

            // Delete the record in medewerkers table
            String query = "DELETE FROM medewerkers WHERE medewerkerNaam = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, medewerker.getMedewerkerNaam());
                stmt.executeUpdate();
            }

            // Re-enable foreign key checks
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
                stmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkIfRecordExists(String naam) {
        String query = "SELECT 1 FROM medewerkers WHERE medewerkerNaam = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, naam);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}