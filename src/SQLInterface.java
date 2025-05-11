import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.TableView.TableRow;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.lang.Integer;

public class SQLInterface extends JFrame {
    private JTable golferTable;
    private JTable resultTable;
    private JLabel statusLabel;
    private JTextField searchField;
    private JButton addReservationButton;
    private JButton addGolferButton;
    private JButton addStaffButton;
    private JButton addMenuButton;
    private JButton removeButton;
    private JButton removeGolferButton;
    private JButton removeStaffButton;
    private JButton updateButton;
    private JButton updateReservationButton;
    private JButton updateGolferButton;
    private JButton totalReservationsButton;
    private JButton habitsButton;
    private JButton lowSpendingButton;
    private JButton searchButton;
    private JButton golfCoursesButton;
    private JButton menuButton;
    private JButton courseRevenueButton;
    private JButton providesButton;
    private JButton playsButton;
    private JButton golfersButton;
    private JButton buysButton;
    private JButton staffButton;
    private JButton reservationsButton;
    private JButton worstBeverageButton;
    private JButton inactiveGolferButton;
    private JButton mostPopFoodButton;
    private JButton avgReservationCountButton;
    private JButton reservationLeaderButton;
    private JButton home;
    private TableRowSorter<DefaultTableModel> sorter;

    private JPanel queryButtonsPanel;
    private JPanel topPanel;

    private Db db;

    private static final String INACTIVE_GOLFERS_QUERY = "SELECT Golfers.GolferID, Golfers.FirstName, Golfers.LastName, Golfers.MembershipStatus "
            +
            "FROM Golfers " +
            "WHERE Golfers.golferID NOT IN (SELECT GolferID FROM Reservations)";

    private static final String COURSE_REVENUE_QUERY = "SELECT GolfCourses.courseId, GolfCourses.numOfHoles, SUM(Reservations.totalCost) AS totalRevenue "
            +
            "FROM GolfCourses " +
            "JOIN Reservations ON GolfCourses.courseId = Reservations.courseId " +
            "GROUP BY GolfCourses.courseId, GolfCourses.numOfHoles " +
            "ORDER BY totalRevenue DESC";

    private static final String WORST_BEVERAGE_QUERY = "SELECT Menu.MenuItemID, Menu.item_name, COUNT(Buys.MenuItemID) AS purchaseCount "
            +
            "FROM Menu " +
            "JOIN Buys ON Menu.MenuItemID = Buys.MenuItemID " +
            "WHERE Menu.category = 'Drinks' " +
            "GROUP BY Menu.MenuItemID, Menu.item_name " +
            "ORDER BY purchaseCount ASC " +
            "LIMIT 1";
    private static final String TOTAL_RESERVATIONS_QUERY = " SELECT CourseID, COUNT(*) AS totalReservations " +
            "FROM Reservations " +
            "GROUP BY CourseID " +
            "ORDER BY totalReservations DESC " + 
            "LIMIT 5";
    
    private static final String TOTAL_SPENDING_RESERVATION_QUERY = "SELECT  g.GolferID, g.FirstName, g.LastName, COUNT(r.ReservationID) AS NumReservations, SUM(r.TotalCost) AS TotalSpent " + 
            "FROM Golfers g " +
            "LEFT JOIN Reservations r ON g.GolferID = r.GolferID " +
            "GROUP BY g.GolferID, g.FirstName, g.LastName " +
            "ORDER BY TotalSpent DESC;";

    private static final String FREE_LOADER_QUERY = "SELECT g.GolferID, g.FirstName, g.LastName "
            +
            "FROM Golfers g " +
            "WHERE g.GolferID IN (SELECT DISTINCT r.GolferID FROM Reservations r WHERE r.GolferID IS NOT NULL) " +
            "AND g.GolferID NOT IN (SELECT DISTINCT b.GolferID FROM Buys b)";

