import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MenuUpdateForm extends JDialog {
    private final Db db;
    private final int menuItemID;
    private final Runnable onUpdateCallback;

    private JTextField nameField;
    private JTextField categoryField;
    private JTextField priceField;

    public MenuUpdateForm(Frame owner, Db db, int menuID, Runnable onUpdateCallback) {
        super(owner, "Update Menu Item", true);
        this.db = db;
        this.menuItemID = menuID;
        this.onUpdateCallback = onUpdateCallback;

        initComponents();
        loadMenuData();
        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("category:"));
        categoryField = new JTextField();
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveMenuItem());
        formPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        formPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadMenuData() {
        String query = "SELECT * FROM Menu WHERE MenuItemID = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, menuItemID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("item_name"));
                categoryField.setText(rs.getString("category"));
                priceField.setText(rs.getString("Price"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load menu item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void saveMenuItem() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String price = priceField.getText().trim();

        if (name.isEmpty() || category.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String updateSQL = "UPDATE Menu SET item_name = ?, category = ?, Price = ? WHERE MenuItemID = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(updateSQL)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, price); // Or use setDouble if Price is numeric in your DB
            stmt.setInt(4, menuItemID);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Menu item updated successfully.");
            dispose();
            if (onUpdateCallback != null) onUpdateCallback.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update menu item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
