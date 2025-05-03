import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class SQLInterface extends JFrame {

    private JTextArea queryArea;
    private JTable resultTable;
    private JButton executeButton;
    private JButton clearButton;
    private JLabel statusLabel;

    private Db db;

    public SQLInterface() {
        setTitle("MySQL SQL Interface");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        initDatabase();
    }

    private void initUI() {
        queryArea = new JTextArea(5, 50);
        resultTable = new JTable();
        executeButton = new JButton("Execute");
        clearButton = new JButton("Clear");

        statusLabel = new JLabel("Status: Ready");

        JScrollPane queryScroll = new JScrollPane(queryArea);
        JScrollPane tableScroll = new JScrollPane(resultTable);

        executeButton.addActionListener(this::executeQuery);
        clearButton.addActionListener(e -> {
            queryArea.setText("");
            resultTable.setModel(new DefaultTableModel());
            statusLabel.setText("Status: Cleared");
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(queryScroll, BorderLayout.CENTER);
        topPanel.add(executeButton, BorderLayout.EAST);
        topPanel.add(clearButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        add(clearButton, BorderLayout.EAST);
    }

    private void initDatabase() {
        db = new Db();
        db.connect();
        try {
            if (db.getConnection() == null || db.getConnection().isClosed()) {
                showError("Failed to connect to database");
                System.exit(1);
            }
        } catch (SQLException e) {
            showError("Database connection error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void executeQuery(ActionEvent event) {
        String sql = queryArea.getText().trim();

        if (sql.isEmpty()) {
            showError("Please enter a query.");
            return;
        }

        try {
            if (sql.toLowerCase().startsWith("select")) {
                ResultSet rs = db.runQuery(sql);
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                String[] columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = meta.getColumnName(i + 1);
                }

                DefaultTableModel model = new DefaultTableModel(columnNames, 0);
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    model.addRow(row);
                }

                resultTable.setModel(model);
                statusLabel.setText("Status: Query executed successfully.");
            } else {
                showError("Only SELECT queries are supported in this UI.");
            }
        } catch (SQLException e) {
            showError("SQL Error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText("Status: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SQLInterface gui = new SQLInterface();
            gui.setVisible(true);
        });
    }
}