    private static final String MOST_POPULAR_FOOD_QUERY = "SELECT Menu.item_name, COUNT(*) AS PurchaseCount " +
            "FROM Buys JOIN Menu ON Buys.MenuItemID = Menu.MenuItemID " +
            "WHERE Menu.MenuItemID " +
            "GROUP BY Menu.item_name " +
            "ORDER BY PurchaseCount DESC " +
            "LIMIT 1";
    
    private static final String AVG_RESERVATION_COUNT_QUERY = "SELECT AVG(ReservationCount) AS AvgReservationsPerGolfer " +
            "FROM ( Select g.GolferID, COUNT(r.ReservationID) AS ReservationCount " +
            "FROM Golfers g " +
            "LEFT JOIN Reservations r ON g.GolferID = r.GolferID " +
            "GROUP BY g.GolferID " + 
            ") AS GolferReservationCounts;";

    private static final String RESERVATION_LEADER_QUERY = "SELECT g.GolferID, g.FirstName, g.LastName, COUNT(r.ReservationID) AS ReservationCount " +
            "FROM Golfers g " +
            "LEFT JOIN Reservations r ON g.GolferID = r.GolferID " + 
            "GROUP BY g.GolferID, g.FirstName, g.LastName " +
            "HAVING COUNT(r.ReservationID) > ( " + 
            "SELECT AVG(ReservationCount) AS AvgReservationsPerGolfer " +
            "FROM ( Select g.GolferID, COUNT(r.ReservationID) AS ReservationCount " +
            "FROM Golfers g " +
            "LEFT JOIN Reservations r ON g.GolferID = r.GolferID " +
            "GROUP BY g.GolferID " +
            ") AS GolferReservationCounts " +
            ");";


    public SQLInterface() {
        setTitle("MySQL SQL Interface");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        initDatabase();
    }

