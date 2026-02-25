package DB;

import Model.EmailModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailDB {

    private Connection conn;

    public EmailDB() {
        this.conn = DBConn.instance.conn;
    }

    public boolean insertRecord(EmailModel email) {
        if (checkIfRecordExists(email.getEmail())) {
            return false;
        }
        String query = "INSERT INTO emails (email) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email.getEmail());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<EmailModel> selectAllRecords() {
        List<EmailModel> emails = new ArrayList<>();
        String query = "SELECT * FROM emails";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                emails.add(new EmailModel(rs.getString("email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }

    public boolean updateRecord(EmailModel oldEmail, EmailModel newEmail) {
        if (!checkIfRecordExists(oldEmail.getEmail())) {
            System.out.println("Old email does not exist in emails table.");
            return false;
        }
        try {
            // Disable foreign key checks
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
                stmt.executeUpdate();
            }

            // Update dependent records in medewerkerbushalteplanning table
            String updateDependentRecordsQuery = "UPDATE medewerkerbushalteplanning SET email = ? WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateDependentRecordsQuery)) {
                pstmt.setString(1, newEmail.getEmail());
                pstmt.setString(2, oldEmail.getEmail());
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Updated medewerkerbushalteplanning rows: " + rowsAffected);
            }

            // Update dependent records in medewerkergegevens table
            String updateMedewerkergegevensQuery = "UPDATE medewerkergegevens SET email = ? WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateMedewerkergegevensQuery)) {
                pstmt.setString(1, newEmail.getEmail());
                pstmt.setString(2, oldEmail.getEmail());
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Updated medewerkergegevens rows: " + rowsAffected);
            }

            // Update the record in emails table
            String query = "UPDATE emails SET email = ? WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newEmail.getEmail());
                stmt.setString(2, oldEmail.getEmail());
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

    public boolean deleteRecord(EmailModel email) {
        try {
            // Disable foreign key checks
            try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
                stmt.executeUpdate();
            }

            // Delete dependent records in medewerkerbushalteplanning table
            String deleteDependentRecordsQuery = "DELETE FROM medewerkerbushalteplanning WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteDependentRecordsQuery)) {
                pstmt.setString(1, email.getEmail());
                pstmt.executeUpdate();
            }

            // Delete dependent records in medewerkergegevens table
            String deleteMedewerkergegevensQuery = "DELETE FROM medewerkergegevens WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteMedewerkergegevensQuery)) {
                pstmt.setString(1, email.getEmail());
                pstmt.executeUpdate();
            }

            // Delete the record in emails table
            String query = "DELETE FROM emails WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email.getEmail());
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

    public boolean checkIfRecordExists(String email) {
        String query = "SELECT 1 FROM emails WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}