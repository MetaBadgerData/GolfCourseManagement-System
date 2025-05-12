import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.TableView.TableRow;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class GolferUpdateForm extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField membershipStatusField;
    private JButton updateButton;
    private int golferID;
    private Db db;

    public GolferUpdateForm(JFrame parent, Db db, int golferID, Runnable onUpdate) {
        this.db = db;
        this.golferID = golferID;

        setTitle("Update Golfer");
        setSize(300, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);  // Center form on parent window

        // Retrieve current golfer data from DB
        Golfer golfer = getGolferData(golferID);

        // Create form fields
        firstNameField = new JTextField(golfer.getFirstName());
        lastNameField = new JTextField(golfer.getLastName());
        membershipStatusField = new JTextField(golfer.getMembershipStatus());

        updateButton = new JButton("Update");
        updateButton.addActionListener((ActionEvent e) -> {
            updateGolfer();
            onUpdate.run();  // Refresh the main table after updating
            dispose();  // Close the form
        });

        // Layout
        setLayout(new GridLayout(5, 2));
        add(new JLabel("First Name:"));
        add(firstNameField);
        add(new JLabel("Last Name:"));
        add(lastNameField);
        add(new JLabel("Membership Status:"));
        add(membershipStatusField);
        add(new JLabel());
        add(updateButton);
    }

    private Golfer getGolferData(int golferID) {
        // Query to get golfer details from DB
        String query = "SELECT * FROM Golfers WHERE GolferID = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(query)) {
            ps.setInt(1, golferID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Golfer(
                        rs.getInt("GolferID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("MembershipStatus")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;  // Return null if no data is found
    }

    private void updateGolfer() {
        // Update the golfer in the database
        String query = "UPDATE Golfers SET FirstName = ?, LastName = ?, MembershipStatus = ? WHERE GolferID = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(query)) {
            ps.setString(1, firstNameField.getText());
            ps.setString(2, lastNameField.getText());
            ps.setString(3, membershipStatusField.getText());
            ps.setInt(4, golferID);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Golfer updated successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating golfer.");
        }
    }
}