    private void initUI() {
        resultTable = new JTable();
        statusLabel = new JLabel("Status: Ready"); 
        totalReservationsButton = new JButton("Total Reservations");
        habitsButton = new JButton("Habits");
        lowSpendingButton = new JButton("Low_spending_poopy_golfers");
        golfersButton = new JButton("Golfers");
        golfCoursesButton = new JButton("Golf Courses");
        buysButton = new JButton("Buys");
        reservationsButton = new JButton("Reservations");
        staffButton = new JButton("Staff");
        providesButton = new JButton("Provides");
        playsButton = new JButton("Plays");
        courseRevenueButton = new JButton("Show Course Revenue");
        mostPopFoodButton = new JButton("Most Popular Food");
        worstBeverageButton = new JButton("Find Worst Beverage");
        inactiveGolferButton = new JButton("Show Inactive Golfers");
        avgReservationCountButton = new JButton("Avg Reservation Count");
        reservationLeaderButton = new JButton("Reservation Leader");
        menuButton = new JButton("Menu");
        home = new JButton("Home");

        searchField = new JTextField(10);
        searchField.setPreferredSize(new Dimension(180, 30));
        searchField.setMaximumSize(new Dimension(180, 30));
        searchButton = new JButton("Search");

        addReservationButton = new JButton("Add Reservation");
        addReservationButton.setVisible(false);
        addGolferButton = new JButton("Add Golfer");
        addGolferButton.setVisible(false);
        addStaffButton = new JButton("Add Staff");
        addStaffButton.setVisible(false);
        addMenuButton = new JButton("Add Menu");
        addMenuButton.setVisible(false);

        removeButton = new JButton("Remove");
        removeGolferButton = new JButton("Remove Golfer");
        removeGolferButton.setVisible(false);
        removeStaffButton = new JButton("Remove staff");
        removeStaffButton.setVisible(false);

        updateButton = new JButton("Update");
        updateReservationButton = new JButton("Update Reservation");
        updateReservationButton.setVisible(false);
        updateGolferButton = new JButton("Update Golfer");
        updateGolferButton.setVisible(false);

            
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            if (sorter != null && searchText != null && !searchText.trim().isEmpty()) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                statusLabel.setText("Status: Filter applied");
            } else if (sorter != null) {
                sorter.setRowFilter(null);
                statusLabel.setText("Status: Filter cleared");
            }
        });

        addReservationButton.addActionListener(e -> {
            new ReservationForm(this, db, () -> executeQuery("SELECT * FROM Reservations")).setVisible(true);
        });

        addGolferButton.addActionListener(e -> {
            new GolferForm(this, db, () -> executeQuery("SELECT * FROM Golfers")).setVisible(true);
        });

        addStaffButton.addActionListener(e -> {
            new StaffForm(this, db, () -> executeQuery("SELECT * FROM Staff")).setVisible(true);
        });

        addMenuButton.addActionListener(e -> {
            new MenuItemForm(this, db, () -> executeQuery("SELECT * FROM Menu")).setVisible(true);
        });

        removeButton.addActionListener(e -> {
            statusLabel.setText("Status: Remove button clicked");
        });

        removeGolferButton.addActionListener(e -> {
            Integer selectedGolferID = getSelectedGolferID();
            if (selectedGolferID == null) {
                JOptionPane.showMessageDialog(this, "Please select a golfer to remove.");
                return;
            }

            String sql = "DELETE FROM Golfers WHERE GolferID = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, selectedGolferID);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Golfer removed successfully!");
                    executeQuery("SELECT * FROM Golfers");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove golfer.");
                }
            } catch (SQLException ex) {
                showError("SQL Error: " + ex.getMessage());
            }
        });

        removeStaffButton.addActionListener(e -> {
            Integer selectedStaffID = getSelectedStaffID();
            if (selectedStaffID == null) {
                JOptionPane.showMessageDialog(this, "Please select a staff member to remove.");
                return;
            }

            String sql = "DELETE FROM Staff WHERE StaffID = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, selectedStaffID);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Staff member removed successfully!");
                    executeQuery("SELECT * FROM Staff");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove staff member.");
                }
            } catch (SQLException ex) {
                showError("SQL Error: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            statusLabel.setText("Status: Update button clicked");
        });

        updateReservationButton.addActionListener(e -> {
            new UpdateReservationForm(this, db, 1, () -> executeQuery("SELECT * FROM Reservations")).setVisible(true);
        });

        updateGolferButton.addActionListener(e -> {
            Integer selectedGolferID = getSelectedGolferID();
            if (selectedGolferID == null) {
                JOptionPane.showMessageDialog(this, "Please select a golfer to update.");
                return;
            }

            if (selectedGolferID == -1) {
                JOptionPane.showMessageDialog(this, "Please select a golfer to update.");
                return;
            }

            new GolferUpdateForm(this, db, selectedGolferID, () -> executeQuery("SELECT * FROM Golfers")).setVisible(true);
            statusLabel.setText("Status: Update Golfer button clicked");
        });

        // tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // String[] mainTable = {"Golfers", "GolfCourses", "Reservations", "Menu", "Buys"};
        // for (String tableName : mainTable) {
        //     JButton tableButton = new JButton("Show " + tableName);
        //     tableButton.addActionListener(e -> {
        //         queryArea.setText("SELECT * FROM " + tableName);
        //         executeQuery(new ActionEvent(e.getSource(), e.getID(), "Execute"));
        //     });
        //     tableButtonsPanel.add(tableButton);
        // }

        JScrollPane tableScroll = new JScrollPane(resultTable);


        golfersButton.addActionListener(e -> {
            removeStaffButton.setVisible(false);
            removeGolferButton.setVisible(true);
            updateButton.setVisible(false);
            updateGolferButton.setVisible(true);
            updateReservationButton.setVisible(false);
            addMenuButton.setVisible(false);
            addStaffButton.setVisible(false);
            addReservationButton.setVisible(false);
            addGolferButton.setVisible(true);
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Golfers");
        });

        staffButton.addActionListener(e -> {
            removeStaffButton.setVisible(true);
            removeGolferButton.setVisible(false);
            updateGolferButton.setVisible(false);
            updateReservationButton.setVisible(false);
            addMenuButton.setVisible(false);
            addStaffButton.setVisible(true);
            addGolferButton.setVisible(false);
            addReservationButton.setVisible(false);
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Staff");
        });

        buysButton.addActionListener(e -> {
            removeStaffButton.setVisible(false);
            removeGolferButton.setVisible(false);
            updateGolferButton.setVisible(false);
            updateReservationButton.setVisible(false);
            addMenuButton.setVisible(false);
            addStaffButton.setVisible(false);
            addGolferButton.setVisible(false);
            addReservationButton.setVisible(false);
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Buys");
        });

        providesButton.addActionListener(e -> {
            removeStaffButton.setVisible(false);
            removeGolferButton.setVisible(false);
            updateGolferButton.setVisible(false);
            updateReservationButton.setVisible(false);
            addMenuButton.setVisible(false);
            addStaffButton.setVisible(false);
            addGolferButton.setVisible(false);
            addReservationButton.setVisible(false);
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Provides");
        });

        playsButton.addActionListener(e -> {
            removeStaffButton.setVisible(false);
            removeGolferButton.setVisible(false);
            updateGolferButton.setVisible(false);
            updateReservationButton.setVisible(false);
            addMenuButton.setVisible(false);
            addStaffButton.setVisible(false);
            addGolferButton.setVisible(false);
            addReservationButton.setVisible(false);
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Plays");
        });

        reservationsButton.addActionListener(e -> {
            removeStaffButton.setVisible(false);
            removeGolferButton.setVisible(false);
            updateGolferButton.setVisible(false);
            updateReservationButton.setVisible(true);
            addMenuButton.setVisible(false);
            addStaffButton.setVisible(false);
            addGolferButton.setVisible(false);
            addReservationButton.setVisible(true);
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Reservations");
        });

        golfCoursesButton.addActionListener(e -> {
            removeStaffButton.setVisible(false);
            removeGolferButton.setVisible(false);
            updateGolferButton.setVisible(false);
            updateReservationButton.setVisible(false);
            addMenuButton.setVisible(false);
            addStaffButton.setVisible(false);
            addGolferButton.setVisible(false);
            addReservationButton.setVisible(false);
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM GolfCourses");
        });

        menuButton.addActionListener(e -> {
            removeStaffButton.setVisible(false);
            removeGolferButton.setVisible(false);
            updateGolferButton.setVisible(false);
            updateReservationButton.setVisible(false);
            addMenuButton.setVisible(true);
            addStaffButton.setVisible(false);
            addGolferButton.setVisible(false);
            addReservationButton.setVisible(false);
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Menu");
        });

        avgReservationCountButton.addActionListener(e -> { 
            executeQuery(AVG_RESERVATION_COUNT_QUERY);
        });

        // {
        //     queryButtonsPanel.setVisible(false);
        //     statusLabel.setText("Status: Menu buttons displayed");
        // });


        lowSpendingButton.addActionListener(e -> {
            executeQuery(FREE_LOADER_QUERY);
        });

        habitsButton.addActionListener(e -> {
            executeQuery(TOTAL_SPENDING_RESERVATION_QUERY);
        });

        totalReservationsButton.addActionListener(e -> {
            executeQuery(TOTAL_RESERVATIONS_QUERY);
        });

        inactiveGolferButton.addActionListener(e -> {
            executeQuery(INACTIVE_GOLFERS_QUERY);
        });

        courseRevenueButton.addActionListener(e -> {
            executeQuery(COURSE_REVENUE_QUERY);
        });

        worstBeverageButton.addActionListener(e -> {
            executeQuery(WORST_BEVERAGE_QUERY);
        });

        mostPopFoodButton.addActionListener(e -> {
            executeQuery(MOST_POPULAR_FOOD_QUERY);
        });

        reservationLeaderButton.addActionListener(e -> {
            executeQuery(RESERVATION_LEADER_QUERY);
        });

        home.addActionListener(e -> {
            updateReservationButton.setVisible(false);
            addMenuButton.setVisible(false);
            addStaffButton.setVisible(false);
            addGolferButton.setVisible(false);
            addReservationButton.setVisible(false);
            queryButtonsPanel.setVisible(true);
            statusLabel.setText("Status: Query buttons displayed");
        });

        queryButtonsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        queryButtonsPanel.add(inactiveGolferButton);
        queryButtonsPanel.add(courseRevenueButton);
        queryButtonsPanel.add(avgReservationCountButton);
        queryButtonsPanel.add(reservationLeaderButton);
        queryButtonsPanel.add(worstBeverageButton);
        queryButtonsPanel.add(mostPopFoodButton);
        queryButtonsPanel.add(totalReservationsButton);
        queryButtonsPanel.add(lowSpendingButton); 
        queryButtonsPanel.add(habitsButton);
        queryButtonsPanel.setVisible(false);

        //this is the panel for all of the overall buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(menuButton);
        buttonPanel.add(golfersButton);
        buttonPanel.add(golfCoursesButton);
        buttonPanel.add(staffButton);
        buttonPanel.add(reservationsButton);
        buttonPanel.add(buysButton);
        buttonPanel.add(providesButton);
        buttonPanel.add(playsButton);
        buttonPanel.setVisible(true);

        JScrollPane scrollPane = new JScrollPane(golferTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS)); 
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(Box.createVerticalStrut(10));
        searchPanel.add(new JLabel("Filter Rows:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addReservationButton);
        searchPanel.add(addGolferButton);
        searchPanel.add(addStaffButton);
        searchPanel.add(addMenuButton);
        searchPanel.add(removeButton);
        searchPanel.add(removeGolferButton);
        searchPanel.add(removeStaffButton);
        searchPanel.add(updateButton);
        searchPanel.add(updateReservationButton);
        searchPanel.add(updateGolferButton);

        topPanel = new JPanel(new BorderLayout());
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topButtonPanel.add(home);

        topPanel.add(buttonPanel, BorderLayout.NORTH);
        topPanel.add(topButtonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(queryButtonsPanel, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);
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

    private void executeQuery(String sql) {
        try {
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
            resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            resultTable.setRowSelectionAllowed(true); 
            sorter = new TableRowSorter<>(model);
            resultTable.setRowSorter(sorter);
            statusLabel.setText("Status: Query executed successfully.");
        } catch (SQLException e) {
            showError("SQL Error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText("Status: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public Integer getSelectedGolferID() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }

        int modelRow = resultTable.convertRowIndexToModel(selectedRow); // handles sorting
        Object value = resultTable.getModel().getValueAt(modelRow, 0); // assuming ID is in column 0

        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Integer getSelectedStaffID() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            return null; // No selection
        }
        
        int modelRow = resultTable.convertRowIndexToModel(selectedRow); // handles sorting
        Object value = resultTable.getModel().getValueAt(modelRow, 0);

        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SQLInterface gui = new SQLInterface();
            gui.setVisible(true);
        });
    }
}

class ReservationForm extends JDialog {
    private JTextField golferIdField, courseIdField, dateField, timeField, totalCostField;
    private JButton submitButton;
    private Db db;
    private Runnable onSuccess;

    public ReservationForm(JFrame parent, Db db, Runnable onSuccess) {
        super(parent, "Add Reservation", true);
        this.db = db;
        this.onSuccess = onSuccess;
        setLayout(new GridLayout(6, 2, 5, 5));
        setSize(300, 250);
        setLocationRelativeTo(parent);

        golferIdField = new JTextField();
        courseIdField = new JTextField();
        dateField = new JTextField(); // Format: YYYY-MM-DD
        timeField = new JTextField(); // Format: HH:MM:SS
        totalCostField = new JTextField();

        submitButton = new JButton("Submit");

        add(new JLabel("Golfer ID:"));
        add(golferIdField);
        add(new JLabel("Course ID:"));
        add(courseIdField);
        add(new JLabel("Date (YYYY-MM-DD):"));
        add(dateField);
        add(new JLabel("Time (HH:MM:SS):"));
        add(timeField);
        add(new JLabel("Total Cost:"));
        add(totalCostField);
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(e -> addReservation());
    }

    private void addReservation() {
        try {
            int golferId = Integer.parseInt(golferIdField.getText().trim());
            int courseId = Integer.parseInt(courseIdField.getText().trim());
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            double totalCost = Double.parseDouble(totalCostField.getText().trim());

            String sql = "INSERT INTO Reservations (GolferID, CourseID, Date, Time, TotalCost) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, golferId);
            stmt.setInt(2, courseId);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.setDouble(5, totalCost);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Reservation added successfully!");
                onSuccess.run(); // callback to refresh table
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add reservation.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class GolferForm extends JDialog {
    private JTextField SNameField, lastNameField, membershipStatusField, ageField;
    private JButton submitButton;
    private Db db;
    private Runnable onSuccess;

    public GolferForm(JFrame parent, Db db, Runnable onSuccess) {
        super(parent, "Add Golfer", true);
        this.db = db;
        this.onSuccess = onSuccess;
        setLayout(new GridLayout(4, 2, 5, 5));
        setSize(300, 200);
        setLocationRelativeTo(parent);

        SNameField = new JTextField();
        lastNameField = new JTextField();
        membershipStatusField = new JTextField();
        ageField = new JTextField();

        submitButton = new JButton("Submit");

        add(new JLabel("First Name:"));
        add(SNameField);
        add(new JLabel("Last Name:"));
        add(lastNameField);
        add(new JLabel("Membership Status:"));
        add(membershipStatusField);
        add(new JLabel("Age:"));
        add(ageField);
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(e -> addGolfer());
    }

    private void addGolfer() {
        try {
            String SName = SNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String status = membershipStatusField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());

            String sql = "INSERT INTO Golfers (SName, LastName, MembershipStatus, Age) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, SName);
            stmt.setString(2, lastName);
            stmt.setString(3, status);
            stmt.setInt(4, age);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Golfer added successfully!");
                onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add golfer.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class StaffForm extends JDialog {
    private JTextField SNameField, roleField, salaryField;
    private JButton submitButton;
    private Db db;
    private Runnable onSuccess;

    public StaffForm(JFrame parent, Db db, Runnable onSuccess) {
        super(parent, "Add Staff Member", true);
        this.db = db;
        this.onSuccess = onSuccess;
        setLayout(new GridLayout(4, 2, 5, 5));
        setSize(300, 200);
        setLocationRelativeTo(parent);

        SNameField = new JTextField();
        roleField = new JTextField();
        salaryField = new JTextField();

        submitButton = new JButton("Submit");

        add(new JLabel("Staff Full Name:"));
        add(SNameField);
        add(new JLabel("Role:"));
        add(roleField);
        add(new JLabel("Salary:"));
        add(salaryField);
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(e -> addStaff());
    }

    private void addStaff() {
        try {
            String SName = SNameField.getText().trim();
            String position = roleField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim());

            String sql = "INSERT INTO Staff (SName, Role, Salary) VALUES (?, ?, ?)";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, SName);
            stmt.setString(3, position);
            stmt.setDouble(2, salary);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Staff member added successfully!");
                onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add staff member.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class MenuItemForm extends JDialog {
    private JTextField itemNameField, priceField;
    private JComboBox<String> categoryBox;
    private JButton submitButton;
    private Db db;
    private Runnable onSuccess;

    public MenuItemForm(JFrame parent, Db db, Runnable onSuccess) {
        super(parent, "Add Menu Item", true);
        this.db = db;
        this.onSuccess = onSuccess;
        setLayout(new GridLayout(4, 2, 5, 5));
        setSize(350, 200);
        setLocationRelativeTo(parent);

        itemNameField = new JTextField();
        categoryBox = new JComboBox<>(new String[] {"Entrees", "Drinks", "Snacks", "Breakfast"});
        priceField = new JTextField();
        submitButton = new JButton("Submit");

        add(new JLabel("Item Name:"));
        add(itemNameField);
        add(new JLabel("Category:"));
        add(categoryBox);
        add(new JLabel("Price:"));
        add(priceField);
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(e -> addMenuItem());
    }

    private void addMenuItem() {
        try {
            String itemName = itemNameField.getText().trim();
            String category = categoryBox.getSelectedItem().toString();
            double price = Double.parseDouble(priceField.getText().trim());

            String sql = "INSERT INTO Menu (ItemName, Category, Price) VALUES (?, ?, ?)";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setString(1, itemName);
            stmt.setString(2, category);
            stmt.setDouble(3, price);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Menu item added successfully!");
                onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add menu item.");
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class UpdateReservationForm extends JDialog {
    private JTextField golferIdField, courseIdField, dateField, timeField, costField;
    private JButton submitButton;
    private Db db;
    private int reservationId;
    private Runnable onSuccess;

    public UpdateReservationForm(JFrame parent, Db db, int reservationId, Runnable onSuccess) {
        super(parent, "Update Reservation", true);
        this.db = db;
        this.reservationId = reservationId;
        this.onSuccess = onSuccess;

        setLayout(new GridLayout(6, 2, 5, 5));
        setSize(400, 300);
        setLocationRelativeTo(parent);

        golferIdField = new JTextField();
        courseIdField = new JTextField();
        dateField = new JTextField();   // Format: YYYY-MM-DD
        timeField = new JTextField();   // Format: HH:MM
        costField = new JTextField();   // Example: 59.99
        submitButton = new JButton("Update");

        add(new JLabel("Golfer ID:"));
        add(golferIdField);
        add(new JLabel("Course ID:"));
        add(courseIdField);
        add(new JLabel("Date (YYYY-MM-DD):"));
        add(dateField);
        add(new JLabel("Time (HH:MM):"));
        add(timeField);
        add(new JLabel("Total Cost:"));
        add(costField);
        add(new JLabel());
        add(submitButton);

        loadReservationData();

        submitButton.addActionListener(e -> updateReservation());
    }

    private void loadReservationData() {
        try {
            String sql = "SELECT GolferID, CourseID, Date, Time, TotalCost FROM Reservations WHERE ReservationID = ?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                golferIdField.setText(String.valueOf(rs.getInt("GolferID")));
                courseIdField.setText(String.valueOf(rs.getInt("CourseID")));
                dateField.setText(rs.getString("Date"));
                timeField.setText(rs.getString("Time"));
                costField.setText(String.valueOf(rs.getDouble("TotalCost")));
            } else {
                JOptionPane.showMessageDialog(this, "Reservation not found.", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReservation() {
        try {
            int golferId = Integer.parseInt(golferIdField.getText().trim());
            int courseId = Integer.parseInt(courseIdField.getText().trim());
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            double totalCost = Double.parseDouble(costField.getText().trim());

            String sql = "UPDATE Reservations SET GolferID=?, CourseID=?, Date=?, Time=?, TotalCost=? WHERE ReservationID=?";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, golferId);
            stmt.setInt(2, courseId);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.setDouble(5, totalCost);
            stmt.setInt(6, reservationId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Reservation updated successfully!");
                onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update reservation.");
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
